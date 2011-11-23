/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;

import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.CloseTableRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.GameProcessor;
import com.cubeia.firebase.api.game.TournamentProcessor;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.handler.BackendCallHandler;
import com.cubeia.games.poker.handler.PokerHandler;
import com.cubeia.games.poker.handler.Trigger;
import com.cubeia.games.poker.jmx.PokerStats;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.games.poker.services.HandDebuggerContract;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;


/**
 * Handle incoming actions.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class Processor implements GameProcessor, TournamentProcessor {

	/** Serializer for poker packets */
	private static StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());

    private static Logger log = LoggerFactory.getLogger(Processor.class);

    @Inject @VisibleForTesting
    ActionCache actionCache;

    @Inject @VisibleForTesting
    StateInjector stateInjector;
    
    @Inject @VisibleForTesting
    PokerState state;

    @Inject @VisibleForTesting
	PokerHandler pokerHandler;
    
    @Inject @VisibleForTesting
	BackendCallHandler backendHandler;
    
    @Inject @VisibleForTesting
    TimeoutCache timeoutCache;
    
    @Inject @VisibleForTesting
    TableCloseHandler tableCloseHandler;
    
    @Service @VisibleForTesting
    HandDebuggerContract handDebugger;
    
    
    /**
	 * Handle a wrapped game packet.
	 * Throw the unpacket to the visitor.
	 *  
	 */
	public void handle(GameDataAction action, Table table) {
		stateInjector.injectAdapter(table);
	    ProtocolObject packet = null;
	    
	    if (state.isShutdown()) {
	        log.warn("dropping action for shut down table: {}", table.getId());
	        return;
	    }
	    
		try {
			packet = serializer.unpack(action.getData());
			pokerHandler.setPlayerId(action.getPlayerId());
			packet.accept(pokerHandler);
			PokerStats.getInstance().setState(table.getId(), state.getStateDescription());			
		} catch (Throwable t) {
			log.error("Unhandled error on table", t);
		    tableCloseHandler.handleCrashOnTable(action, table, t);
		}
		
		updatePlayerDebugInfo(table);
	}
    

	/**
	 * Handle a wrapped object. This is typically a scheduled action
	 * (actually, for the poker so far I know it is *only* scheduled actions).
	 * 
	 * I am using an enum for simple commands, the commands has no input parameters.
	 */
	public void handle(GameObjectAction action, Table table) {
		stateInjector.injectAdapter(table);
	    try {
    		Object attachment = action.getAttachment();
            if (attachment instanceof Trigger) {
    			Trigger command = (Trigger) attachment;
    			handleCommand(table, command);
    		} else if (attachment instanceof OpenSessionResponse) {
    	        backendHandler.handleOpenSessionSuccessfulResponse((OpenSessionResponse) attachment);
    	    } else if (attachment instanceof OpenSessionFailedResponse) {
                log.debug("got open session failed response: {}", attachment);
                backendHandler.handleOpenSessionFailedResponse((OpenSessionFailedResponse) attachment);
    	    } else if (attachment instanceof ReserveResponse) {
    	        log.debug("got reserve response: {}", attachment);
    	        backendHandler.handleReserveSuccessfulResponse((ReserveResponse) attachment);
    	    } else if (attachment instanceof ReserveFailedResponse) {
    	    	log.debug("got reserve failed response: {}", attachment);
    	    	backendHandler.handleReserveFailedResponse((ReserveFailedResponse) attachment);
    	    } else if (attachment instanceof AnnounceTableResponse) {
    	        backendHandler.handleAnnounceTableSuccessfulResponse((AnnounceTableResponse) attachment);
            } else if (attachment instanceof AnnounceTableFailedResponse) {
                log.debug("got announce table failed response: {}", attachment);
                backendHandler.handleAnnounceTableFailedResponse((AnnounceTableFailedResponse)attachment);
            } else if (attachment instanceof CloseTableRequest) {
            	log.debug("got close table request: {}", attachment);
                tableCloseHandler.closeTable(table, false);
    	    } else {
    	        log.warn("Unhandled object: " + attachment.getClass().getName());
    	    }
	    } catch (Throwable t) {
	    	log.error("Failed handling game object action.", t);
            tableCloseHandler.handleCrashOnTable(action, table, t);
	    }
	    
        updatePlayerDebugInfo(table);
	}

    private void updatePlayerDebugInfo(Table table) {
        if (handDebugger != null) {
            for (PokerPlayer player : state.getSeatedPlayers()) {
                try {
                    GenericPlayer genericPlayer = table.getPlayerSet().getPlayer(player.getId());
                    handDebugger.updatePlayerInfo(table.getId(), player.getId(), 
                        genericPlayer.getName(), !player.isSittingOut(), player.getBalance(), player.getBetStack());
                } catch (Exception e) {
                    log.error("unable to fill out debug info for player: " + player.getId(), e);
                }
            }
        }
    }
	
	/**
	 * Basic switch and response for command types.
	 * 
	 * @param table
	 * @param command
	 */
	private void handleCommand(Table table, Trigger command) {
	    
	    if (state.isShutdown()) {
	        log.warn("dropping scheduled {} command for shut down table: {}", command.getType(), table.getId());
	        return;
	    }
	    
		switch (command.getType()) {
			case TIMEOUT:
				boolean verified = pokerHandler.verifySequence(command);
				if (verified) {
					state.timeout();
				} else {
					log.warn("Invalid sequence detected");
					tableCloseHandler.printActionsToErrorLog(null, "Timeout command OOB: "+command+" on table: "+table, table);
				}
				break;
			case PLAYER_TIMEOUT:
				handlePlayerTimeoutCommand(table, command);
			    break;
		}
		
		PokerStats.getInstance().setState(table.getId(), state.getStateDescription());
	}

	/**
	 * Verify sequence number before timeout
	 * 
	 * @param table
	 * @param command
	 * @param logic
	 */
	private void handlePlayerTimeoutCommand(Table table, Trigger command) {
		if (pokerHandler.verifySequence(command)) {
		    timeoutCache.removeTimeout(table.getId(), command.getPid(), table.getScheduler());
		    clearRequestSequence(table);
		    state.timeout();
		} 
	}

	
    public void startRound(Table table) {
    	stateInjector.injectAdapter(table);
        if (actionCache != null) {
            actionCache.clear(table.getId());
        }
        log.debug("Start Hand on table: "+table+" ("+table.getPlayerSet().getPlayerCount()+":"+state.getSeatedPlayers().size()+")");
        state.sitOutPlayersMarkedForSitOutNextRound();
        state.startHand();
        
        updatePlayerDebugInfo(table);
    }

    public void stopRound(Table table) {
    	stateInjector.injectAdapter(table);
    }
    
    private void clearRequestSequence(Table table) {
        FirebaseState fbState = (FirebaseState)state.getAdapterState();
        fbState.setCurrentRequestSequence(-1);
    }

}

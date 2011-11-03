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

package com.cubeia.games.poker.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoRequest;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInRequest;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitinRequest;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitoutRequest;

import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse.ErrorCode;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.FirebaseState;
import com.cubeia.games.poker.adapter.ActionTransformer;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.player.SitOutStatus;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

public class PokerHandler extends DefaultPokerHandler {

    private static Logger log = LoggerFactory.getLogger(PokerHandler.class);
    
	public int playerId;
	
	@Inject @VisibleForTesting
	public Table table;
	
	@Inject @VisibleForTesting
	public PokerState state;
	
	@Inject @VisibleForTesting
	public ActionTransformer actionTransformer;

	@Inject @VisibleForTesting
	protected TimeoutCache timeoutCache;
	
	@Service @VisibleForTesting
	CashGamesBackendContract cashGameBackend;
	
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	@Override
	public void visit(PerformAction packet) {
	    if (verifySequence(packet)) {
	    	timeoutCache.removeTimeout(table.getId(), playerId, table.getScheduler());
	        PokerAction action = new PokerAction(playerId, actionTransformer.transform(packet.action.type));
	        action.setBetAmount(packet.betAmount);
	        state.act(action);
	    } 
	}
	
	// player wants to sit out next hand
    @Override
	public void visit(PlayerSitoutRequest packet) {
		state.playerIsSittingOut(playerId, SitOutStatus.SITTING_OUT);
	}

	// player wants to sit in again
    @Override
	public void visit(PlayerSitinRequest packet) {
		state.playerIsSittingIn(playerId);
	}
	
	@Override
	public void visit(BuyInInfoRequest packet) {
	    state.notifyBuyinInfo(playerId, false);
	}

	@Override
	public void visit(BuyInRequest packet) {
        PokerPlayerImpl pokerPlayer = (PokerPlayerImpl) state.getPokerPlayer(playerId);
        ReserveRequest reserveRequest = new ReserveRequest(pokerPlayer.getPlayerSessionId(), -1, packet.amount);
        ReserveCallback callback = cashGameBackend.getCallbackFactory().createReserveCallback(table);
        
        // Check if the amount is allowed by the table
        long sum = reserveRequest.amount + pokerPlayer.getBalance();
		if (sum <= state.getMaxBuyIn()) {
        	cashGameBackend.reserve(reserveRequest, callback);
        } else {
        	ReserveFailedResponse failResponse = new ReserveFailedResponse(reserveRequest.playerSessionId, ErrorCode.AMOUNT_TOO_HIGH, "Requested buy in plus balance cannot be more than max buy in");
			callback.requestFailed(failResponse);
        }
        
        // pokerPlayer.setSitInAfterSuccessfulBuyIn(packet.sitInIfSuccessful);
        pokerPlayer.setSitInAfterSuccessfulBuyIn(true);
	}
	
	/*@Override
	public void visit(InternalSerializedObject packet) {
	    ObjectInputStream objectIn;
	    Object object;
        try {
            objectIn = new ObjectInputStream(new ByteArrayInputStream(packet.bytes));
            object = objectIn.readObject();
        } catch (Exception e) {
            throw new RuntimeException("error deserializing object payload", e);
        } 
	    
	    if (object instanceof OpenSessionResponse) {
	        backendHandler.handleOpenSessionSuccessfulResponse((OpenSessionResponse) object);
	    } else if (object instanceof OpenSessionFailedResponse) {
            log.debug("got open session failed response: {}", object);
	    } else if (object instanceof ReserveResponse) {
	        log.debug("got reserver response: {}", object);
	        backendHandler.handleReserveSuccessfulResponse(playerId, (ReserveResponse) object);
	    } else {
	        log.warn("unhandled object: " + object.getClass().getName());
	    }
	    
	}*/


    private boolean verifySequence(PerformAction packet) {
        FirebaseState fbState = (FirebaseState)state.getAdapterState();
        int current = fbState.getCurrentRequestSequence();
        if (current >= 0 && current == packet.seq) {
            return true;
            
        } else {
            log.debug("Ignoring action. current-seq["+current+"] packet-seq["+packet.seq+"] - packet["+packet+"]");
            return false;
        }
        
    }
    
    public boolean verifySequence(Trigger command) {
        FirebaseState fbState = (FirebaseState)state.getAdapterState();
        int current = fbState.getCurrentRequestSequence();
        if (current == command.getSeq()) {
            return true;
            
        } else {
            log.warn("Ignoring scheduled command, current-seq["+current+"] command-seq["+command.getSeq()+"] - command["+command+"] state["+state+"]");
            return false;
        }
    }

}

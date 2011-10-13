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

import static com.cubeia.poker.player.SitOutStatus.NOT_ENTERED_YET;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.TableIdImpl;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.api.action.UnseatPlayersMttAction.Reason;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.player.PlayerStatus;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TournamentTableListener;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * <p>In this class we are modifying a stored watcher list in the poker state object.
 * The reason for this is that we want to detect if a player that joins the 
 * game was previously a watcher. If the player was a watcher then we don't need to 
 * send the game state again.</p>
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerTableListener implements TournamentTableListener {

	private static Logger log = LoggerFactory.getLogger(PokerTableListener.class);
	
	@Inject
	ActionCache actionCache;

	@Inject
	GameStateSender gameStateSender;
	
	@Deprecated
	@Service
	WalletServiceContract walletService;
	
	@Service
	CashGamesBackendContract cashGameBackend;
	
    @Inject
    StateInjector stateInjector;
	
    @Inject 
    PokerState state;
    
	/**
	 * A Player has joined our table. =)
	 * 
	 */
	public void playerJoined(Table table, GenericPlayer player) {
		stateInjector.injectAdapter(table);
        log.debug("Player["+player.getPlayerId()+":"+player.getName()+"] joined Table["+table.getId()+":"+table.getMetaData().getName()+"]");
	    if (state.isPlayerSeated(player.getPlayerId())) {
	        // rejoin
	    	// TODO Possibly add reconnect event to action cache?
	        sitInPlayer(table, player);
	    } else {
	        addPlayer(table, player, false);
	    }
	}

	/** Check if joined from watching state, only send if not a previous watcher */
	private void checkSendGameState(Table table, GenericPlayer player) {
        if (!state.removeAsWatcher(player.getPlayerId())) {
        	gameStateSender.sendGameState(table, player.getPlayerId());
        }
	}
	
    /**
	 * A Player has left our table. =(
	 * 
	 */
	public void playerLeft(Table table, int playerId) {
		log.debug("Player left: "+playerId);
		stateInjector.injectAdapter(table);
		removePlayer(table, playerId, false);
	}

	
	public void tournamentPlayerJoined(Table table, GenericPlayer player, Serializable balance) {
		stateInjector.injectAdapter(table);
	    PokerPlayer pokerPlayer = addPlayer(table, player, true);
	    pokerPlayer.addChips((Long) balance);
    }

    public void tournamentPlayerRejoined(Table table, GenericPlayer player) {
        // log.debug("Tournament player rejoined: "+player);
        // addPlayer(table, player);
    }

    public void tournamentPlayerRemoved(Table table, int playerId, Reason reason) {
    	stateInjector.injectAdapter(table);
        removePlayer(table, playerId, true);
    }
    
	/**
	 * Send current game state to the watching player
	 */
	public void watcherJoined(Table table, int playerId) {
        log.debug("Player["+playerId+"] watching Table["+table.getId()+":"+table.getMetaData().getName()+"]");
		stateInjector.injectAdapter(table);
		state.addWatcher(playerId);
		gameStateSender.sendGameState(table, playerId);
	}

	public void playerStatusChanged(Table table, int playerId, PlayerStatus status) {
		// TODO On disconnect, add event to action cache?
	}

	public void seatReserved(Table table, GenericPlayer player) {}
	
	public void watcherLeft(Table table, int playerId) {
		state.removeAsWatcher(playerId);
	}
	

	private void sitInPlayer(Table table, GenericPlayer player) {
		checkSendGameState(table, player);
	    state.playerIsSittingIn(player.getPlayerId());
    }
    
	@VisibleForTesting
    protected PokerPlayer addPlayer(Table table, GenericPlayer player, boolean tournamentPlayer) {
		checkSendGameState(table, player);
        PokerPlayer pokerPlayer = new PokerPlayerImpl(player);
        pokerPlayer.setSitOutStatus(NOT_ENTERED_YET);
        state.addPlayer(pokerPlayer);
        
        if (!tournamentPlayer) {
            // TODO: wallet session should not be created here but on buy in request
            
        	log.debug("Start wallet session for player: "+player);
        	startWalletSession(table, player);
        }
        
        return pokerPlayer;
    }
    
	private void startWalletSession(Table table, GenericPlayer player) {
	    log.debug("starting wallet session: tId = {}, pId = {}", table.getId(), player.getPlayerId());
			
		// TODO: TableId must be obtained from announceTable call, may not be instantiated here.
		OpenSessionRequest openSessionRequest = new OpenSessionRequest(player.getPlayerId(), new TableIdImpl(), -1);
        cashGameBackend.openSession(openSessionRequest, cashGameBackend.getCallbackFactory().createOpenSessionCallback(table));
	}

	private void removePlayer(Table table, int playerId, boolean tournamentPlayer) {
        if (!tournamentPlayer) {
        	PokerPlayerImpl pokerPlayer = (PokerPlayerImpl) state.getPokerPlayer(playerId);
            if (pokerPlayer != null) { // Check if player was removed already
            	handleSessionEnd(table, playerId, pokerPlayer);
        	}
        }
        
        state.removePlayer(playerId);
    }
	
    private void handleSessionEnd(Table table, int playerId, PokerPlayerImpl pokerPlayer) {
        PlayerSessionId sessionId = pokerPlayer.getPlayerSessionId();
        
        log.debug("Handle session end for player["+playerId+"], sessionid["+sessionId+"]");
        if (sessionId != null) {
        	CloseSessionRequest closeSessionRequest = new CloseSessionRequest(sessionId, -1);
            try {
                cashGameBackend.closeSession(closeSessionRequest);
            } catch (CloseSessionFailedException e) {
                log.error("error ending wallet session: " + sessionId, e);
            } finally {
                pokerPlayer.clearBalance();
                pokerPlayer.setPlayerSessionId(null);
            }
        }
    }
}

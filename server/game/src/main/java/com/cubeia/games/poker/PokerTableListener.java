package com.cubeia.games.poker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.UnseatPlayersMttAction.Reason;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.player.PlayerStatus;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TournamentTableListener;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.adapter.ActionTransformer;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.io.protocol.StartHandHistory;
import com.cubeia.games.poker.io.protocol.StopHandHistory;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.games.poker.util.WalletAmountConverter;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.inject.Inject;

public class PokerTableListener implements TournamentTableListener {

	private static transient Logger log = Logger.getLogger(PokerTableListener.class);
	
	@Inject
	ActionCache actionCache;

	@Service
	WalletServiceContract walletService;
	
    @Inject
    StateInjector stateInjector;
	
    @Inject 
    PokerState state;
    
	private WalletAmountConverter amountConverter = new WalletAmountConverter();
	
	/**
	 * A Player has joined our table. =)
	 * 
	 */
	public void playerJoined(Table table, GenericPlayer player) {
		stateInjector.injectAdapter(table);
		log.debug("Player["+player.getPlayerId()+":"+player.getName()+"] joined Table["+table.getId()+":"+table.getMetaData().getName()+"]");
	    if (state.isPlayerSeated(player.getPlayerId())) {
	        sitInPlayer(table, player);
	    } else {
	        addPlayer(table, player, false);
	    }
	}
	
    /**
	 * A Player has left our table. =(
	 * 
	 */
	public void playerLeft(Table table, int playerId) {
		log.debug("RMV Player left: "+playerId);
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
		stateInjector.injectAdapter(table);
		sendGameState(table, playerId);
	}

	
	public void playerStatusChanged(Table table, int playerId, PlayerStatus status) {}

	public void seatReserved(Table table, GenericPlayer player) {}
	
	public void watcherLeft(Table table, int playerId) {}
	
	private void sendGameState(Table table, int playerId) {
	    List<GameAction> actions = new LinkedList<GameAction>();
	    actions.add(ProtocolFactory.createGameAction(new StartHandHistory(), playerId, table.getId()));
		actions.addAll(actionCache.getActions(table.getId()));
		actions.add(ProtocolFactory.createGameAction(new StopHandHistory(), playerId, table.getId()));
		table.getNotifier().notifyPlayer(playerId, actions);
	}

	private void sendTableBalance(PokerState state, Table table, int playerId) {
        int balance = state.getBalance(playerId);
		GameDataAction balanceAction = ActionTransformer.createPlayerBalanceAction(balance, playerId, table.getId());
		table.getNotifier().notifyAllPlayers(balanceAction);
	}
	
	private void sitInPlayer(Table table, GenericPlayer player) {
	    sendGameState(table, player.getPlayerId());
	    state.playerIsSittingIn(player.getPlayerId());
	    
    }
    
    private PokerPlayer addPlayer(Table table, GenericPlayer player, boolean tournamentPlayer) {
        sendGameState(table, player.getPlayerId());

        PokerPlayer pokerPlayer = new PokerPlayerImpl(player);
        state.addPlayer(pokerPlayer);
        
        if (!tournamentPlayer) {
        	log.debug("Start wallet session for player: "+player);
	        Long sessionId = startWalletSession(table, player);
	        ((PokerPlayerImpl) pokerPlayer).setSessionId(sessionId);
	        
	        // TODO: handle wallet error!
	        
	        if (sessionId != null) {
	        	// TODO: amount is hardcoded, user should give the amount
	        	int amount = 1000;
	        	 withdraw(amount, sessionId, table.getId());
	        	state.addChips(player.getPlayerId(), amount);
	        }
        }
        
        sendTableBalance(state, table, player.getPlayerId());
        return pokerPlayer;
    }
    
	private Long startWalletSession(Table table, GenericPlayer player) {
		Long sessionId = walletService.startSession(
			PokerGame.LICENSEE_ID,
			player.getPlayerId(), 
			table.getId(), 
			PokerGame.POKER_GAME_ID, 
			player.getName());
		
		log.debug("Get session ID for player["+player+"]: "+sessionId);
		
		if (sessionId == null) {
			log.error("error opening wallet session. Table["+table.getId()+"] player["+player+"]");
			return null;
		} else {
			return sessionId;
		}
	}

	private boolean endWalletSession(Table table, GenericPlayer player, long sessionId) {
		log.debug("RMV CLOSE SESSION: "+sessionId);
		walletService.endSession(sessionId);
		return true;
	}
	
	private void withdraw(int amount, long sessionId, int tableId) {
		walletService.withdraw(amountConverter.convertToWalletAmount(amount), PokerGame.LICENSEE_ID, sessionId, "To poker table["+tableId+"]");
	}
	
	private void deposit(int amount, long sessionId, int tableId) {
		log.debug("RMV DEPOSIT BACK: "+amount+", "+sessionId+", "+tableId);
		walletService.deposit(amountConverter.convertToWalletAmount(amount), PokerGame.LICENSEE_ID, sessionId, "From poker table["+tableId+"]");
	}

	private void removePlayer(Table table, int playerId, boolean tournamentPlayer) {
		log.debug("RMV Remove Player: "+playerId);
        if (!tournamentPlayer) {
        	PokerPlayerImpl pokerPlayer = (PokerPlayerImpl) state.getPokerPlayer(playerId);
            if (pokerPlayer != null) { // Check if player was removed already
            	handleSessionEnd(table, playerId, pokerPlayer);
        	}
        }
        
        state.removePlayer(playerId);
    }
	
    private void handleSessionEnd(Table table, int playerId, PokerPlayerImpl pokerPlayer) {
        Long sessionId = pokerPlayer.getSessionId();
        
        log.debug("RMV Handle Session end for player["+playerId+"] with sessionid["+sessionId+"]");
        if (sessionId != null) {
        	long balance = pokerPlayer.getBalance();
        	deposit((int) balance, sessionId, table.getId());
        	// TODO: Add check that depositedAmount-balance is 0
        	pokerPlayer.clearBalance();
        	
        	GenericPlayer player = table.getPlayerSet().getPlayer(playerId);
        	boolean endSessionOk = endWalletSession(table, player, sessionId);
        	if (endSessionOk) {
        		pokerPlayer.setSessionId(null);
        	} else {
        		// TODO: how do we handle this???
        		log.error("error ending wallet session");
        	}
        }
    }
}

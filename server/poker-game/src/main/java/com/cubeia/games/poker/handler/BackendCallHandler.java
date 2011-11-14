package com.cubeia.games.poker.handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.BuyInResponse;
import se.jadestone.dicearena.game.poker.network.protocol.Enums;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.lobby.PokerLobbyAttributes;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.inject.Inject;

public class BackendCallHandler {
    public static final String EXT_PROP_KEY_TABLE_ID = "tableId";

    private static Logger log = LoggerFactory.getLogger(BackendCallHandler.class);
    
    @Inject
    private final PokerState state;
    
    @Inject
    private final Table table;

    private StyxSerializer styx = new StyxSerializer(null);
    
    @Inject
    public BackendCallHandler(PokerState state, Table table) {
        this.state = state;
        this.table = table;
    }
    
    public void handleReserveSuccessfulResponse(ReserveResponse reserveResponse) {
    	int playerId = reserveResponse.getPlayerSessionId().getPlayerId();
        PokerPlayer pokerPlayer = state.getPokerPlayer(playerId);
        int amountReserved = reserveResponse.amountReserved;
		log.debug("handle reserve response: session = {}, amount = {}, pId = {}", 
            new Object[] {reserveResponse.getPlayerSessionId(), amountReserved, pokerPlayer.getId()});
        
        log.debug("player is in hand, adding reserved amount {} as pending", amountReserved);
        pokerPlayer.addPendingAmount(amountReserved);
        
        // TODO: response should move to PokerHandler.handleReserveResponse
        BuyInResponse resp = new BuyInResponse();
        resp.balance = (int) pokerPlayer.getBalance();
        resp.pendingBalance = (int) pokerPlayer.getPendingBalance();
        resp.resultCode = Enums.BuyInResultCode.OK;
        
        sendGameData(playerId, resp);
        
        if (pokerPlayer.isSitInAfterSuccessfulBuyIn()) {
            state.playerIsSittingIn(playerId);
        }
        
        state.notifyPlayerBalance(playerId);
    }
    
    public void handleReserveFailedResponse(ReserveFailedResponse response) {
    	int playerId = response.sessionId.getPlayerId();
    	
    	BuyInResponse resp = new BuyInResponse();
        resp.resultCode = Enums.BuyInResultCode.UNSPECIFIED_ERROR; 
        
        switch (response.errorCode) {
	        case AMOUNT_TOO_HIGH: 
	        	resp.resultCode = Enums.BuyInResultCode.AMOUNT_TOO_HIGH;
	        	break;
	        	
	        case MAX_LIMIT_REACHED: 
	        	resp.resultCode = Enums.BuyInResultCode.MAX_LIMIT_REACHED;
	        	break;
	        	
	        case SESSION_NOT_OPEN: 
	        	resp.resultCode = Enums.BuyInResultCode.SESSION_NOT_OPEN;
	        	break;
	        	
	        case UNSPECIFIED_FAILURE: 
	        	resp.resultCode = Enums.BuyInResultCode.UNSPECIFIED_ERROR;
	        	break;
        }
        
    	
		sendGameData(playerId, resp);
	}

    public void handleOpenSessionSuccessfulResponse(OpenSessionResponse openSessionResponse) {
        PlayerSessionId playerSessionId = openSessionResponse.sessionId;
        int playerId = playerSessionId.getPlayerId();
        // log.debug("handle open session response: session = {}, pId = {}", playerSessionId, playerId);
        PokerPlayerImpl pokerPlayer = (PokerPlayerImpl) state.getPokerPlayer(playerId);
        pokerPlayer.setPlayerSessionId(playerSessionId);
    }

    public void handleAnnounceTableSuccessfulResponse(AnnounceTableResponse attachment) {
        //log.debug("handle announce table success, tId = {}, intTableId = {}, tableProperties = {}", new Object[] { Integer.valueOf(table.getId()), attachment.tableId, attachment.tableProperties });
        Map<String, Serializable> extProps = state.getExternalTableProperties();
        extProps.put(EXT_PROP_KEY_TABLE_ID, attachment.tableId);
        extProps.putAll(attachment.tableProperties);
        makeTableVisibleInLobby(table);
    }

    private void makeTableVisibleInLobby(Table table) {
        //log.debug("setting table {} as visible in lobby", table.getId());
        table.getAttributeAccessor().setIntAttribute("VISIBLE_IN_LOBBY", 1);
    }

    /**
	 * This table has not been approved by 3rd party (e.g. Italian government). 
	 * We need to close it asap.
	 * 
	 * @param attachment
	 */
    public void handleAnnounceTableFailedResponse(AnnounceTableFailedResponse attachment) {
		log.info("Handle Announce Table Failed for table["+table.getId()+"], will flag for removal");
		LobbyTableAttributeAccessor attributeAccessor = table.getAttributeAccessor();
		attributeAccessor.setIntAttribute(PokerLobbyAttributes.TABLE_READY_FOR_CLOSE.name(), 1);
    }

    public void handleOpenSessionFailedResponse(OpenSessionFailedResponse response) {
    	log.info("Handle Open Session Failed on table["+table.getId()+"]: "+response);
        
    	// Send message to player
    	BuyInResponse resp = new BuyInResponse();
        resp.resultCode = Enums.BuyInResultCode.SESSION_NOT_OPEN;
        sendGameData(response.playerId, resp);
    	
    	// Unseat player & set as watcher
    	state.unseatPlayer(response.playerId, true);
    	
    }

    private void sendGameData(int playerId, ProtocolObject resp) {
		GameDataAction action = new GameDataAction(playerId, table.getId());
        try {
			action.setData(styx.pack(resp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		table.getNotifier().notifyPlayer(playerId, action );
	}
    
}

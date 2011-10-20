package com.cubeia.games.poker.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.BuyInResponse;
import se.jadestone.dicearena.game.poker.network.protocol.Enums;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.inject.Inject;

public class BackendCallHandler {
    private static Logger log = LoggerFactory.getLogger(BackendCallHandler.class);
    
    @Inject
    private final PokerState state;
    
    @Inject
    private final Table table;

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
        
        if (state.isPlayerInHand(playerId)) {
            log.debug("player is in hand, adding reserved amount {} as pending", amountReserved);
            pokerPlayer.addPendingAmount(amountReserved);
        } else {
            log.debug("player is not in hand, adding reserved amount {} to balance", amountReserved);
            pokerPlayer.addChips(amountReserved);
        }
        
        // TODO: response should move to PokerHandler.handleReserveResponse
        BuyInResponse resp = new BuyInResponse();
        resp.balance = (int) pokerPlayer.getBalance();
        resp.pendingBalance = (int) pokerPlayer.getPendingBalance();
        resp.resultCode = Enums.BuyInResultCode.OK;
        
        GameDataAction gda = new GameDataAction(playerId, table.getId());
        StyxSerializer styx = new StyxSerializer(null);
        try {
            gda.setData(styx.pack(resp));
        } catch (IOException e) {
            e.printStackTrace();
        }
  
        table.getNotifier().notifyPlayer(playerId, gda);
        
        if (pokerPlayer.isSitInAfterSuccessfulBuyIn()) {
            state.playerIsSittingIn(playerId);
        }
        
        state.notifyPlayerBalance(playerId);
    }

    public void handleOpenSessionSuccessfulResponse(OpenSessionResponse openSessionResponse) {
        PlayerSessionId playerSessionId = openSessionResponse.sessionId;
        int playerId = playerSessionId.getPlayerId();
        log.debug("handle open session response: session = {}, pId = {}", playerSessionId, playerId);
        
        PokerPlayerImpl pokerPlayer = (PokerPlayerImpl) state.getPokerPlayer(playerId);
        pokerPlayer.setPlayerSessionId(playerSessionId);
    }
    
}

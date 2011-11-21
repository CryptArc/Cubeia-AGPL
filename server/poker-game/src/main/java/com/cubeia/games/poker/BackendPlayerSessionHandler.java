package com.cubeia.games.poker;

import static com.cubeia.games.poker.handler.BackendCallHandler.EXT_PROP_KEY_TABLE_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.adapter.FirebaseServerAdapter;
import com.cubeia.games.poker.lobby.PokerLobbyAttributes;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

public class BackendPlayerSessionHandler {
    private static Logger log = LoggerFactory.getLogger(BackendPlayerSessionHandler.class);

    @Service @VisibleForTesting
    protected CashGamesBackendContract cashGameBackend;
    
    private final PokerState state;
    
    @Inject
    public BackendPlayerSessionHandler(PokerState state) {
        this.state = state;
    }
    

    public void endPlayerSessionInBackend(Table table, PokerPlayer pokerPlayer, int roundNumber) {
        if (!(pokerPlayer instanceof PokerPlayerImpl)) {
            throw new IllegalStateException("must be a PokerPlayerImpl");
        }
        
        PokerPlayerImpl pokerPlayerImpl = (PokerPlayerImpl) pokerPlayer;
        
        PlayerSessionId sessionId = pokerPlayerImpl.getPlayerSessionId();
        
        log.debug("Handle session end for player[" + pokerPlayer.getId() + "], sessionid["+sessionId+"]");
        if (sessionId != null) {
            // TODO: table round number is mocked!
            CloseSessionRequest closeSessionRequest = new CloseSessionRequest(sessionId, roundNumber);
            try {
                cashGameBackend.closeSession(closeSessionRequest);
            } catch (CloseSessionFailedException e) {
                log.error("error ending wallet session: " + sessionId, e);
            } finally {
                pokerPlayer.clearBalance();
                pokerPlayerImpl.setPlayerSessionId(null);
            }
        }
    }

    public void startWalletSession(PokerState state, Table table, int playerId, int roundNumber) {
        log.debug("starting wallet session: tId = {}, pId = {}", table.getId(), playerId);
        TableId tableId = (TableId) state.getExternalTableProperties().get(EXT_PROP_KEY_TABLE_ID);
        if (tableId == null) {
            log.error("No table ID found in external properties; Table must be anounced first; tId = {}", table.getId());
            log.debug("destroying table "+table.getId());
            handleCrashOnTable(table);
        }else{
            OpenSessionRequest openSessionRequest = new OpenSessionRequest(playerId, tableId, roundNumber);
            cashGameBackend.openSession(openSessionRequest, cashGameBackend.getCallbackFactory().createOpenSessionCallback(table));
        }
        
    }
    
    public void handleCrashOnTable(Table table) {
        log.info("handling crashed table id = {}, hand id = {}", table.getId());
        
        // 1. stop table from accepting actions
        //    Gotcha: client actions should not be accepted but callbacks from backend should.
        state.shutdown();
        
        // 2. set table to invisible in lobby
        makeTableInvisibleInLobby(table);
        
        // 3. mark table as closed and let the activator take care of destroying it
        markTableReadyForClose(table);
    }
    
    private void makeTableInvisibleInLobby(Table table) {
        log.debug("setting table {} as invisible in lobby", table.getId());
        table.getAttributeAccessor().setIntAttribute(PokerLobbyAttributes.VISIBLE_IN_LOBBY.name(), 0);
    }
    
    private void markTableReadyForClose(Table table) {
        table.getAttributeAccessor().setAttribute(PokerLobbyAttributes.TABLE_READY_FOR_CLOSE.name(), new AttributeValue(1));
    }
}

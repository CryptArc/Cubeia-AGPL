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
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.common.annotations.VisibleForTesting;

public class BackendPlayerSessionHandler {
    private static Logger log = LoggerFactory.getLogger(BackendPlayerSessionHandler.class);

    @Service @VisibleForTesting
    protected CashGamesBackendContract cashGameBackend;

    public void endPlayerSessionInBackend(Table table, PokerPlayer pokerPlayer) {
        if (!(pokerPlayer instanceof PokerPlayerImpl)) {
            throw new IllegalStateException("must be a PokerPlayerImpl");
        }
        
        PokerPlayerImpl pokerPlayerImpl = (PokerPlayerImpl) pokerPlayer;
        
        PlayerSessionId sessionId = pokerPlayerImpl.getPlayerSessionId();
        
        log.debug("Handle session end for player[" + pokerPlayer.getId() + "], sessionid["+sessionId+"]");
        if (sessionId != null) {
            // TODO: table round number is mocked!
            CloseSessionRequest closeSessionRequest = new CloseSessionRequest(sessionId, -1);
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

    public void startWalletSession(PokerState state, Table table, int playerId) {
        log.debug("starting wallet session: tId = {}, pId = {}", table.getId(), playerId);
        TableId tableId = (TableId) state.getExternalTableProperties().get(EXT_PROP_KEY_TABLE_ID);
        if (tableId == null) {
            log.error("No table ID found in external properties; Table must be anounced first; tId = {}", table.getId());
            throw new IllegalStateException("cannot create table session if table is not announced yet, tableId = " + table.getId());
        }
        // TODO: table round number is mocked!
        OpenSessionRequest openSessionRequest = new OpenSessionRequest(playerId, tableId, -1);
        cashGameBackend.openSession(openSessionRequest, cashGameBackend.getCallbackFactory().createOpenSessionCallback(table));
    }


    
}

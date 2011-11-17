package com.cubeia.backend.firebase.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.service.ServiceRouter;

public class FirebaseCallbackFactoryImpl implements FirebaseCallbackFactory {

    private static Logger log = LoggerFactory.getLogger(FirebaseCallbackFactoryImpl.class);
    
    private final ServiceRouter router;

    public FirebaseCallbackFactoryImpl(ServiceRouter router) {
        this.router = router;
    }
    
    @Override
    public OpenSessionCallback createOpenSessionCallback(final Table table) {
        OpenSessionCallback callback = new OpenSessionCallback() {
            @Override
            public void requestSucceded(OpenSessionResponse response) {
                log.debug("open session succeeded: gId = {}, tId = {}, sId = {}", 
                    new Object[] {table.getMetaData().getGameId(), table.getId(), response.sessionId});
                sendGameObjectActionToTable(response.sessionId.getPlayerId(), table.getMetaData().getGameId(), table.getId(), response);
            }
            
            @Override
            public void requestFailed(OpenSessionFailedResponse response) {
                log.debug("open session failed: gId = {}, tId = {}, error = {}, msg = {}", 
                    new Object[] {table.getMetaData().getGameId(), table.getId(), response.errorCode, response.message});
                sendGameObjectActionToTable(-1, table.getMetaData().getGameId(), table.getId(), response);
            }
        };
        return callback;
    }
    
    @Override
    public ReserveCallback createReserveCallback(final Table table) {
        ReserveCallback callback = new ReserveCallback() {
            @Override
            public void requestSucceded(ReserveResponse response) {
                log.debug("reserve succeeded: gId = {}, tId = {}, sId = {}", 
                    new Object[] {table.getMetaData().getGameId(), table.getId(), response.getPlayerSessionId()});
                sendGameObjectActionToTable(response.getPlayerSessionId().getPlayerId(), table.getMetaData().getGameId(), table.getId(), response);
            }
            
            @Override
            public void requestFailed(ReserveFailedResponse response) {
                log.debug("reserve failed: gId = {}, tId = {}, error = {}, msg = {}", 
                    new Object[] {table.getMetaData().getGameId(), table.getId(), response.errorCode, response.message});
                sendGameObjectActionToTable(response.sessionId.getPlayerId(), table.getMetaData().getGameId(), table.getId(), response);
            }
        };
        return callback;
    }
    
    @Override
    public AnnounceTableCallback createAnnounceTableCallback(final Table table) {
    	AnnounceTableCallback callback = new AnnounceTableCallback() {
            @Override
            public void requestSucceded(final AnnounceTableResponse response) {
//                log.debug("announce suceeded: gId = {}, tId = {}, response = {}",
//                    new Object[] {table.getMetaData().getGameId(), table.getId(), response });
                sendGameObjectActionToTable(-1, table.getMetaData().getGameId(), table.getId(), response);
            }
            
            @Override
            public void requestFailed(AnnounceTableFailedResponse response) {
                log.debug("announce failed: gId = {}, tId = {}, error = {}, msg = {}", 
                    new Object[] {table.getMetaData().getGameId(), table.getId(), response.errorCode, response.message});
                sendGameObjectActionToTable(-1, table.getMetaData().getGameId(), table.getId(), response);
            }
        };
        return callback;
    }
    
    private void sendGameObjectActionToTable(int playerId, int gameId, int tableId, Object object) {
        GameObjectAction action = new GameObjectAction(tableId);
        action.setAttachment(object);
        router.dispatchToGame(gameId, action);
    }
}

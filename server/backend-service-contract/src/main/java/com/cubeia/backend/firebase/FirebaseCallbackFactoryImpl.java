package com.cubeia.backend.firebase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.InternalSerializedObject;

import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.firebase.io.StyxSerializer;

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
                log.debug("open session succeded: gId = {}, tId = {}, sId = {}", 
                    new Object[] {table.getMetaData().getGameId(), table.getId(), response.sessionId});
                sendGameDataActionToTable(response.sessionId.getPlayerId(), table.getMetaData().getGameId(), table.getId(), response);
            }
            
            @Override
            public void requestFailed(OpenSessionFailedResponse response) {
                log.debug("open session failed: gId = {}, tId = {}, error = {}, msg = {}", 
                    new Object[] {table.getMetaData().getGameId(), table.getId(), response.errorCode, response.message});
                sendGameDataActionToTable(-1, table.getMetaData().getGameId(), table.getId(), response);
            }
        };
        return callback;
    }
    
    private void sendGameDataActionToTable(int playerId, int gameId, int tableId, Object object) {
        GameDataAction action = new GameDataAction(playerId, tableId);
        
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut;
        try {
            objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(object);
            InternalSerializedObject internalSerializedObject = new InternalSerializedObject(byteOut.toByteArray());
            StyxSerializer styx = new StyxSerializer(null);
            ByteBuffer pack = styx.pack(internalSerializedObject);
            action.setData(pack);
        } catch (Throwable t) {
            log.error("error serializing object", t);
            throw new RuntimeException("error serializing object", t);
        }
        
        router.dispatchToGame(gameId, action);
    }
    
}

package com.cubeia.backend.firebase;

import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.service.ServiceRouter;

public class FirebaseCallbackFactoryImpl implements FirebaseCallbackFactory {

    private final ServiceRouter router;

    protected FirebaseCallbackFactoryImpl(ServiceRouter router) {
        this.router = router;
    }
    
    @Override
    public OpenSessionCallback createOpenSessionCallback(final Table table) {
        OpenSessionCallback callback = new OpenSessionCallback() {
            @Override
            public void requestSucceded(OpenSessionResponse response) {
                sendToTable(table.getMetaData().getGameId(), table.getId(), response);
            }
            
            @Override
            public void requestFailed(OpenSessionFailedResponse response) {
                sendToTable(table.getMetaData().getGameId(), table.getId(), response);
            }
        };
        return callback;
    }
    
    private void sendToTable(int gameId, int tableId, Object object) {
        GameObjectAction action = new GameObjectAction(tableId);
        action.setAttachment(object);
        router.dispatchToGame(gameId, action);
    }
    
}

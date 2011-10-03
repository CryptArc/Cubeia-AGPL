package com.cubeia.backend.firebase;

import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.firebase.api.game.table.Table;

public interface FirebaseCallbackFactory {
    public OpenSessionCallback createOpenSessionCallback(final Table table);
}

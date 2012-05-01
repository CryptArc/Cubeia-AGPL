package com.cubeia.backend.firebase;

import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.firebase.api.game.table.Table;

public interface FirebaseCallbackFactory {
    public OpenSessionCallback createOpenSessionCallback(Table table);

    public ReserveCallback createReserveCallback(Table table);

    public AnnounceTableCallback createAnnounceTableCallback(Table table);
}

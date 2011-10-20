package com.cubeia.backend.firebase;

import com.cubeia.backend.cashgame.CashGamesBackend;
import com.cubeia.firebase.api.service.Contract;

public interface CashGamesBackendContract extends CashGamesBackend, Contract {

    public FirebaseCallbackFactory getCallbackFactory();
}

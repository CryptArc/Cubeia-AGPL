package com.cubeia.backend.firebase;

import com.cubeia.backend.cashgame.CashGamesBackend;
import com.cubeia.firebase.api.service.Contract;

public interface CashGamesBackendContract extends CashGamesBackend, Contract {

    public static final String MARKET_TABLE_REFERENCE_KEY = "MARKET_TABLE_REFERENCE";

    public static final String MARKET_TABLE_SESSION_REFERENCE_KEY = "MARKET_TABLE_SESSION_REFERENCE";
    
    public FirebaseCallbackFactory getCallbackFactory();
}

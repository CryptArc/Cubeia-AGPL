package com.cubeia.games.poker.cep;

import com.cubeia.firebase.api.service.Contract;
import com.cubeia.poker.player.PokerPlayer;

/**
 * Facade and wrapper for the Cubeia CEP Service.
 * 
 * This interface will allow contextual notifications that are
 * not tied to any CEP specific classes or domain objects (dependency isolation).
 * 
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public interface PokerCEPService extends Contract {

    void reportHandResult(int tableId, PokerPlayer p, long amount);
    
    public void reportHandEnd(int tableId, EventMontaryType monetaryType);
}

package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

import java.util.List;

public interface FutureActionsCalculator {

    /**
     * Calculates what a player can do in the future given that the state does not change.
     * i.e. the "check next" and "fold next" check boxes.
     *
     * @param player
     * @return
     */
    public abstract List<PokerActionType> calculateFutureActionOptionList(PokerPlayer player, Long highestBet);
}
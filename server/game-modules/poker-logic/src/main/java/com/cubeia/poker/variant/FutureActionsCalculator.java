package com.cubeia.poker.variant;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

import java.util.List;

public interface FutureActionsCalculator {

    /**
     * Calculate what a player can do in the future given that the state does not change.
     * i.e. the check next and fold next checkboxes
     *
     * @param player
     * @return
     */
    public abstract List<PokerActionType> calculateFutureActionOptionList(
            PokerPlayer player, Long highestBet);

    public abstract List<PokerActionType> getEmptyFutureOptionList();

}
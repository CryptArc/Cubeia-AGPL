package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.betting.FutureActionsCalculator;

import java.util.ArrayList;
import java.util.List;

public class TelesinaFutureActionsCalculator implements FutureActionsCalculator {


    /* (non-Javadoc)
     * @see com.cubeia.poker.variant.texasholdem.FutureActionsCalculator#calculateFutureActionOptionList(com.cubeia.poker.player.PokerPlayer, java.lang.Long)
     */
    @Override
    public List<PokerActionType> calculateFutureActionOptionList(PokerPlayer player, Long highestBet) {
        List<PokerActionType> options = new ArrayList<PokerActionType>();

        // players that are all in or has folded should not have anything
        if (player.hasFolded() || player.isAllIn() || player.isSittingOut()) {
            return options;
        }

        // in telesina if you have ever acted then you will never be able to check
        if (player.getBetStack() >= highestBet && !player.hasActed()) {
            options.add(PokerActionType.CHECK);
        }

        options.add(PokerActionType.FOLD);


        return options;
    }
}

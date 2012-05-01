package com.cubeia.game.poker.bot;

import com.cubeia.games.poker.io.protocol.Enums.ActionType;
import com.cubeia.games.poker.io.protocol.PlayerAction;

import java.util.List;
import java.util.Random;

public class Strategy {

    private static Random rng = new Random();

    public static PlayerAction getAction(List<PlayerAction> allowedActions) {

//    	List<PlayerAction> allowedActions = new ArrayList<PlayerAction>();
//    	for (PlayerAction action : allowedActionsX) {
//    		if (!action.type.equals(ActionType.RAISE)) {
//    			allowedActions.add(action);
//    		}
//    	}

        // Always post blinds
        for (PlayerAction action : allowedActions) {
            switch (action.type) {
                case BIG_BLIND:
                    return action;

                case SMALL_BLIND:
                    return action;

                case ANTE:
                    return action;
            }
        }

        int optionCount = allowedActions.size();
        int optionIndex = rng.nextInt(optionCount);
        PlayerAction playerAction = allowedActions.get(optionIndex);

        if (playerAction.type == ActionType.FOLD) {
            // We need to downplay fold
            if (rng.nextBoolean()) return getAction(allowedActions);

        }

        return playerAction;
    }


    /**
     * @param allowedActions
     * @return true if the returned action should use an arbitrary delay.
     */
    public static boolean useDelay(List<PlayerAction> allowedActions) {
        for (PlayerAction action : allowedActions) {
            switch (action.type) {
                case BIG_BLIND:
                    return false;

                case SMALL_BLIND:
                    return false;

                case ANTE:
                    return false;

            }
        }
        return true;
    }


}

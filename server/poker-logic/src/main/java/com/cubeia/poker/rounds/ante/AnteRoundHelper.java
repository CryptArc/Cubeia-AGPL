package com.cubeia.poker.rounds.ante;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import com.cubeia.poker.GameType;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.util.PokerUtils;

/**
 * Testable helper class for the ante round.
 * @author w
 */
public class AnteRoundHelper {

    /**
     * Returns true if all players has acted.
     * @param players players
     * @return true if all players has acted
     */
    boolean hasAllPlayersActed(Collection<PokerPlayer> players) {
    	boolean allActed = true;
    	for (PokerPlayer player : players) {
    		allActed = allActed  &&  player.hasActed();
    	}
    	
    	return allActed;
    }

    /**
     * Returns the next player to act.
     * @param lastActedSeatId seat id of last acted player
     * @param seatingMap seating map
     * @return next player to act or null if all players has acted
     */
    PokerPlayer getNextPlayerToAct(int lastActedSeatId, SortedMap<Integer, PokerPlayer> seatingMap) {
    	PokerPlayer next = null;
    
    	List<PokerPlayer> players = PokerUtils.unwrapList(seatingMap, lastActedSeatId + 1);
    	for (PokerPlayer player : players) {
    		if (canPlayerAct(player)) {
    			next = player;
    			break;
    		}
    	}
    	return next;
    }

    /**
     * Test if a placer can act. A player can act if all of the following are true:
     * - has not folded
     * - has not acted
     * - is not sitting out
     * - is not all in
     * @param player player to check
     * @return true if player can act
     */
    boolean canPlayerAct(PokerPlayer player) {
        return !player.hasFolded() && !player.hasActed() && !player.isSittingOut() && !player.isAllIn();
    }

    /**
     * Setup the given player's current action request as an ante request and send it via the game.
     * @param player player
     * @param anteLevel ante level
     * @param game the game
     */
    void requestAnte(PokerPlayer player, int anteLevel, GameType game) {
    	player.enableOption(new PossibleAction(PokerActionType.ANTE, anteLevel));
    	player.enableOption(new PossibleAction(PokerActionType.DECLINE_ENTRY_BET));
    	game.requestAction(player.getActionRequest());
    }

}

package com.cubeia.poker.rounds.betting;

import java.util.List;
import java.util.SortedMap;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;

public interface PlayerToActCalculator {

    /**
     * Returns the first player in the round to act.
     * @param dealerButtonSeatId dealer button position
     * @param seatingMap seating
     * @return player to act
     */
    PokerPlayer getFirstPlayerToAct(int dealerButtonSeatId, SortedMap<Integer, PokerPlayer> seatingMap, List<Card> communityCards);
    
    /**
     * Returns the next player in the round to act.
     * @param lastActedSeatId seat of the player that acted previously
     * @param seatingMap seating
     * @return player to act
     */
    PokerPlayer getNextPlayerToAct(int lastActedSeatId, SortedMap<Integer, PokerPlayer> seatingMap);
    
}

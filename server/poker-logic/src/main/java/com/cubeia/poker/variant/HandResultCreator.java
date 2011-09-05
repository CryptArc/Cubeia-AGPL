package com.cubeia.poker.variant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.model.PlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.util.HandResultCalculator;

public class HandResultCreator {

    public HandResult createHandResult(List<Card> communityCards, HandResultCalculator handResultCalculator, PotHolder potHolder, Map<Integer, PokerPlayer> currentHandPlayerMap) {
        Collection<PlayerHand> playerHands = createHandHolder(communityCards, currentHandPlayerMap.values());
        Map<PokerPlayer, Result> playerResults = handResultCalculator.getPlayerResults(playerHands, potHolder, currentHandPlayerMap);
        return new HandResult(playerResults, playerHands);
    }

    private Collection<PlayerHand> createHandHolder(List<Card> communityCards, Collection<PokerPlayer> players) {
        ArrayList<PlayerHand> playerHands = new ArrayList<PlayerHand>();
        
        for (PokerPlayer player : players) {
            if (!player.hasFolded()) {
                Hand h = new Hand();
                h.addCards(player.getPocketCards().getCards());
                h.addCards(communityCards);
                playerHands.add(new PlayerHand(player.getId(), h));
            }
        }

        return playerHands;
    }

    
}

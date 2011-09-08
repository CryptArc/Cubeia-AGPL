package com.cubeia.poker.rounds.betting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.PokerEvaluator;
import com.cubeia.poker.model.PlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.util.PokerUtils;

/**
 * In Telesina the first player to act is the one showing the best hand formed from
 * the public cards (including the vela in the last betting round).
 * 
 * This implementation must be re-constructed for each round as it needs fresh information
 * about all players public cards and the vela card.
 * 
 * @author w
 */
public class TelesinaPlayerToActCalculator implements PlayerToActCalculator {
    private Logger log = LoggerFactory.getLogger(getClass());
    
    private final PokerEvaluator evaluator;

    /**
     * TODO: use Telesina evaluator here!
     * @param evaluator
     */
    public TelesinaPlayerToActCalculator(PokerEvaluator evaluator) {
        this.evaluator = evaluator;
    }
    
    @Override
    public PokerPlayer getFirstPlayerToAct(int dealerButtonSeatId, SortedMap<Integer, PokerPlayer> seatingMap) {
        Collection<PlayerHand> publicHands = new ArrayList<PlayerHand>();
        
        for (PokerPlayer player : seatingMap.values()) {
            publicHands.add(new PlayerHand(player.getId(), new Hand(player.getPublicPocketCards())));
        }
        
        PlayerHand bestHand = evaluator.rankHands(publicHands).get(0);
        log.debug("first player to act is {} with hand {}: {}", 
            new Object[] {bestHand.getPlayerId(), bestHand.getHand(), bestHand.getHand().getHandStrength()});
        Integer firstPlayerId = bestHand.getPlayerId();
        return findPlayerById(firstPlayerId, seatingMap.values());
    }

    private PokerPlayer findPlayerById(int playerId, Collection<PokerPlayer> players) {
        for (PokerPlayer player : players){
            if (playerId == player.getId()) {
                return player;
            }
        }
        
        throw new IllegalStateException("player not found");
    }
    
    @Override
    public PokerPlayer getNextPlayerToAct(int lastActedSeatId, SortedMap<Integer, PokerPlayer> seatingMap) {
        PokerPlayer next = null;

        List<PokerPlayer> players = PokerUtils.unwrapList(seatingMap, lastActedSeatId + 1);
        for (PokerPlayer player : players) {
            if (!player.hasFolded() && !player.hasActed() && !player.isSittingOut() && !player.isAllIn()) {
                next = player;
                break;
            }
        }
        return next;
    }
}

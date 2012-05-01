package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.util.PokerUtils;
import com.cubeia.poker.variant.telesina.hand.TelesinaHandStrengthEvaluator;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

/**
 * In Telesina the first player to act is the one showing the best hand formed from
 * the public cards (including the vela in the last betting round).
 * <p/>
 * This implementation must be re-constructed for each round as it needs fresh information
 * about all players public cards and the vela card.
 *
 * @author w
 */
public class TelesinaPlayerToActCalculator implements PlayerToActCalculator {

    private TelesinaHandStrengthEvaluator evaluator;

    /**
     * Needed for serialization.
     */
    @SuppressWarnings("unused")
    private TelesinaPlayerToActCalculator() {
    }

    public TelesinaPlayerToActCalculator(Rank deckLowestRank) {
        evaluator = new TelesinaHandStrengthEvaluator(deckLowestRank);
    }

    @Override
    public PokerPlayer getFirstPlayerToAct(int dealerButtonSeatId, SortedMap<Integer, PokerPlayer> seatingMap, List<Card> communityCards) {
        Comparator<Hand> thc = evaluator.createHandComparator(seatingMap.size());
        PokerPlayer currentBestPlayer = null;
        Hand currentBestHand = null;

        for (PokerPlayer p : seatingMap.values()) {
            if (p.hasFolded() || p.hasActed() || p.isAllIn()) {
                continue; // Don't include sitting out or folded players
            }
            List<Card> cards = new LinkedList<Card>(p.getPublicPocketCards());
            cards.addAll(communityCards);

            Hand pHand = new Hand(cards);
            if (currentBestPlayer == null || thc.compare(pHand, currentBestHand) > 0) {
                currentBestPlayer = p;
                currentBestHand = pHand;
            }
        }

        return currentBestPlayer;
    }

    @Override
    public PokerPlayer getNextPlayerToAct(int lastActedSeatId, SortedMap<Integer, PokerPlayer> seatingMap) {
        PokerPlayer next = null;

        List<PokerPlayer> players = PokerUtils.unwrapList(seatingMap, lastActedSeatId + 1);
        for (PokerPlayer player : players) {
            if (!player.hasFolded() && !player.hasActed() && !player.isAllIn()) {
                next = player;
                break;
            }
        }
        return next;
    }

}

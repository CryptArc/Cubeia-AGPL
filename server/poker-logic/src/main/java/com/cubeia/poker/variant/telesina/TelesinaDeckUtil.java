package com.cubeia.poker.variant.telesina;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Sets.cartesianProduct;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;

public class TelesinaDeckUtil {

	private TelesinaDeckUtil() { }
	
	public static Rank calculateLowestRank(int participants) {
		int firstRankIndex = Math.max(0, 11 - participants - 2);
    	return Rank.values()[firstRankIndex];
	}
	
	public static List<Card> createDeckCards(int participants) {
		return createDeckCards(calculateLowestRank(participants));
	}
	
	@SuppressWarnings("unchecked")
    public static List<Card> createDeckCards(Rank lowestRank) {
        ArrayList<Card> cards = new ArrayList<Card>();
        List<Rank> ranks = asList(Rank.values()).subList(lowestRank.ordinal(), Rank.values().length);
        for (List<Enum<?>> cardContainer : cartesianProduct(copyOf(Suit.values()), copyOf(ranks))) {
            Suit suit = (Suit) cardContainer.get(0);
            Rank rank = (Rank) cardContainer.get(1);
            cards.add(new Card(rank, suit));
        }
        return cards;
    }
}

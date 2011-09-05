package com.cubeia.poker.variant.telesina.telesina;

import java.util.Comparator;

import com.cubeia.poker.hand.Card;

/**
 * Compares two cards telesina style. Note an ACE will always be better than any other 
 * Rank card. So an ACE used as a low card in a straight will still compare as a high card 
 * if compared to another card.
 */
public class TelesinaCardComparator implements Comparator<Card> {
	public static final TelesinaCardComparator ASC = new TelesinaCardComparator();
	public static final TelesinaCardComparator DESC = new TelesinaCardComparator(true);

	private int reverse = 1;
	
	public TelesinaCardComparator() {}
	
	public TelesinaCardComparator(boolean reverseOrder) {
		if (reverseOrder) {
			reverse = -1;
		} 
	}
	
	@Override
	public int compare(Card c1, Card c2) {
		if (c1.getRank() != c2.getRank()) {
			return reverse * (c1.getRank().ordinal() - c2.getRank().ordinal());
		}

		return reverse * (c1.getSuit().telesinaSuitValue - c2.getSuit().telesinaSuitValue);
	}
}
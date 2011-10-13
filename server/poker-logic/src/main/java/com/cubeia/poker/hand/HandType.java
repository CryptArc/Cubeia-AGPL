package com.cubeia.poker.hand;

public enum HandType {
	NOT_RANKED(0),
	HIGH_CARD(1),
	PAIR(2),
	TWO_PAIRS(3),
	THREE_OF_A_KIND(4),
	STRAIGHT(5),
	FLUSH(7),
	FULL_HOUSE(6),
	FOUR_OF_A_KIND(8),
	STRAIGHT_FLUSH(9),
	ROYAL_STRAIGHT_FLUSH(10);
	
	public final int telesinaHandTypeValue;

	private HandType(int telesinaHandTypeValue) {
		this.telesinaHandTypeValue = telesinaHandTypeValue;
	}
}

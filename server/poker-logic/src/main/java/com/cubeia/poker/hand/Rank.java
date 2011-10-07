package com.cubeia.poker.hand;


public enum Rank {
	TWO, 
	THREE, 
	FOUR, 
	FIVE, 
	SIX, 
	SEVEN, 
	EIGHT, 
	NINE, 
	TEN, 
	JACK, 
	QUEEN, 
	KING, 
	ACE;

	public String toShortString() {
		switch(this) {
		case TWO: return "2";
		case THREE: return "3";
		case FOUR: return "4";
		case FIVE: return "5";
		case SIX: return "6";
		case SEVEN: return "7";
		case EIGHT: return "8";
		case NINE: return "9";
		case TEN: return "T";
		case JACK: return "J";
		case QUEEN: return "Q";
		case KING: return "K";
		case ACE: return "A";
		default: throw new IllegalArgumentException("Invalid enum value for Rank: " + this);
		}
	}

	public static Rank fromShortString(char rank) {
		switch (rank) {
			case '2': return TWO;
			case '3': return THREE;
			case '4': return FOUR;
			case '5': return FIVE;
			case '6': return SIX;
			case '7': return SEVEN;
			case '8': return EIGHT;
			case '9': return NINE;
			case 'T': return TEN;
			case 'J': return JACK;
			case 'Q': return QUEEN;
			case 'K': return KING;
			case 'A': return ACE;
			case 't': return TEN;
			case 'j': return JACK;
			case 'q': return QUEEN;
			case 'k': return KING;
			case 'a': return ACE;
			default: throw new IllegalArgumentException("Invalid enum value for Rank: " + rank);
		}
	}
}

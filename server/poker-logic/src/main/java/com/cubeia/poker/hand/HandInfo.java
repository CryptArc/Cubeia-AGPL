package com.cubeia.poker.hand;

import java.util.List;

public interface HandInfo {

	HandType getType();
	
	List<Card> getCards();
}

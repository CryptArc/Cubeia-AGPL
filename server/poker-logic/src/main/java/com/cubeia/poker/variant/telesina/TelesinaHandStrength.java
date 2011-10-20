package com.cubeia.poker.variant.telesina;

import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandInfo;
import com.cubeia.poker.hand.HandType;

public class TelesinaHandStrength implements HandInfo {

	public HandType handType;
	public List<Card>[] groups;
	public List<Card> cardsUsedInHand;

	public TelesinaHandStrength(HandType handType, List<Card> cardsUsedInHand, int i, List<Card>... groups) {
		this.handType = handType;
		this.cardsUsedInHand = cardsUsedInHand;
		this.groups = groups;
	}

	@Override
	public HandType getType() {
		return handType;
	}

	@Override
	public List<Card> getCards() {
		return cardsUsedInHand;
	}
}

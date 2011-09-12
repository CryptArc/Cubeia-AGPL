package com.cubeia.poker.variant.telesina;

import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandInfo;
import com.cubeia.poker.hand.HandType;

public class TelesinaHandStrength implements HandInfo {

	public HandType handType;
	public List<Card> highGroupCards;
	public List<Card> lowGroupCards;
	public List<Card> kickers;
	public List<Card> handCards;

	public TelesinaHandStrength(HandType handType, List<Card> highGroupCards,
			List<Card> lowGroupCards, List<Card> kickers, List<Card> handCards) {
		this.handType = handType;
		this.highGroupCards = highGroupCards;
		this.lowGroupCards = lowGroupCards;
		this.kickers = kickers;
		this.handCards = handCards;
	}

	@Override
	public HandType getType() {
		return handType;
	}

	@Override
	public List<Card> getCards() {
		return handCards;
	}
}

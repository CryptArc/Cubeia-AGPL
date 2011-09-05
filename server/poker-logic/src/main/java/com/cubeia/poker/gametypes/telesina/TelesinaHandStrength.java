package com.cubeia.poker.gametypes.telesina;

import java.util.List;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandType;

public class TelesinaHandStrength {

	public HandType handType;
	public List<Card> highGroupCards;
	public List<Card> lowGroupCards;
	public List<Card> kickers;

	public TelesinaHandStrength(HandType handType, List<Card> highGroupCards,
			List<Card> lowGroupCards, List<Card> kickers) {
		this.handType = handType;
		this.highGroupCards = highGroupCards;
		this.lowGroupCards = lowGroupCards;
		this.kickers = kickers;
	}
}

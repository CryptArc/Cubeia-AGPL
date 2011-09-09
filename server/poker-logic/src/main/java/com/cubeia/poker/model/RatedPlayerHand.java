package com.cubeia.poker.model;

import java.io.Serializable;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandType;

public class RatedPlayerHand implements Serializable {

	private static final long serialVersionUID = 1L;

	private PlayerHand playerHand;
	private HandType bestHandType;

	public RatedPlayerHand(PlayerHand playerHand, HandType bestHandType) {
		this.playerHand = playerHand;
		this.bestHandType = bestHandType;
	}

	public PlayerHand getPlayerHand() {
		return playerHand;
	}

	public HandType getBestHandType() {
		return bestHandType;
	}

	public Integer getPlayerId() {
		return playerHand.getPlayerId();
	}

	public Hand getHand() {
		return playerHand.getHand();
	}
}

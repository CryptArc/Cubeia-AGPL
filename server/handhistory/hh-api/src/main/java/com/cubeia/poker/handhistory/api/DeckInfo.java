package com.cubeia.poker.handhistory.api;

import com.cubeia.poker.handhistory.api.GameCard.Rank;

public class DeckInfo {

	private final int size;
	private final Rank lowRank;

	public DeckInfo(int size, Rank lowRank) {
		this.size = size;
		this.lowRank = lowRank;
	}
	
	public Rank getLowRank() {
		return lowRank;
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return "Deck size: " + size + "; Low rank: " + lowRank;
	}
}

package com.cubeia.poker.hand;


public class HandStrength {
	
	private final HandType type;
	
	private Rank highestRank;
	
	private Rank secondRank;
	
	public HandStrength(HandType type) {
		this.type = type;
	}

	public HandType getHandType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "type["+type+"]";
	}

	public Rank getHighestRank() {
		return highestRank;
	}
	
	public void setHighestRank(Rank highestRank) {
		this.highestRank = highestRank;
	}

	public Rank getSecondRank() {
		return secondRank;
	}
	
	public void setSecondRank(Rank secondRank) {
		this.secondRank = secondRank;
	}

}

package com.cubeia.poker.hand;


public class HandStrength {
	
	private HandType type = HandType.NOT_RANKED;
	
	public HandType getHandType() {
		return type;
	}
	
	public void setHandType(HandType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "type["+type+"]";
	}
}

package com.cubeia.poker.handhistory.api;

public class Amount {

	public static enum Type {
		BET,
		RAISE,
		STACK,
		OTHER
	}
	
	public static Amount bet(long amount) {
		return new Amount(Type.BET, amount);
	}
	
	public static Amount raise(long amount) {
		return new Amount(Type.RAISE, amount);
	}
	
	public static Amount stack(long amount) {
		return new Amount(Type.STACK, amount);
	}
	
	public static Amount other(long amount) {
		return new Amount(Type.OTHER, amount);
	}
	
	private final Type type;
	private final long amount;
	
	private Amount(Type type, long amount) {
		this.type = type;
		this.amount = amount;
	}

	public Type getType() {
		return type;
	}

	public long getAmount() {
		return amount;
	}
}

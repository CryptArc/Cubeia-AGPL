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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (amount ^ (amount >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Amount other = (Amount) obj;
		if (amount != other.amount)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Amount [type=" + type + ", amount=" + amount + "]";
	}
}

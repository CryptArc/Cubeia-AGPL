package com.cubeia.backend.cashgame;

public class LongTransactionId implements TransactionId {

	private static final long serialVersionUID = 7340834491263254732L;
	
	private final long transactionId;
	
	public LongTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}
	
	public long getTransactionId() {
		return transactionId;
	}

	@Override
	public String toString() {
		return "LongTransactionId [transactionId=" + transactionId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (transactionId ^ (transactionId >>> 32));
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
		LongTransactionId other = (LongTransactionId) obj;
		if (transactionId != other.transactionId)
			return false;
		return true;
	}
}

package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

import com.cubeia.backend.cashgame.TransactionId;

public class TransactionUpdate implements Serializable {

	private static final long serialVersionUID = -7165224449107966435L;

	private final BalanceUpdate balance;
	private final TransactionId transactionId;
	
	public TransactionUpdate(TransactionId transactionId, BalanceUpdate balance) {
		this.transactionId = transactionId;
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "TransactionUpdate [balance=" + balance + ", transactionId="
				+ transactionId + "]";
	}

	public TransactionId getTransactionId() {
		return transactionId;
	}
	
	public BalanceUpdate getBalance() {
		return balance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result
				+ ((transactionId == null) ? 0 : transactionId.hashCode());
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
		TransactionUpdate other = (TransactionUpdate) obj;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		return true;
	}
}

package com.cubeia.poker.result;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long netResult;
	
	private final long winningsIncludingOwnBets;

	public Result(long netResult, long ownBets) {
		this.netResult = netResult;
		this.winningsIncludingOwnBets = netResult + ownBets;
	}

	public long getNetResult() {
		return netResult;
	}

	public long getWinningsIncludingOwnBets() {
		return winningsIncludingOwnBets;
	}
	
	public String toString() {
		return "Result net["+netResult+"] winningsIncOwnBet["+winningsIncludingOwnBets+"]";
	}
}

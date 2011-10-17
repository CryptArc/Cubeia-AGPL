package com.cubeia.poker.handhistory.api;

public class HandResult {
	
	private final long netWin;
	private final long totalWin;
	private final long rake;
	private final long totalBet;

	public HandResult(long netWin, long totalWin, long rake, long totalBet) {
		this.netWin = netWin;
		this.totalWin = totalWin;
		this.rake = rake;
		this.totalBet = totalBet;
	}
	
	public long getTotalBet() {
		return totalBet;
	}
	
	public long getNetWin() {
		return netWin;
	}
	
	public long getRake() {
		return rake;
	}
	
	public long getTotalWin() {
		return totalWin;
	}
}

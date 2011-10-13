package com.cubeia.poker.handhistory.api;

public class HandResult {
	
	private final long netWin;
	private final long totalWin;
	private final long rake;

	public HandResult(long netWin, long totalWin, long rake) {
		this.netWin = netWin;
		this.totalWin = totalWin;
		this.rake = rake;
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

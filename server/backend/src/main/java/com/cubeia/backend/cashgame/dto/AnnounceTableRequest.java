package com.cubeia.backend.cashgame.dto;

import java.math.BigDecimal;

public class AnnounceTableRequest {

	public final int numberOfSeats;
	public final String tableDescription;
	public final BetStrategy betStrategy;
	public final Currency currency;
	
	public final Integer ante;
	public final Integer smallBlind;
	public final Integer bigBlind;
	
	public final BigDecimal maxRakePercentage;
	public final int minBuyIn;
	public final int maxBuyIn;
	
	public AnnounceTableRequest(int numberOfSeats, String tableDescription,
			BetStrategy betStrategy, Currency currency, Integer ante,
			Integer smallBlind, Integer bigBlind, BigDecimal maxRakePercentage,
			int minBuyIn, int maxBuyIn) {
		this.numberOfSeats = numberOfSeats;
		this.tableDescription = tableDescription;
		this.betStrategy = betStrategy;
		this.currency = currency;
		this.ante = ante;
		this.smallBlind = smallBlind;
		this.bigBlind = bigBlind;
		this.maxRakePercentage = maxRakePercentage;
		this.minBuyIn = minBuyIn;
		this.maxBuyIn = maxBuyIn;
	}
	
	
	
}

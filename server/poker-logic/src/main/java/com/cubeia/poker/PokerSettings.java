package com.cubeia.poker;

import java.math.BigDecimal;

import com.cubeia.poker.rounds.betting.BetStrategyName;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.PokerVariant;

public class PokerSettings {
	private final int anteLevel;
	private final int minBuyIn;
	private final int maxBuyIn;
	private final TimingProfile timing;
	private final PokerVariant variant;
    private final int tableSize;
    private final BetStrategyName betStrategy;
    private final BigDecimal rakeFraction;
	
	public PokerSettings(
			int anteLevel,
			int minBuyIn,
			int maxBuyIn,
			TimingProfile timing,
			PokerVariant variant,
			int tableSize,
			BetStrategyName betStrategy, 
			BigDecimal rakeFraction) {
		
		this.anteLevel = anteLevel;
		this.minBuyIn = minBuyIn;
		this.maxBuyIn = maxBuyIn;
		this.timing = timing;
		this.variant = variant;
		this.tableSize = tableSize;
		this.betStrategy = betStrategy;
		this.rakeFraction = rakeFraction;
	}

	public int getAnteLevel() {
		return anteLevel;
	}

	public int getMaxBuyIn() {
		return maxBuyIn;
	}

	public TimingProfile getTiming() {
		return timing;
	}

	public PokerVariant getVariant() {
		return variant;
	}
	
	public int getTableSize() {
        return tableSize;
    }
	
	public BetStrategyName getBetStrategy() {
		return betStrategy;
	}
	
	public int getMinBuyIn() {
		return minBuyIn;
	}
	
	public BigDecimal getRakeFraction() {
        return rakeFraction;
    }

	public int getEntryBetLevel() {
		switch (variant)
		{
			case TELESINA:
				return anteLevel*2;
			default:
				return anteLevel;
		}
	}
}

package com.cubeia.poker.rounds;

import java.io.Serializable;

import com.cubeia.poker.player.PokerPlayer;

/**
 * Bet strategy for deciding what the min and max bets are, given the situation.
 * 
 * For fixed limit, we need to know:
 * 1. Min bet (will vary between rounds)
 * 2. Player to act's current bet stack and total stack 
 * 3. The number of bets and raises
 * 4. The max number of bets and raises allowed (usually 4)
 */
public interface BetStrategy extends Serializable {

	public long getMinRaiseToAmount(PokerPlayer player);

	public long getMaxRaiseToAmount(PokerPlayer player);

	public long getMinBetAmount(PokerPlayer player);

	public long getMaxBetAmount(PokerPlayer player);

	public long getCallAmount(PokerPlayer player);


}

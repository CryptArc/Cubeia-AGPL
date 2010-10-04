package com.cubeia.poker.rounds;

import com.cubeia.poker.player.PokerPlayer;

public interface BettingRoundContext {

	/**
	 * Gets the min bet in this betting round.
	 * 
	 * @return the min bet
	 */
	public long getMinBet();

	/**
	 * Gets the currently highest bet in this betting round.
	 * 
	 * @return the currently highest bet in this betting round
	 */
	public long getHighestBet();
	
	/**
	 * Gets the size of the last bet or raise.
	 * 
	 * @return the size of the last bet or raise
	 */
	public long getSizeOfLastBetOrRaise();
	
	/**
	 * Checks whether all other plahyers in this round are all in.
	 * 
	 * @return <code>true</code> if so, <code>false</code> otherwise
	 */
	public boolean allOtherPlayersAreAllIn(PokerPlayer thisPlayer);

}

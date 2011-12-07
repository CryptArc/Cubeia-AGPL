package com.cubeia.poker.blinds;

/**
 * Interface for describing a blinds player.
 * 
 * @author viktor
 *
 */
public interface BlindsPlayer {

	/**
	 * Gets the seat id of this player.
	 * 
	 * @return the seat id of this player
	 */
	public int getSeatId();
	
	/**
	 * Gets the player id of this player.
	 * 
	 * @return the player id of this player
	 */
	public long getPlayerId();
	
	/**
	 * Checks whether the player is sitting in.
	 * 
	 * Any player who is not sitting out is considered as sitting in, whether the entry bet has been posted or not. 
	 * 
	 * @return <code>true</code> if the player is sitting in, <code>false</code> otherwise
	 */
	public boolean isSittingIn();
	
	/**
	 * Checks whether the player has posted the entry bet.
	 * 
	 * A player pays the entry bet and then later declines or misses the big blind (due to sitting out) is 
	 * considered as _not_ having paid the entry bet.
	 * 
	 * @return <code>true</code> if the player has posted the entry bet, <code>false</code> otherwise
	 */
	public boolean hasPostedEntryBet();

	/**
	 * Gets the {@link MissedBlindsStatus} of this player.
	 * 
	 * @return the {@link MissedBlindsStatus} of this player
	 */
	public MissedBlindsStatus getMissedBlindsStatus();
}

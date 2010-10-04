package com.cubeia.poker.pot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.cubeia.poker.player.PokerPlayer;



/**
 * Pot 
 * @author peter
 *
 */
public class Pot implements Serializable {

	private static final long serialVersionUID = 1600275759404214507L;
	
	/** pot types */
	public enum PotType { MAIN, SIDE };
	
	/** pot id */
	private final int potId;
	
	/** type */
	private final PotType type;
	
	/** pot size */
	private long potSize;
	
	/** map of players who has contributed to this pot */
	Map<PokerPlayer, Long> playerToBetMap = new HashMap<PokerPlayer, Long>();
	
	/** is pot open? */
	private Boolean isOpen = true;
	
	public Pot(int potId) {
		this.potId = potId;
		if ( potId == 0 ) {
			this.type = PotType.MAIN;
		} else {
			this.type = PotType.SIDE;
		}
	}
	
	public String toString() {
		return "id["+potId+"] size["+potSize+"] betMap:"+playerToBetMap;
	}
	
	public PotType getType() {
		return type;
	}

	/**
	 * Add amount to the pot, add the player to the set of contributors 
	 * @param player the player who put the bet
	 * @param amount the betting amount
	 */
	public void bet(PokerPlayer player, Long amount) {
		if (amount == 0) {
			return;
		}
		if ( !isOpen ) {
			throw new IllegalStateException("Pot is not open");
		}
		
		potSize += amount;
		
		Long current = playerToBetMap.get(player);
		if (current == null) {
			playerToBetMap.put(player, amount);
		} else {
			playerToBetMap.put(player, current + amount);
		}
		
	}

	/**
	 * Gets the size of this pot.
	 *
	 * @return the size of this pot
	 */
	public long getPotSize() {
		 return potSize;
	}

	/**
	 * Closes this pot.
	 */
	public void close() {
		isOpen = false;
	}
	
	/** 
	 * Get the players involved in this pot 
	 */
	public Map<PokerPlayer, Long> getPotContributors() {
		return playerToBetMap;
	}

	/**
	 * is pot open? 
	 * @return true if the pot is open
	 */
	public Boolean isOpen() {
		return isOpen;
	}
	
	/**
	 * reduce the amount
	 * 
	 * @param amount to reduce the pot size with
	 */
	public void reduce(long amount) {
		if ( amount > potSize ) {
			throw new IllegalArgumentException("Tried to reduce the pot by " + amount + ", but there is only " + potSize);
		}
		potSize -= amount;
	}
	
	/** return the id of this pot */
	public int getId() {
		return potId;
	}
	
}

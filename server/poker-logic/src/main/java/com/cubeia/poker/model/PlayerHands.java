package com.cubeia.poker.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ualberta.cs.poker.Hand;

/**
 * DTO for sending all hands to the server layer.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PlayerHands implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<Integer, Hand> hands = new HashMap<Integer, Hand>();

	public PlayerHands() {
		
	}
	
	public void addHand(int playerId, Hand hand) {
		hands.put(playerId, hand);
	}
	
	public PlayerHands(Map<Integer, Hand> hands) {
		this.hands = hands;
	}
	
	public Map<Integer, Hand> getHands() {
		return hands;
	}
	
	
	/**
	 * Returns a new instance of PlayerHands that only includes the given
	 * list of player ids.
	 * 
	 * @param players
	 * @return
	 */
	public PlayerHands filter(List<Integer> players) {
		Map<Integer, Hand> filtered = new HashMap<Integer, Hand>(hands);
		filtered.keySet().retainAll(players);
		return new PlayerHands(filtered);
	}
}

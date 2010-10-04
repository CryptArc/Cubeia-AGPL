package com.cubeia.poker.player;

/**
 * Represents a tournament poker player.
 * 
 * The difference between a ring game player and a tournament player is that
 * a tournament player can never sit out and can never deny posting blinds.
 * 
 * @author viktor
 *
 */
public class TournamentPokerPlayer extends DefaultPokerPlayer {

	private static final long serialVersionUID = 1L;

	public TournamentPokerPlayer(int id) {
		super(id);
	}


}

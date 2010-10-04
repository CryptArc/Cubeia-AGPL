package com.cubeia.poker.rounds;

import com.cubeia.poker.GameType;
import com.cubeia.poker.action.PokerAction;

/**
 * This round has been separated for timing reasons.
 * 
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class DealCommunityCardsRound implements Round {

	private static final long serialVersionUID = 1L;
	
	private final GameType gameType;

	public DealCommunityCardsRound(GameType gameType) {
		this.gameType = gameType;
	}

	@Override
	public void act(PokerAction action) {
		throw new IllegalStateException("Perform action not allowed during DealCommunityCardsRound. Action received: "+action);
	}

	@Override
	public String getStateDescription() {
		return "DealCommunityCardsRound";
	}

	/**
	 * 
	 */
	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public void visit(RoundVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void timeout() {
		gameType.dealCommunityCards();
	}

}

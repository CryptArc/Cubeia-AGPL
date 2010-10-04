package com.cubeia.poker.rounds.blinds;


public abstract class AbstractBlindsState implements BlindsState {

	private static final long serialVersionUID = 1L;

	public void bigBlind(int playerId, BlindsRound context) {
		throw new IllegalStateException();
	}

	public void smallBlind(int playerId, BlindsRound context) {
		throw new IllegalStateException();
	}
	
	public void declineEntryBet(Integer playerId, BlindsRound blindsRound) {
		throw new IllegalStateException();
	}
	
	public void timeout(BlindsRound context) {
		throw new IllegalStateException();		
	}
	
	public boolean isFinished() {
		return false;
	}
	
	public boolean isCanceled() {
		return false;
	}

}

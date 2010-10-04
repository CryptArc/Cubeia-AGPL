package com.cubeia.poker.rounds.blinds;


public class CanceledState extends AbstractBlindsState {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean isFinished() {
		return true;
	}
	
	@Override
	public boolean isCanceled() {
		return true;
	}
}

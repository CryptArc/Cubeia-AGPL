package com.cubeia.poker.rounds.blinds;

import java.io.Serializable;


public interface BlindsState extends Serializable {

	void smallBlind(int playerId, BlindsRound blindsRound);

	void bigBlind(int playerId, BlindsRound blindsRound);

	void declineEntryBet(Integer playerId, BlindsRound blindsRound);

	void timeout(BlindsRound context);
	
	boolean isFinished();
	
	boolean isCanceled();

}

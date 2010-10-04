package com.cubeia.poker.rounds;

import com.cubeia.poker.rounds.blinds.BlindsRound;

public interface RoundVisitor {

	void visit(BettingRound bettingRound);

	void visit(BlindsRound blindsRound);
	
	void visit(DealCommunityCardsRound round);

}

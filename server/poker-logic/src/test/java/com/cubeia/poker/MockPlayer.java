package com.cubeia.poker;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;


public class MockPlayer extends DefaultPokerPlayer implements PokerPlayer  {

	private static final long serialVersionUID = 1L;

	public MockPlayer(int id) {
		super(id+100);
//		setBalance(5000);
		seatId = id;
	}

	public boolean isActionPossible(PokerActionType actionType) {
		return getActionRequest().isOptionEnabled(actionType);
	}

	@Override
	public int getSeatId() {
		return seatId;
	}
	
	@Override
	public String toString() {
		return String.format("<playerId[%d] seatId[%d] hasFolded[%b] hasActed[%b] isSittingOut[%b]>", playerId, seatId, hasFolded, hasActed, isSittingOut);
	}

	public void setSeatId(int i) {
		seatId = i;
	}
}

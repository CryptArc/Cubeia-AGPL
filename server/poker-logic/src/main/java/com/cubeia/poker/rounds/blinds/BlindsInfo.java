package com.cubeia.poker.rounds.blinds;

import java.io.Serializable;

import com.cubeia.poker.player.PokerPlayer;

public class BlindsInfo implements Serializable {

	private static final long serialVersionUID = -5897169468846176172L;

	private int anteLevel = -1;
	
	private int smallBlindPlayerId;
	
	private int dealerButtonSeatId;
	
	private int bigBlindPlayerId;
	
	private int bigBlindSeatId;

	private int smallBlindSeatId;

	private boolean hasDeadSmallBlind = false;

	public int getSmallBlindPlayerId() {
		return smallBlindPlayerId;
	}

	public void setSmallBlindPlayerId(int playerId) {
		this.smallBlindPlayerId = playerId;
	}

	public int getBigBlindPlayerId() {
		return bigBlindPlayerId;
	}

	public int getBigBlindSeatId() {
		return bigBlindSeatId;
	}

	public boolean isDefined() {
		return (smallBlindSeatId != bigBlindSeatId);
	}

	public boolean isHeadsUpLogic() {
		return dealerButtonSeatId == smallBlindSeatId;
	}

	public int getSmallBlindSeatId() {
		return smallBlindSeatId;
	}

	public void setSmallBlindSeatId(int smallBlindSeatId) {
		this.smallBlindSeatId = smallBlindSeatId;
	}

	public void setDealerButtonSeatId(int dealerButtonSeatId) {
		this.dealerButtonSeatId = dealerButtonSeatId;
	}

	public int getDealerButtonSeatId() {
		return dealerButtonSeatId;
	}

	public boolean hasDeadSmallBlind() {
		return hasDeadSmallBlind;
	}

	public void setHasDeadSmallBlind(boolean b) {
		hasDeadSmallBlind = b;
	}

	public void setSmallBlind(PokerPlayer player) {
		setSmallBlindPlayerId(player.getId());
		setSmallBlindSeatId(player.getSeatId());
	}

	public void setBigBlind(PokerPlayer player) {
		this.bigBlindPlayerId = player.getId();
		this.bigBlindSeatId = player.getSeatId();
	}

	public void setBigBlindSeatId(int i) {
		this.bigBlindSeatId = i;
	}

	public void setBigBlindPlayerId(int i) {
		this.bigBlindPlayerId = i;
	}

	public void setAnteLevel(int anteLevel) {
		this.anteLevel = anteLevel;
	}
	
	public int getAnteLevel() {
		return anteLevel;
	}
	
	public int getSmallBlindLevel() {
		return anteLevel/2;
	}
	
	public int getBigBlindLevel() {
		return anteLevel;
	}
}

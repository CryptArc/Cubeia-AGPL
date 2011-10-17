package com.cubeia.poker.handhistory.api;

public class PlayerAction extends HandHistoryEvent {
	
	public enum Type {
		SMALL_BLIND, BIG_BLIND, CALL, CHECK, BET, RAISE, FOLD, DECLINE_ENTRY_BET, ANTE;
	}

	private Type action;
	private Amount amount;
	private boolean timeout;

	private final int playerId;
	
	public PlayerAction(int playerId) {
		this.playerId = playerId;
	}
	
	public PlayerAction(int playerId, Type action) {
		this.playerId = playerId;
		this.action = action;
	}
	
	public PlayerAction(int playerId, Type action, Amount amount) {
		this.playerId = playerId;
		this.action = action;
		this.amount = amount;
	}

	public Type getAction() {
		return action;
	}

	public void setAction(Type action) {
		this.action = action;
	}

	public void setAmount(Amount amount) {
		this.amount = amount;
	}
	
	public Amount getAmount() {
		return amount;
	}

	public boolean isTimout() {
		return timeout;
	}

	public void setTimout(boolean timout) {
		this.timeout = timout;
	}

	public int getPlayerId() {
		return playerId;
	}
}

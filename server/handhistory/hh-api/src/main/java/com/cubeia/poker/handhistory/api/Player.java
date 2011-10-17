package com.cubeia.poker.handhistory.api;

public class Player {

	private final int id;

	private long initialBalance;
	private int seatId;
	private String name;
	
	public Player(int id) {
		this.id = id;
	}
	
	public Player(int id, int seatId, long initialBalance, String name) {
		this.id = id;
		this.seatId = seatId;
		this.initialBalance = initialBalance;
		this.name = name;
	}
	
	public long getInitialBalance() {
		return initialBalance;
	}
	
	public void setInitialBalance(long balance) {
		this.initialBalance = balance;
	}
	
	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}
	
	public int getSeatId() {
		return seatId;
	}
}

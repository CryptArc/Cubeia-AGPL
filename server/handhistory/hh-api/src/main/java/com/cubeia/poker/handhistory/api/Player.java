package com.cubeia.poker.handhistory.api;

public class Player {

	private final int id;

	private long balance;
	private int seatId;
	private String name;
	
	public Player(int id) {
		this.id = id;
	}
	
	public long getBalance() {
		return balance;
	}
	
	public void setBalance(long balance) {
		this.balance = balance;
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

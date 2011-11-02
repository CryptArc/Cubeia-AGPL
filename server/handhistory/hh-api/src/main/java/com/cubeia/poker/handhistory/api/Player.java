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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ (int) (initialBalance ^ (initialBalance >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + seatId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id != other.id)
			return false;
		if (initialBalance != other.initialBalance)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (seatId != other.seatId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", initialBalance=" + initialBalance
				+ ", seatId=" + seatId + ", name=" + name + "]";
	}
}

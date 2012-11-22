package com.cubeia.games.poker;


public class Request {

	private String currency = "EUR";
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private long balance = 50000;
	private long bankaccount = -3000;
	
	public Request(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public long getBalance() {
		return balance;
	}
	public void setBalance(long balance) {
		this.balance = balance;
	}
	public long getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(long bankaccount) {
		this.bankaccount = bankaccount;
	}

	@Override
	public String toString() {
		return "Request [currency=" + currency + ", username=" + username
				+ ", password=" + password + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", balance=" + balance
				+ ", bankaccount=" + bankaccount + "]";
	}
}

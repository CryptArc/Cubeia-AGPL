package com.cubeia.backend.cashgame;

public class AllowJoinResponse {

	public final boolean allowed;
	public final int responseCode;

	public AllowJoinResponse(boolean allowed, int responseCode) {
		this.allowed = allowed;
		this.responseCode = responseCode;
	}
}

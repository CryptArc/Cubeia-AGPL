package com.cubeia.backend.cashgame.dto;

public class Currency {

	public final String name;
	public final String code;
	public final int numberOfFractionalDigits;

	public Currency(String name, String code, int numberOfFractionalDigits) {
		this.name = name;
		this.code = code;
		this.numberOfFractionalDigits = numberOfFractionalDigits;
	}
	
	
}

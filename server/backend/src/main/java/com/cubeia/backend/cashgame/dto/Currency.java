package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Currency implements Serializable {

	public final String name;
	public final String code;
	public final int numberOfFractionalDigits;

	public Currency(String name, String code, int numberOfFractionalDigits) {
		this.name = name;
		this.code = code;
		this.numberOfFractionalDigits = numberOfFractionalDigits;
	}
	
	
}

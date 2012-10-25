package com.cubeia.games.poker.integration.api;

public enum RequestType {

	JOIN, // table join & tournament reg
	REBUY, // table or tournament reboy
	LEAVE, // table leave & tournament un-reg
	WIN, // tournament win
	ADJUST // system adjustments
}

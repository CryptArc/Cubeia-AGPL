package com.cubeia.poker.handhistory.api;

import java.util.HashMap;
import java.util.Map;

public class Results {

	private final Map<Integer, HandResult> results = new HashMap<Integer, HandResult>();
	
	public Map<Integer, HandResult> getResults() {
		return results;
	}
}

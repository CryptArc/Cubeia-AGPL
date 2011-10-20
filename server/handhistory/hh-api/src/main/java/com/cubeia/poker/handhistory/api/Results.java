package com.cubeia.poker.handhistory.api;

import java.util.HashMap;
import java.util.Map;

public class Results {

	private final Map<Integer, HandResult> results = new HashMap<Integer, HandResult>();
	
	public Map<Integer, HandResult> getResults() {
		return results;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((results == null) ? 0 : results.hashCode());
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
		Results other = (Results) obj;
		if (results == null) {
			if (other.results != null)
				return false;
		} else if (!results.equals(other.results))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Results [results=" + results + "]";
	}
}

package com.cubeia.games.poker.admin;

import org.springframework.stereotype.Component;

@Component
public class Configuration {

	private String networkUrl;
	
	public String getNetworkUrl() {
		return networkUrl;
	}
	
	public void setNetworkUrl(String networkUrl) {
		this.networkUrl = networkUrl;
	}

	@Override
	public String toString() {
		return "Configuration [networkUrl=" + networkUrl + "]";
	}
}

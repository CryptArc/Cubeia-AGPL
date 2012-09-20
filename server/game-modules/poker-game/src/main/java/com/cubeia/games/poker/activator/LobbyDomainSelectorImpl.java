package com.cubeia.games.poker.activator;

import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.google.inject.Singleton;

@Singleton
public class LobbyDomainSelectorImpl implements LobbyDomainSelector {

	@Override
	public String selectLobbyDomainFor(TableConfigTemplate templ) {
		return getGameShortName(templ) + "/cashgame/REAL_MONEY/" + templ.getSeats();
	}

	private String getGameShortName(TableConfigTemplate templ) {
		switch(templ.getVariant()) {
			case TELESINA : return "telesina";
			case TEXAS_HOLDEM : return "texas";
		}
		throw new IllegalArgumentException("Unknown variant: " + templ.getVariant());
	}
}

package com.cubeia.games.poker.activator;

import com.cubeia.games.poker.entity.TableConfigTemplate;

public interface LobbyDomainSelector {

	public String selectLobbyDomainFor(TableConfigTemplate templ);
	
}

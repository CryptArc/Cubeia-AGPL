package com.cubeia.games.poker.activator;

import static com.cubeia.games.poker.lobby.PokerLobbyAttributes.TABLE_TEMPLATE;

import java.util.Map;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.common.AttributeValue.Type;
import com.cubeia.firebase.api.game.lobby.LobbyTableFilter;
import com.cubeia.games.poker.entity.TableConfigTemplate;

public class TemplateLobbyTableFilter implements LobbyTableFilter {

	private final TableConfigTemplate template;

	public TemplateLobbyTableFilter(TableConfigTemplate template) {
		this.template = template;
	}
	
	@Override
	public boolean accept(Map<String, AttributeValue> map) {
		return map.containsKey(TABLE_TEMPLATE.name()) && matches(map.get(TABLE_TEMPLATE.name()));
	}

	// --- PRIVATE METHODS --- //
	
	private boolean matches(AttributeValue val) {
		return val.getType() == Type.INT && val.getIntValue() == template.getId();
	}
}

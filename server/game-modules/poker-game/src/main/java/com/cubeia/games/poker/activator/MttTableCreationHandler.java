package com.cubeia.games.poker.activator;

import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;

public interface MttTableCreationHandler {

	 public void tableCreated(Table table, int mttId, Object commandAttachment, LobbyAttributeAccessor acc);
	 
}

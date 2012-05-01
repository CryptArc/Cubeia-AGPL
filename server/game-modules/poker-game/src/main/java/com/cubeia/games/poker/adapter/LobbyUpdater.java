package com.cubeia.games.poker.adapter;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.games.poker.state.FirebaseState;

public class LobbyUpdater {

    public void updateLobby(FirebaseState fbState, Table table) {
        LobbyTableAttributeAccessor lobbyTable = table.getAttributeAccessor();
        lobbyTable.setAttribute("handcount", new AttributeValue(fbState.getHandCount()));
    }
}

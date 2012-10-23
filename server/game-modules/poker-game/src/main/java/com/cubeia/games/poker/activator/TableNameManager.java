package com.cubeia.games.poker.activator;

import com.cubeia.firebase.api.game.table.Table;

public interface TableNameManager {

    public void tableDestroyed(int tableId);

    public String tableCreated(Table table);

}

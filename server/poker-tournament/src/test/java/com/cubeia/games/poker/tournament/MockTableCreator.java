package com.cubeia.games.poker.tournament;

import java.util.Collection;

import com.cubeia.firebase.api.action.mtt.MttTablesCreatedAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.tables.MttTableCreator;

public class MockTableCreator implements MttTableCreator {

	private PokerTournament tournament;

	private MttInstance instance;
	
	private PokerTournamentUtil util = new PokerTournamentUtil();

	public MockTableCreator(PokerTournament tournament, MttInstance instance) {
		this.tournament = tournament;
		this.instance = instance;
	}

	public void createTables(int gameId, int mttId, int tableCount, int seats, String baseName, Object attachment) {
		MttTablesCreatedAction action = new MttTablesCreatedAction(mttId);
		for (int i = 0; i < tableCount; i++) {
			action.addTable(i);
		}
		MTTStateSupport state = util.getStateSupport(instance);
		state.getTables().addAll(action.getTables());
		tournament.process(action, instance);
	}

	public void removeTables(int gameId, int mttId, Collection<Integer> tableIds) {
		removeTables(tableIds);
	}

	private void removeTables(Collection<Integer> tableIds) {
		MTTStateSupport state = util.getStateSupport(instance);
		for (Integer tableId : tableIds) {
			state.getTables().remove(tableId);
		}
	}

	public void removeTables(int gameId, int mttId, Collection<Integer> unusedTables, long delayMs) {
		removeTables(unusedTables);
	}

}

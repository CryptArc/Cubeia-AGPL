package com.cubeia.games.poker.activator;

import static com.cubeia.poker.timing.Timings.DEFAULT;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;
import static java.util.Collections.singletonList;

import java.util.List;

import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.google.inject.Singleton;

@Singleton
public class SimpleTableConfigTemplateProvider implements TableConfigTemplateProvider {

	@Override
	public List<TableConfigTemplate> getTemplates() {
		TableConfigTemplate t = new TableConfigTemplate();
		t.setAnte(100);
		t.setSeats(10);
		t.setVariant(TEXAS_HOLDEM);
		t.setTiming(DEFAULT);
		t.setTTL(60000);
		t.setMinEmptyTables(5);
		t.setMinTables(10);
		return singletonList(t);
	}
}

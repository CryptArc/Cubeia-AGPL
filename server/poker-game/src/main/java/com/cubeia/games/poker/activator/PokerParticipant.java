/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.activator;

import java.util.Random;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.game.GameDefinition;
import com.cubeia.firebase.api.game.activator.DefaultCreationParticipant;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.lobby.LobbyPath;
import com.cubeia.games.poker.FirebaseState;
import com.cubeia.poker.PokerSettings;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.betting.BetStrategyName;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.timing.Timings;
import com.cubeia.poker.variant.PokerVariant;
import com.google.inject.Injector;


/**
 * Table Creator.
 * 
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerParticipant extends DefaultCreationParticipant {
    private transient Logger log = Logger.getLogger(this.getClass());

	private static final int GAME_ID = 4718;
	
	// the minimum buy in as a multiple of ante.
	private static final int MIN_BUY_IN = 10;

	/** Number of seats at the table */
	private int seats = 10;

	/** Dummy domain for tesing lobby tree */
	private String domain = "A";

	private TimingProfile timingProfile = TimingFactory.getRegistry().getDefaultTimingProfile();

	private Timings timing = Timings.DEFAULT;

	private final int anteLevel;
	
	private final RNGProvider rngProvider;

	// FIXME: This is not good IoC practice *cough*
	private Injector injector;

	private final PokerVariant variant;

	public PokerParticipant(int seats, String domain, int anteLevel, Timings timing, PokerVariant variant, RNGProvider rngProvider) {
		super();
		this.seats = seats;
		this.domain = domain;
		this.timing = timing;
		this.anteLevel = anteLevel;
		this.variant = variant;
		this.timingProfile  = TimingFactory.getRegistry().getTimingProfile(timing);
		this.rngProvider = rngProvider;
	}


	public LobbyPath getLobbyPath() {
		LobbyPath path = new LobbyPath(GAME_ID, domain + "/" + variant.name());
		return path;
	}

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	/* (non-Javadoc)
	 * @see com.cubeia.firebase.api.game.activator.DefaultCreationParticipant#getLobbyPathForTable(com.cubeia.firebase.api.game.table.Table)
	 */
	@Override 
	public LobbyPath getLobbyPathForTable(Table table) {
		LobbyPath path = getLobbyPath();
		path.setObjectId(table.getId());
		return path;
	}

	@Override
	public void tableCreated(Table table, LobbyTableAttributeAccessor acc) {
		super.tableCreated(table, acc);
		PokerState pokerState = injector.getInstance(PokerState.class);

		PokerSettings settings = new PokerSettings(anteLevel, anteLevel * MIN_BUY_IN, Integer.MAX_VALUE, timingProfile, variant, table.getPlayerSet().getSeatingMap().getNumberOfSeats(), BetStrategyName.NO_LIMIT);
		pokerState.init(rngProvider, settings);
		pokerState.setAdapterState(new FirebaseState());
		pokerState.setId(table.getId());
		table.getGameState().setState(pokerState);

		acc.setStringAttribute("SPEED", timing.name());
		acc.setIntAttribute("ANTE", anteLevel);
		acc.setStringAttribute("MONETARY_TYPE", "REAL_MONEY");
		acc.setIntAttribute("VISIBLE_IN_LOBBY", 1);
		acc.setStringAttribute("VARIANT", variant.name());

		log.debug("table created by participant: " + toString());
	}

	@Override
	public String getTableName(GameDefinition def, Table t) {
		return variant.name() + "<"+t.getId()+">";
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "PokerParticipant [seats=" + seats + ", domain=" + domain
				+ ", timingProfile=" + timingProfile + ", timing=" + timing
				+ ", anteLevel=" + anteLevel + ", variant=" + variant + "]";
	}
}

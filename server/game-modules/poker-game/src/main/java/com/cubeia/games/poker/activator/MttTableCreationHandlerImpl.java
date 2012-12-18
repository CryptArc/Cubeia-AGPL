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

import static com.cubeia.games.poker.common.lobby.PokerLobbyAttributes.TABLE_EXTERNAL_ID;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.cubeia.games.poker.common.lobby.PokerLobbyAttributes;
import com.cubeia.poker.model.BlindsLevel;
import org.apache.log4j.Logger;

import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.firebase.guice.inject.Log4j;
import com.cubeia.games.poker.state.FirebaseState;
import com.cubeia.games.poker.tournament.configuration.TournamentTableSettings;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.settings.BetStrategyName;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.GameType;
import com.cubeia.poker.variant.factory.GameTypeFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MttTableCreationHandlerImpl implements MttTableCreationHandler {

    @Log4j
    private Logger log;

    @Inject
    private PokerStateCreator stateCreator;

    @Override
    public void tableCreated(Table table, int mttId, Object commandAttachment, LobbyAttributeAccessor acc) {
        String externalTableId = "TOUR_TABLE::" + UUID.randomUUID();
        table.getGameState().setState(createGameState(table, mttId, commandAttachment, externalTableId));
        setLobbyData(acc, externalTableId);
        log.debug("Created tournament table[" + table.getId() + "] MTT ID: " + mttId);
    }

    private PokerState createGameState(Table table, int mttId, Object commandAttachment, String externalTableId) {
        PokerSettings settings = createSettings(table, commandAttachment, externalTableId);

        PokerState pokerState = stateCreator.newPokerState();
        GameType gameType = GameTypeFactory.createGameType(TEXAS_HOLDEM);
        pokerState.init(gameType, settings);
        pokerState.setTableId(table.getId());
        pokerState.setTournamentTable(true);
        pokerState.setTournamentId(mttId);
        pokerState.setAdapterState(new FirebaseState());
        return pokerState;
    }

    private PokerSettings createSettings(Table table, Object commandAttachment, String externalTableId) {
        TimingProfile timing = getTimingProfile(commandAttachment);
        int numberOfSeats = table.getPlayerSet().getSeatingMap().getNumberOfSeats();
        BetStrategyName noLimit = BetStrategyName.NO_LIMIT;
        RakeSettings rakeSettings = new RakeSettings(new BigDecimal(0), 0, 0); // No rake in tournaments.
        BlindsLevel level = new BlindsLevel(-1, -1, -1); // Blinds will be sent later.
        Map<Serializable, Serializable> attributes = Collections.<Serializable, Serializable>singletonMap(TABLE_EXTERNAL_ID.name(), externalTableId);
        return new PokerSettings(level, -1, -1, timing, numberOfSeats, noLimit, rakeSettings, attributes);
    }

    private TimingProfile getTimingProfile(Object commandAttachment) {
        TimingProfile timing = TimingFactory.getRegistry().getDefaultTimingProfile();
        if (commandAttachment instanceof TournamentTableSettings) {
            TournamentTableSettings settings = (TournamentTableSettings) commandAttachment;
            timing = settings.getTimingProfile();
        }
        log.debug("Timing for mtt table: " + timing);
        return timing;
    }

    private void setLobbyData(LobbyAttributeAccessor acc, String externalTableId) {
        acc.setStringAttribute(PokerLobbyAttributes.TABLE_EXTERNAL_ID.name(), externalTableId);
    }
}

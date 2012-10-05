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

import static com.cubeia.games.poker.lobby.PokerLobbyAttributes.TABLE_EXTERNAL_ID;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

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
        /*
         * TODO 1: Cleanup this method, it isn't very beautiful
         *
         * TODO 2: Add poker variant to tournament table settings etc
         */
        log.debug("Created poker tournament table: " + table.getId());
        int anteAmount = -1;
        int smallBlindAmount = -1;
        int bigBlindAmount = -1;
        TimingProfile timing = TimingFactory.getRegistry().getDefaultTimingProfile();
        if (commandAttachment instanceof TournamentTableSettings) {
            TournamentTableSettings settings = (TournamentTableSettings) commandAttachment;
            timing = settings.getTimingProfile();
            anteAmount = settings.getAnteAmount();
            smallBlindAmount = settings.getSmallBlindAmount();
            bigBlindAmount = settings.getBigBlindAmount();
        }
        log.debug("Created tournament table[" + table.getId() + "] with timing profile: " + timing + " MTT ID: " + mttId);
        PokerState pokerState = stateCreator.newPokerState();
        int numberOfSeats = table.getPlayerSet().getSeatingMap().getNumberOfSeats();
        BetStrategyName noLimit = BetStrategyName.NO_LIMIT;
        RakeSettings rakeSettings = new RakeSettings(new BigDecimal(0), 0, 0); // No rake in tournaments.
        Map<Serializable, Serializable> attributes = Collections.<Serializable, Serializable>singletonMap(TABLE_EXTERNAL_ID.name(),"MOCK_TRN::" + table.getId());
        PokerSettings settings = new PokerSettings(anteAmount, smallBlindAmount, bigBlindAmount, -1, -1, timing, numberOfSeats,
                                                   noLimit, rakeSettings, attributes);
        GameType gameType = GameTypeFactory.createGameType(TEXAS_HOLDEM);
        pokerState.init(gameType, settings);
        pokerState.setTableId(table.getId());
        pokerState.setTournamentTable(true);
        pokerState.setTournamentId(mttId);
        pokerState.setAdapterState(new FirebaseState());
        table.getGameState().setState(pokerState);
    }

}

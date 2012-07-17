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

package com.cubeia.games.poker.tournament.activator;

import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.games.poker.tournament.PokerTournamentLobbyAttributes;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentInstance;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentLifeCycle;
import com.cubeia.games.poker.tournament.configuration.TournamentLifeCycle;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;

public class ScheduledTournamentCreationParticipant extends PokerTournamentCreationParticipant {

    private ScheduledTournamentInstance instanceConfiguration;

    public ScheduledTournamentCreationParticipant(ScheduledTournamentInstance config) {
        super(config.getConfiguration());
        instanceConfiguration = config;
    }

    @Override
    protected TournamentLifeCycle getTournamentLifeCycle() {
        return new ScheduledTournamentLifeCycle(instanceConfiguration.getStartTime(), instanceConfiguration.getOpenRegistrationTime());
    }

    @Override
    protected void tournamentCreated(PokerTournamentState pokerState, LobbyAttributeAccessor lobbyAttributeAccessor) {
        setStatus(pokerState, lobbyAttributeAccessor, PokerTournamentStatus.ANNOUNCED);
        lobbyAttributeAccessor.setStringAttribute(PokerTournamentLobbyAttributes.IDENTIFIER.name(), instanceConfiguration.getIdentifier());
    }

    @Override
    protected String getType() {
        return "scheduled";
    }

    public ScheduledTournamentInstance getInstance() {
        return instanceConfiguration;
    }
}

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
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.lifecycle.SitAndGoLifeCycle;
import com.cubeia.games.poker.tournament.configuration.lifecycle.TournamentLifeCycle;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;

public class SitAndGoCreationParticipant extends PokerTournamentCreationParticipant {

    public SitAndGoCreationParticipant(SitAndGoConfiguration config) {
        super(config.getConfiguration());
    }

    @Override
    protected TournamentLifeCycle getTournamentLifeCycle() {
        return new SitAndGoLifeCycle();
    }

    @Override
    protected void tournamentCreated(PokerTournamentState pokerState, LobbyAttributeAccessor lobbyAttributeAccessor) {
        // Sit and go tournaments start in registering mode.
        setStatus(pokerState, lobbyAttributeAccessor, PokerTournamentStatus.REGISTERING);
    }

    @Override
    protected String getType() {
        return "sitandgo";
    }

}

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
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentInstance;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static com.cubeia.games.poker.tournament.PokerTournamentLobbyAttributes.START_TIME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ScheduledTournamentCreationParticipantTest {

    @Mock
    private ScheduledTournamentInstance config;

    @Mock
    private PokerTournamentState pokerState;

    @Mock
    private LobbyAttributeAccessor accessor;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testStartTime() {
        DateTime registrationTime = new DateTime(2012, 6, 4, 13, 0, 0);
        DateTime startTime = new DateTime(2012, 6, 4, 13, 10, 0);
        when(config.getStartTime()).thenReturn(startTime);
        when(config.getOpenRegistrationTime()).thenReturn(registrationTime);
        ScheduledTournamentCreationParticipant participant = new ScheduledTournamentCreationParticipant(config);
        participant.tournamentCreated(pokerState, accessor);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(accessor).setStringAttribute(eq(START_TIME.name()), captor.capture());
        assertEquals("2012-06-04 13:10", captor.getValue());
    }
}

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

package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.action.mtt.MttAction;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.MttNotifier;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.MTTSupport;
import com.cubeia.firebase.api.scheduler.Scheduler;
import com.cubeia.firebase.guice.tournament.TournamentAssist;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.tournament.configuration.lifecycle.ScheduledTournamentLifeCycle;
import com.cubeia.games.poker.tournament.configuration.lifecycle.TournamentLifeCycle;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PokerTournamentTest {

    private PokerTournamentState pokerState;

    @Mock
    private SystemTime dateFetcher;

    @Mock
    private MttInstance instance;

    @Mock
    private TournamentAssist support;

    @Mock
    private MTTStateSupport state;

    @Mock
    private MttNotifier notifier;

    @Mock
    private Scheduler<MttAction> scheduler;

    @Mock
    private LobbyAttributeAccessor lobbyAccessor;

    private PokerTournament tournament;
    private TournamentLifeCycle lifeCycle;

    @Before
    public void setup() {
        initMocks(this);
        pokerState = new PokerTournamentState();
        when(instance.getScheduler()).thenReturn(scheduler);
        when(instance.getLobbyAccessor()).thenReturn(lobbyAccessor);
    }

    @Test
    public void registrationStartShouldBeScheduledWhenScheduledTournamentIsCreated() {
        // Given a scheduled tournament
        prepareTournament();
        when(dateFetcher.date()).thenReturn(new DateTime(2011, 7, 5, 13, 30, 1));

        // When the tournament is created
        tournament.tournamentCreated();

        // Then registration opening should be scheduled
        ArgumentCaptor<MttAction> captor = ArgumentCaptor.forClass(MttAction.class);
        long timeToRegistrationStart = lifeCycle.getTimeToRegistrationStart(dateFetcher.date());
        verify(scheduler).scheduleAction(captor.capture(), eq(timeToRegistrationStart));
        assertEquals(TournamentTrigger.OPEN_REGISTRATION, ((MttObjectAction) captor.getValue()).getAttachment());
    }

    @Test
    public void shouldScheduleTournamentStartAfterOpeningRegistration() {
        // Given a scheduled tournament
        prepareTournament();
        when(dateFetcher.date()).thenReturn(new DateTime(2011, 7, 5, 14, 00, 1));

        // When registration is opened
        tournament.handleTrigger(TournamentTrigger.OPEN_REGISTRATION);

        // Then we should schedule tournament start
        ArgumentCaptor<MttAction> captor = ArgumentCaptor.forClass(MttAction.class);
        long timeToTournamentStart = lifeCycle.getTimeToTournamentStart(dateFetcher.date());
        verify(scheduler).scheduleAction(captor.capture(), eq(timeToTournamentStart));
        assertEquals(TournamentTrigger.START_TOURNAMENT, ((MttObjectAction) captor.getValue()).getAttachment());
    }

    private TournamentLifeCycle prepareTournament() {
        DateTime startTime = new DateTime(2011, 7, 5, 14, 30, 0);
        DateTime openRegistrationTime = new DateTime(2011, 7, 5, 14, 0, 0);
        lifeCycle = new ScheduledTournamentLifeCycle(startTime, openRegistrationTime);
        tournament = new PokerTournament(pokerState, dateFetcher, lifeCycle);
        tournament.injectTransientDependencies(instance, support, state, notifier);
        return lifeCycle;
    }
}

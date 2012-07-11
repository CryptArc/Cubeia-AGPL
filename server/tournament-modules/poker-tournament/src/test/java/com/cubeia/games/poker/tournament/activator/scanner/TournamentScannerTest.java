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

package com.cubeia.games.poker.tournament.activator.scanner;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.mtt.MttFactory;
import com.cubeia.firebase.api.mtt.activator.ActivatorContext;
import com.cubeia.firebase.api.mtt.lobby.MttLobbyObject;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.games.poker.tournament.PokerTournamentLobbyAttributes;
import com.cubeia.games.poker.tournament.activator.ScheduledTournamentCreationParticipant;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentInstance;
import com.cubeia.games.poker.tournament.configuration.TournamentSchedule;
import com.cubeia.games.poker.tournament.configuration.provider.SitAndGoConfigurationProvider;
import com.cubeia.games.poker.tournament.configuration.provider.TournamentScheduleProvider;
import com.cubeia.games.poker.tournament.util.DateFetcher;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.quartz.CronTrigger;

import java.util.Map;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.TriggerBuilder.newTrigger;

public class TournamentScannerTest {

    @Mock
    private SitAndGoConfigurationProvider sitAndGoProvider;

    @Mock
    private TournamentScheduleProvider tournamentScheduleProvider;

    @Mock
    private ActivatorContext context;

    @Mock
    private DateFetcher dateFetcher;

    @Mock
    private MttFactory factory;

    private TournamentScanner scanner;

    @Before
    public void setup() throws SystemException {
        initMocks(this);
        scanner = new TournamentScanner(sitAndGoProvider, tournamentScheduleProvider, dateFetcher);
        scanner.init(context);
        scanner.setMttFactory(factory);

        when(factory.listTournamentInstances()).thenReturn(new MttLobbyObject[]{});
    }

    @Test
    public void shouldCreateTournamentWhenAnnouncingTimeHasCome() {
        // Given a tournament that should start at 14.30 and be announced 30 minutes before.
        CronTrigger schedule = newTrigger().withSchedule(dailyAtHourAndMinute(14, 30))
                .startAt(new DateTime(2011, 7, 5, 9, 0, 0).toDate())
                .endAt(new DateTime(2013, 7, 5, 9, 0, 0).toDate()).build();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(schedule, 10, 20, 30);
        ScheduledTournamentConfiguration tournament = new ScheduledTournamentConfiguration(tournamentSchedule);
        when(tournamentScheduleProvider.getTournamentSchedule()).thenReturn(singletonList(tournament));

        // When we scan tournaments at 14.00.
        when(dateFetcher.now()).thenReturn(new DateTime(2012, 7, 5, 14, 0, 2));
        scanner.checkTournamentsNow();

        // Then we should create a tournament.
        verify(factory).createMtt(anyInt(), anyString(), isA(ScheduledTournamentCreationParticipant.class));
    }

    @Test
    public void shouldOnlyCreateOneInstancePerStartTime() {
        // Given a tournament that should start at 14.30 and be announced 30 minutes before.
        CronTrigger schedule = newTrigger().withSchedule(dailyAtHourAndMinute(14, 30))
                .startAt(new DateTime(2011, 7, 5, 9, 0, 0).toDate())
                .endAt(new DateTime(2013, 7, 5, 9, 0, 0).toDate()).build();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(schedule, 10, 20, 30);
        ScheduledTournamentConfiguration tournament = new ScheduledTournamentConfiguration(tournamentSchedule);
        when(tournamentScheduleProvider.getTournamentSchedule()).thenReturn(singletonList(tournament));

        // When we scan tournaments at 14:00.02 and 14:00.03.
        when(dateFetcher.now()).thenReturn(new DateTime(2012, 7, 5, 14, 0, 2)).thenReturn(new DateTime(2012, 7, 5, 14, 0, 3));
        scanner.checkTournamentsNow();

        // We should create one tournament.
        ArgumentCaptor<ScheduledTournamentCreationParticipant> captor = ArgumentCaptor.forClass(ScheduledTournamentCreationParticipant.class);
        verify(factory, times(1)).createMtt(anyInt(), anyString(), captor.capture());

        // And then check it again at 14.00.03 (resetting the factory so we can have it return the tournament we just created).
        reset(factory);
        ScheduledTournamentInstance instance = captor.getValue().getInstance();
        MttLobbyObject mttLobbyObject = tournamentWithNameAndIdentifier(instance.getName(), instance.getIdentifier());
        MttLobbyObject[] mttLobbyObjects = new MttLobbyObject[]{mttLobbyObject};
        when(factory.listTournamentInstances()).thenReturn(mttLobbyObjects);
        scanner.checkTournamentsNow();

        // Then we should not create any more tournaments (because the factory has been reset, the first invocation is gone).
        verify(factory, never()).createMtt(anyInt(), anyString(), isA(ScheduledTournamentCreationParticipant.class));
    }

    private MttLobbyObject tournamentWithNameAndIdentifier(String name, String identifier) {
        MttLobbyObject lobbyObject = mock(MttLobbyObject.class);
        Map<String, AttributeValue> map = Maps.newHashMap();
        map.put(PokerTournamentLobbyAttributes.IDENTIFIER.name(), AttributeValue.wrap(identifier));
        when(lobbyObject.getAttributes()).thenReturn(map);
        return lobbyObject;
    }
}

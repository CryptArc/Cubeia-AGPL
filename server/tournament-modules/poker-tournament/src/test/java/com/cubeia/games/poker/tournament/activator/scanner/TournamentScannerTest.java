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
import com.cubeia.games.poker.tournament.activator.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.activator.configuration.ScheduledTournamentInstance;
import com.cubeia.games.poker.tournament.activator.configuration.TournamentSchedule;
import com.cubeia.games.poker.tournament.activator.configuration.provider.SitAndGoConfigurationProvider;
import com.cubeia.games.poker.tournament.activator.configuration.provider.TournamentScheduleProvider;
import com.cubeia.games.poker.tournament.util.DateFetcher;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;

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
        TournamentSchedule schedule = new TournamentSchedule(dailyAtHourAndMinute(14, 30).build(), 10, 20, 30);
        ScheduledTournamentConfiguration tournament = new ScheduledTournamentConfiguration(schedule);
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
        TournamentSchedule schedule = new TournamentSchedule(dailyAtHourAndMinute(14, 30).build(), 10, 20, 30);
        ScheduledTournamentConfiguration tournament = new ScheduledTournamentConfiguration(schedule);
        when(tournamentScheduleProvider.getTournamentSchedule()).thenReturn(singletonList(tournament));

        // When we scan tournaments at 14:00.02 and 14:00.03.
        when(dateFetcher.now()).thenReturn(new DateTime(2012, 7, 5, 14, 0, 2)).thenReturn(new DateTime(2012, 7, 5, 14, 0, 3));
        scanner.checkTournamentsNow();

        reset(factory);
        ScheduledTournamentInstance instance = tournament.spawnConfigurationForNextInstance(dateFetcher.now());
        MttLobbyObject mttLobbyObject = tournamentWithNameAndIdentifier(instance.getName(), instance.getIdentifier());
        MttLobbyObject[] mttLobbyObjects = new MttLobbyObject[]{mttLobbyObject};
        when(factory.listTournamentInstances()).thenReturn(mttLobbyObjects);
        scanner.checkTournamentsNow();

        // Then we should only create one instance of the tournament.
        verify(factory, times(1)).createMtt(anyInt(), anyString(), isA(ScheduledTournamentCreationParticipant.class));
    }

    private MttLobbyObject tournamentWithNameAndIdentifier(String name, String identifier) {
        MttLobbyObject lobbyObject = mock(MttLobbyObject.class);
        Map<String, AttributeValue> map = Maps.newHashMap();
        map.put(PokerTournamentLobbyAttributes.IDENTIFIER.name(), AttributeValue.wrap(identifier));
//        map.put(Enums.TournamentAttributes.NAME.name(), AttributeValue.wrap(identifier));
//        map.put(Enums.TournamentAttributes.STATUS.name(), AttributeValue.wrap("ANNOUNCED"));
        when(lobbyObject.getAttributes()).thenReturn(map);
        return lobbyObject;
    }
}

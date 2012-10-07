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

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.SeatPlayersMttAction;
import com.cubeia.firebase.api.action.mtt.MttAction;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.MttNotifier;
import com.cubeia.firebase.api.mtt.model.MttPlayer;
import com.cubeia.firebase.api.mtt.model.MttRegistrationRequest;
import com.cubeia.firebase.api.mtt.support.LobbyAttributeAccessorAdapter;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.MttNotifierAdapter;
import com.cubeia.firebase.api.scheduler.Scheduler;
import com.cubeia.firebase.api.service.mttplayerreg.TournamentPlayerRegistry;
import com.cubeia.games.poker.tournament.activator.PokerTournamentCreationParticipant;
import com.cubeia.games.poker.tournament.activator.ScheduledTournamentCreationParticipant;
import com.cubeia.games.poker.tournament.activator.SitAndGoCreationParticipant;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentInstance;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsLevel;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructureFactory;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.ANNOUNCED;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.REGISTERING;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests a poker tournament.
 * <p/>
 * Testing check list:
 * 1. Register a player, check that he is registered.
 * 2. Register enough players for the tournament to start, check that it starts.
 * 3. Send a round report indicating that two players are out, check that they are removed from the tournament.
 * 4. Send another round report and check that table balancing occurs.
 * 5. Check that the blinds are increased when a timeout is triggered.
 * 6. Check that the tournament finishes when there is only one player left.
 */
public class PokerTournamentProcessorTest extends TestCase {

    private static final Logger log = Logger.getLogger(PokerTournamentProcessorTest.class);

    // Class under test.
    private PokerTournamentProcessor tournamentProcessor;

    private MTTStateSupport state;

    @Mock
    private MttInstance instance;

    @Mock
    private Scheduler<MttAction> scheduler;

    @Mock
    private TournamentPlayerRegistry playerRegistry;

    @Mock
    private MttNotifier notifier;

    @Mock
    private ScheduledTournamentInstance instanceConfig;

    @Mock
    private TournamentConfiguration configuration;

    private MockTournamentAssist support;

    private LobbyAttributeAccessor lobbyAccessor = new LobbyAttributeAccessorAdapter();

    private PokerTournamentState pokerState;

    private Random rng = new Random();

    @Override
    protected void setUp() throws Exception {
        initMocks(this);
        tournamentProcessor = new PokerTournamentProcessor();
        support = new MockTournamentAssist();
        tournamentProcessor.setSupport(support);

        state = new MTTStateSupport(1, 1);
        when(configuration.getBlindsStructure()).thenReturn(BlindsStructureFactory.createDefaultBlindsStructure());
        when(instance.getSystemPlayerRegistry()).thenReturn(playerRegistry);
        when(instance.getState()).thenReturn(state);
        when(instance.getLobbyAccessor()).thenReturn(lobbyAccessor);
        when(instance.getScheduler()).thenReturn(scheduler);
        when(instance.getMttNotifier()).thenReturn(notifier);
        when(instanceConfig.getConfiguration()).thenReturn(configuration);
        support.setTableCreator(new MockTableCreator(tournamentProcessor, instance));
        support.setMttNotifier(new MttNotifierAdapter());

        SitAndGoConfiguration config = new SitAndGoConfiguration("test", 20);
        config.getConfiguration().setBlindsStructure(BlindsStructureFactory.createDefaultBlindsStructure());
        PokerTournamentCreationParticipant part = new SitAndGoCreationParticipant(config);
        part.tournamentCreated(state, instance.getLobbyAccessor());

        pokerState = new PokerTournamentUtil().getPokerState(instance);
    }

    public void testRegister() {
        registerPlayer(1);
    }

    public void testSitAndGo() {
        assertEquals(REGISTERING.name(),
                instance.getLobbyAccessor().getStringAttribute(PokerTournamentLobbyAttributes.STATUS.name()));
        assertEquals(20, state.getMinPlayers());
        fillTournament();
        assertEquals(PokerTournamentStatus.RUNNING.name(),
                instance.getLobbyAccessor().getStringAttribute(PokerTournamentLobbyAttributes.STATUS.name()));
        assertEquals(2, state.getTables().size());
        assertEquals(10, state.getPlayersAtTable(0).size());
    }

    public void testPlayerOut() {
        fillTournament();
        int remaining = state.getRemainingPlayerCount();
        simulatePlayersOut(1, state.getPlayersAtTable(1).iterator().next());
        assertEquals(remaining - 1, state.getRemainingPlayerCount());
    }

    public void testBalanceTables() {
        fillTournament();
        forceBalancing();
    }

    public void testScheduledTournamentSchedulesOpeningRegistrationWhenAskedTo() {
        // Given a scheduled tournament
        prepareScheduledTournament();

        // When the tournament receives an open registration trigger.
        MttObjectAction objectAction = new MttObjectAction(instance.getId(), TournamentTrigger.OPEN_REGISTRATION);
        tournamentProcessor.process(objectAction, instance);

        // Then the registration should be opened.
        assertEquals(REGISTERING, pokerState.getStatus());
    }

    private void prepareScheduledTournament() {
        when(instanceConfig.getStartTime()).thenReturn(new DateTime());
        when(instanceConfig.getOpenRegistrationTime()).thenReturn(new DateTime());
        PokerTournamentCreationParticipant participant = new ScheduledTournamentCreationParticipant(instanceConfig);
        participant.tournamentCreated(state, instance.getLobbyAccessor());
        pokerState = new PokerTournamentUtil().getPokerState(instance);
        assertEquals(ANNOUNCED, pokerState.getStatus());
    }

    private void forceBalancing() {
        int remaining = state.getRemainingPlayerCount();
        Collection<Integer> playersAtTable = state.getPlayersAtTable(0);
        Iterator<Integer> iterator = playersAtTable.iterator();
        simulatePlayersOut(0, iterator.next(), iterator.next());
        assertEquals(remaining - 2, state.getRemainingPlayerCount());

        // Another table finishes a hand.
        int playersAtTableTwo = state.getPlayersAtTable(1).size();
        sendRoundReport(1, new PokerTournamentRoundReport(new BlindsLevel(10, 20, 0)));
        assertEquals(playersAtTableTwo - 1, state.getPlayersAtTable(1).size());
    }

    public void testStartingBalance() {
        fillTournament();
        assertEquals(Long.valueOf(100000), pokerState.getPlayerBalance(1));
    }

    public void testBalanceAfterMove() {
        fillTournament();
        support.setMttNotifier(new MttNotifier() {

            public void notifyPlayer(int playerId, MttAction action) {

            }

            public void notifyTable(int tableId, GameAction action) {
                log.debug("Received action: " + action);
                if (action instanceof SeatPlayersMttAction) {
                    SeatPlayersMttAction seat = (SeatPlayersMttAction) action;
                    assertEquals(Long.valueOf(100000), seat.getPlayers().iterator().next().getPlayerData());
                }
            }

        });
        forceBalancing();
    }

    public void testStartToEnd() {
        fillTournament();

        int i = 0;
        while (pokerState.getStatus() != PokerTournamentStatus.FINISHED) {
            int randomTableId = getRandomTableId(state.getTables());

            if (randomTableId != -1) {
                sendRoundReport(randomTableId, createRoundReport(randomTableId));
            }
            if (i++ > 1000) {
                fail("Tournament should have been finished by now.");
            }
        }
    }

    private PokerTournamentRoundReport createRoundReport(int tableId) {
        PokerTournamentRoundReport report = new PokerTournamentRoundReport(new BlindsLevel(10, 20, 0));
        Collection<Integer> playersAtTable = state.getPlayersAtTable(tableId);
        int playersInTournament = state.getRemainingPlayerCount();

        for (Integer playerId : playersAtTable) {
            // Check so we don't kick all players out
            long randomBalance = getRandomBalance();
            if (randomBalance <= 0 && --playersInTournament == 0) {
                randomBalance = 1000; // Last player
            }
            report.setBalance(playerId, randomBalance);
        }

        return report;
    }

    private long getRandomBalance() {
        boolean out = rng.nextInt(100) < 40;
        if (out) {
            return 0;
        } else {
            return rng.nextInt(1000);
        }
    }

    private int getRandomTableId(Set<Integer> tables) {
        List<Integer> list = new ArrayList<Integer>(tables);
        if (list.size() > 0) {
            return list.get(new Random().nextInt(list.size()));
        }
        return -1;
    }

    private void simulatePlayersOut(int tableId, int... playerIds) {
        sendRoundReport(tableId, createPlayersOutRoundReport(playerIds));
    }

    private void sendRoundReport(int tableId, PokerTournamentRoundReport report) {
        MttRoundReportAction action = new MttRoundReportAction(1, tableId);
        action.setAttachment(report);
        tournamentProcessor.process(action, instance);
    }

    private PokerTournamentRoundReport createPlayersOutRoundReport(int... playerIds) {
        PokerTournamentRoundReport roundReport = new PokerTournamentRoundReport(new BlindsLevel(10, 20, 0));
        for (int playerId : playerIds) {
            roundReport.setBalance(playerId, 0);
        }
        return roundReport;
    }

    private void fillTournament() {
        for (int i = 0; i < state.getMinPlayers(); i++) {
            registerPlayer(i);
        }
    }

    private void registerPlayer(int playerId) {
        MttPlayer player = new MttPlayer(playerId);
        MttRegistrationRequest request = new MttRegistrationRequest(player, new ArrayList<Attribute>());
        int before = state.getRegisteredPlayersCount();
        state.getPlayerRegistry().register(instance, request);
        tournamentProcessor.getPlayerListener(state).playerRegistered(instance, request);
        assertEquals(before + 1, state.getRegisteredPlayersCount());
    }
}

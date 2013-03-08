/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.TournamentSessionId;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.firebase.CashGamesBackendService;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.service.mttplayerreg.TournamentPlayerRegistry;
import com.cubeia.firebase.guice.tournament.TournamentAssist;
import com.cubeia.games.poker.common.money.Money;
import com.cubeia.games.poker.common.time.DefaultSystemTime;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructure;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructureFactory;
import com.cubeia.games.poker.tournament.configuration.blinds.Level;
import com.cubeia.games.poker.tournament.configuration.lifecycle.ScheduledTournamentLifeCycle;
import com.cubeia.games.poker.tournament.configuration.lifecycle.TournamentLifeCycle;
import com.cubeia.games.poker.tournament.messages.PokerTournamentRoundReport;
import com.cubeia.games.poker.tournament.messages.RebuyResponse;
import com.cubeia.games.poker.tournament.messages.RebuyTimeout;
import com.cubeia.games.poker.tournament.rebuy.RebuySupport;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.util.PacketSender;
import com.cubeia.poker.shutdown.api.ShutdownServiceContract;
import com.cubeia.poker.tournament.history.storage.api.TournamentHistoryPersistenceService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.ON_BREAK;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.PREPARING_BREAK;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.RUNNING;
import static com.google.common.collect.ImmutableSet.of;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Stuff to test:
 * <p/>
 * 2. Don't start break when we are waiting for rebuys.
 * 3. If a spontaneous rebuy is performed while we are waiting for rebuys from players who are out, don't start a new hand.
 * 4. If a rebuy timeout occurs while we are waiting for a rebuy, we should remove any players from the tournament who
 * has not yet answered, but we should wait for any outstanding backend requests before the next hand is started.
 */
public class RebuyTournamentTests {

    private PokerTournamentState pokerState;
    private PokerTournament tournament;
    private BlindsStructure blindsStructure = BlindsStructureFactory.createDefaultBlindsStructure();
    @Mock(answer = RETURNS_DEEP_STUBS)
    private MttInstance instance;
    @Mock
    private TournamentAssist support;
    @Mock
    private MTTStateSupport state;
    @Mock
    private TournamentHistoryPersistenceService historyService;
    @Mock
    private CashGamesBackendService backend;
    @Mock
    private ShutdownServiceContract shutdownService;
    @Mock
    private TournamentPlayerRegistry tournamentPlayerRegistry;
    @Mock
    private PacketSender sender;
    @Captor
    private ArgumentCaptor<MttObjectAction> actionCaptor;

    @Before
    public void setup() {
        initMocks(this);
        pokerState = new PokerTournamentState();
        pokerState.setStatus(RUNNING);
    }

    @Test
    public void doNotStarNewHandWhenRebuysHaveBeenRequested() {
        // Given a tournament
        when(state.getTables()).thenReturn(of(1));
        when(state.getPlayersAtTable(1)).thenReturn(of(1, 2, 3));
        prepareTournament();

        // When one player is out
        sendRoundReportToTournament(1, 1);

        // Then we should schedule a rebuy timeout (as opposed to starting the next hand)
        verify(instance.getScheduler()).scheduleAction(actionCaptor.capture(), anyLong());
        assertThat(actionCaptor.getValue().getAttachment() instanceof RebuyTimeout, is(true));
    }

    @Test
    public void doNotStartBreakUntilRebuysAreFinished() {
        // Given a tournament that is waiting for a rebuy and about to go on a break.
        when(state.getTables()).thenReturn(of(1, 2));
        when(state.getPlayersAtTable(1)).thenReturn(of(1, 2, 3));
        prepareTournament();
        blindsStructure.insertLevel(1, new Level(80, 120, 0, 2, true));
        pokerState.increaseBlindsLevel();
        assertThat(pokerState.isOnBreak(), is(true));
        sendRoundReportToTournament(1, 1);

        // When another table finishes a hand.
        sendRoundReportToTournament(2);

        // The break should not have started.
        assertThat(pokerState.getStatus(), is(PREPARING_BREAK));

        // But when the rebuy is finished.
        tournament.handleRebuyResponse(new RebuyResponse(1, 1, 0, true));
        tournament.handleReservationResponse(createReserveResponse(1));

        // The break starts.
        assertThat(pokerState.getStatus(), is(ON_BREAK));
    }

    private ReserveResponse createReserveResponse(int playerId) {
        Money money = new Money(1, "EUR", 2);
        BalanceUpdate balanceUpdate = new BalanceUpdate(new PlayerSessionId(playerId, "1"), money, 1);
        return new ReserveResponse(balanceUpdate, money);
    }

    private void sendRoundReportToTournament(int tableId, int ... playersOut) {
        MttRoundReportAction action = mock(MttRoundReportAction.class);
        Map balances = new HashMap<Integer, Long>();
        for (int playerOut : playersOut) {
            balances.put(playerOut, 0L);
        }
        PokerTournamentRoundReport.Level level = new PokerTournamentRoundReport.Level(10, blindsStructure.getBlindsLevel(0).getBigBlindAmount(), 0);
        PokerTournamentRoundReport report = new PokerTournamentRoundReport(balances, level);

        when(action.getTableId()).thenReturn(tableId);
        when(action.getAttachment()).thenReturn(report);
        tournament.processRoundReport(action);
    }

    private TournamentLifeCycle prepareTournament() {
        DateTime startTime = new DateTime(2011, 7, 5, 14, 30, 0);
        DateTime openRegistrationTime = new DateTime(2011, 7, 5, 14, 0, 0);
        ScheduledTournamentLifeCycle lifeCycle = new ScheduledTournamentLifeCycle(startTime, openRegistrationTime);
        pokerState.setLifecycle(lifeCycle);
        pokerState.setRebuySupport(new RebuySupport(true, 1000, 1000, 1000, 1000, true, 3, BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
        pokerState.setBlindsStructure(blindsStructure);
        pokerState.setTournamentSessionId(new TournamentSessionId("4"));
        tournament = new PokerTournament(pokerState);
        tournament.injectTransientDependencies(instance, support, state, historyService, backend, new DefaultSystemTime(), shutdownService,
                tournamentPlayerRegistry, sender);
        return lifeCycle;
    }

}

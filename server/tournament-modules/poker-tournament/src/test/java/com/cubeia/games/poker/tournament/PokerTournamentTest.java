package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.action.mtt.MttAction;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.MttNotifier;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.MTTSupport;
import com.cubeia.firebase.api.scheduler.Scheduler;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentLifeCycle;
import com.cubeia.games.poker.tournament.configuration.TournamentLifeCycle;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;
import com.cubeia.games.poker.tournament.util.DateFetcher;
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

    @Mock
    private PokerTournamentState pokerState;

    @Mock
    private DateFetcher dateFetcher;

    @Mock
    private MttInstance instance;

    @Mock
    private MTTSupport support;

    @Mock
    private MTTStateSupport state;

    @Mock
    private MttNotifier notifier;

    @Mock
    private Scheduler<MttAction> scheduler;

    private PokerTournament tournament;

    @Before
    public void setup() {
        initMocks(this);
        when(instance.getScheduler()).thenReturn(scheduler);
    }

    @Test
    public void registrationStartShouldBeScheduledWhenScheduledTournamentIsCreated() {
        // Given a scheduled tournament
        DateTime startTime = new DateTime(2011, 7, 5, 14, 30, 0);
        DateTime openRegistrationTime = new DateTime(2011, 7, 5, 14, 0, 0);
        TournamentLifeCycle lifeCycle = new ScheduledTournamentLifeCycle(startTime, openRegistrationTime);
        tournament = new PokerTournament(pokerState, dateFetcher, lifeCycle);
        tournament.injectTransientDependencies(instance, support, state, notifier);
        when(pokerState.getStatus()).thenReturn(PokerTournamentStatus.ANNOUNCED);
        when(dateFetcher.now()).thenReturn(new DateTime(2011, 7, 5, 13, 35, 0));

        // When the tournament is created
        tournament.tournamentCreated();

        // Then registration opening should be scheduled
        ArgumentCaptor<MttAction> captor = ArgumentCaptor.forClass(MttAction.class);
        long timeToRegistrationStart = lifeCycle.getTimeToRegistrationStart(dateFetcher.now());
        verify(scheduler).scheduleAction(captor.capture(), eq(timeToRegistrationStart));
        assertEquals(TournamentTrigger.OPEN_REGISTRATION, ((MttObjectAction) captor.getValue()).getAttachment());
    }
}

package com.cubeia.games.poker.tournament.configuration;

import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;
import org.joda.time.DateTime;

public interface TournamentLifeCycle {

    public boolean shouldStartTournament(DateTime now, int nrRegistered, int capacity);

    public boolean shouldCancelTournament(DateTime now, int nrRegistered, int capacity);

    public boolean shouldScheduleRegistrationOpening(PokerTournamentStatus status, DateTime now);

    long getTimeToRegistrationStart(DateTime now);

    boolean shouldOpenRegistration(DateTime now);
}

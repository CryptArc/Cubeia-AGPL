package com.cubeia.games.poker.tournament.configuration;

import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;
import org.joda.time.DateTime;

public class ScheduledTournamentLifeCycle implements TournamentLifeCycle {

    private DateTime startTime;

    private DateTime openRegistrationTime;

    public ScheduledTournamentLifeCycle(DateTime startTime, DateTime openRegistrationTime) {
        this.startTime = startTime;
        this.openRegistrationTime = openRegistrationTime;
    }

    @Override
    public boolean shouldStartTournament(DateTime now, int nrRegistered, int capacity) {
        return now.isAfter(startTime);
    }

    @Override
    public boolean shouldCancelTournament(DateTime now, int nrRegistered, int capacity) {
        return now.isAfter(startTime) && nrRegistered < capacity;
    }

    @Override
    public boolean shouldOpenRegistration(DateTime now) {
        return now.isAfter(openRegistrationTime);
    }

    @Override
    public boolean shouldScheduleRegistrationOpening(PokerTournamentStatus status, DateTime now) {
        return status == PokerTournamentStatus.ANNOUNCED && now.isBefore(openRegistrationTime);
    }

    @Override
    public long getTimeToRegistrationStart(DateTime now) {
        long timeToRegistrationOpening = openRegistrationTime.toDate().getTime() - now.toDate().getTime();
        // If the registration should already have opened, schedule it in one second.
        return (timeToRegistrationOpening <= 1000) ? 1000 : timeToRegistrationOpening;
    }
}

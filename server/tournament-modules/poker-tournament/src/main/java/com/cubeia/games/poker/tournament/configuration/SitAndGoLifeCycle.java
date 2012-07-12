package com.cubeia.games.poker.tournament.configuration;

import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;
import org.joda.time.DateTime;

public class SitAndGoLifeCycle implements TournamentLifeCycle {

    @Override
    public boolean shouldStartTournament(DateTime now, int nrRegistered, int capacity) {
        return nrRegistered == capacity;
    }

    @Override
    public boolean shouldCancelTournament(DateTime now, int nrRegistered, int capacity) {
        return false;
    }

    @Override
    public boolean shouldScheduleRegistrationOpening(PokerTournamentStatus status, DateTime now) {
        return false;
    }

    @Override
    public boolean shouldScheduleTournamentStart(PokerTournamentStatus status, DateTime now) {
        return false;
    }

    @Override
    public long getTimeToRegistrationStart(DateTime now) {
        return 1000;
    }

    @Override
    public long getTimeToTournamentStart(DateTime now) {
        return 1000;
    }

    @Override
    public boolean shouldOpenRegistration(DateTime now) {
        return true;
    }
}

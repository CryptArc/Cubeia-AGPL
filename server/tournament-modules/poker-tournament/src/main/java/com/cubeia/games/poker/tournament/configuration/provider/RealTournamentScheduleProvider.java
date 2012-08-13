package com.cubeia.games.poker.tournament.configuration.provider;

import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.dao.TournamentConfigurationDao;
import com.google.inject.Inject;

import java.util.Collection;

public class RealTournamentScheduleProvider implements TournamentScheduleProvider {

    private TournamentConfigurationDao dao;

    @Inject
    public RealTournamentScheduleProvider(TournamentConfigurationDao dao) {
        this.dao = dao;
    }

    @Override
    public Collection<ScheduledTournamentConfiguration> getTournamentSchedule() {
        return dao.getScheduledTournamentConfigurations();
    }
}

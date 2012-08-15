package com.cubeia.games.poker.tournament.configuration.provider;

import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.dao.TournamentConfigurationDao;
import com.google.inject.Inject;

import java.util.Collection;

public class RealSitAndGoConfigurationProvider implements SitAndGoConfigurationProvider {

    private TournamentConfigurationDao dao;

    @Inject
    public RealSitAndGoConfigurationProvider(TournamentConfigurationDao dao) {
        this.dao = dao;
    }

    @Override
    public Collection<SitAndGoConfiguration> getConfigurations() {
        return dao.getSitAndGoConfigurations();
    }
}

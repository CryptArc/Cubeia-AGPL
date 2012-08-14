package com.cubeia.games.poker.tournament.guice;

import com.cubeia.games.poker.tournament.activator.PokerActivator;
import com.cubeia.games.poker.tournament.activator.TournamentScanner;
import com.cubeia.games.poker.tournament.configuration.provider.RealTournamentScheduleProvider;
import com.cubeia.games.poker.tournament.configuration.provider.SitAndGoConfigurationProvider;
import com.cubeia.games.poker.tournament.configuration.provider.TournamentScheduleProvider;
import com.cubeia.games.poker.tournament.configuration.provider.mock.MockSitAndGoConfigurationProvider;
import com.cubeia.games.poker.tournament.dao.TournamentConfigurationDao;
import com.cubeia.games.poker.tournament.util.DateFetcher;
import com.cubeia.games.poker.tournament.util.RealDateFetcher;
import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

public class ActivatorModule extends AbstractModule {

    public void configure() {
        install(new JpaPersistModule("pokerPersistenceUnit"));
        bind(TournamentConfigurationDao.class);
        bind(TournamentScheduleProvider.class).to(RealTournamentScheduleProvider.class);
        bind(SitAndGoConfigurationProvider.class).to(MockSitAndGoConfigurationProvider.class);
        bind(DateFetcher.class).to(RealDateFetcher.class);
        bind(PokerActivator.class).to(TournamentScanner.class);
    }

}
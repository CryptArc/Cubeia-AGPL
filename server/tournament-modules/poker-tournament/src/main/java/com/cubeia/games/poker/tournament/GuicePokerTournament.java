package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.guice.tournament.Configuration;
import com.cubeia.firebase.guice.tournament.GuiceTournament;
import com.cubeia.firebase.guice.tournament.TournamentHandler;

public class GuicePokerTournament extends GuiceTournament {

    @Override
    public Configuration getConfigurationHelp() {
        return new Configuration() {
            @Override
            public Class<? extends TournamentHandler> getTournamentHandlerClass() {
                return PokerTournamentProcessor.class;
            }
        };
    }

}

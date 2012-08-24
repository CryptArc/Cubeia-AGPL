package com.cubeia.games.poker.tournament.guice;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class PersistInitializer {

    @Inject
    public PersistInitializer(PersistService service) {
        service.start();
    }

}

package com.cubeia.games.poker.tournament.guice;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class PersistInitializer {

    private PersistService service;

    @Inject
    public PersistInitializer(PersistService service) {
        this.service = service;
    }

    public void start() {
        service.start();
    }
}

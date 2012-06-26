package com.cubeia.games.poker.activator;

import com.google.inject.Injector;

public class TestablePokerActivator extends PokerActivator {

    Injector injector;

    public TestablePokerActivator(Injector injector) {
        this.injector = injector;
    }

    @Override
    protected Injector getInjector() {
        return injector;
    }
}

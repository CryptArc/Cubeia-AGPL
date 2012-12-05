package com.cubeia.game.poker.challenge.impl;

import com.cubeia.game.poker.challenge.api.ChallengeManager;
import com.google.inject.Binder;
import com.google.inject.Module;


public class ChallengeModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(ChallengeManager.class).to(ChallengeManagerImpl.class);
    }
}

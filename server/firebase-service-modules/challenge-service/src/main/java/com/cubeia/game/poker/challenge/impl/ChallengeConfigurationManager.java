package com.cubeia.game.poker.challenge.impl;


import com.cubeia.game.poker.challenge.api.ChallengeConfiguration;

import java.util.Collection;

public interface ChallengeConfigurationManager {

    Collection<ChallengeConfiguration> getConfigurations();

    ChallengeConfiguration getConfiguration(int id);
}

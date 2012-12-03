package com.cubeia.game.poker.challenge.impl;


import com.cubeia.game.poker.challenge.api.ChallengeConfiguration;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChallengeConfigurationManagerImpl implements ChallengeConfigurationManager {

    private final Map<Integer,ChallengeConfiguration> configurations = new LinkedHashMap<Integer, ChallengeConfiguration>();

    public ChallengeConfigurationManagerImpl() {

        configurations.put(1,new ChallengeConfiguration(1,"1+0.10", BigDecimal.ONE, new BigDecimal("0.10")));
        configurations.put(2,new ChallengeConfiguration(2,"5+0.50", new BigDecimal("5"), new BigDecimal("0.5")));
        configurations.put(3,new ChallengeConfiguration(3,"10+1", new BigDecimal("10"), new BigDecimal("1")));
        configurations.put(4,new ChallengeConfiguration(4,"20+2", new BigDecimal("20"), new BigDecimal("2")));
        configurations.put(5,new ChallengeConfiguration(5,"100+10", new BigDecimal("100"), new BigDecimal("10")));

    }


    @Override
    public Collection<ChallengeConfiguration> getConfigurations() {
        return configurations.values();
    }

    @Override
    public ChallengeConfiguration getConfiguration(int id) {
        return configurations.get(id);
    }
}

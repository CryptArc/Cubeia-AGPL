package com.cubeia.game.poker.challenge.impl;


import com.cubeia.game.poker.challenge.api.ChallengeConfiguration;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ChallengeConfigurationManagerImplTest {

    ChallengeConfigurationManager cm = new ChallengeConfigurationManagerImpl();

    @Test
    public void testConfigurations() {
        Collection<ChallengeConfiguration> configs = cm.getConfigurations();
        for(ChallengeConfiguration c : configs) {
            assertNotNull(cm.getConfiguration(c.getId()));
        }

        assertNull(cm.getConfiguration(23323232));
    }

}

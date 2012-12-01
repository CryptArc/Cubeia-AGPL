package com.cubeia.game.poker.challenge.impl;


import com.cubeia.game.poker.challenge.api.Challenge;
import com.cubeia.game.poker.challenge.api.ChallengeNotFoundException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;

import java.util.UUID;

public class ChallengeManagerImplTest {

    @Inject
    private ChallengeManagerImpl challengeManager;

    @Before
    public void setup() {
        Guice.createInjector(new ChallengeModule()).injectMembers(this);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateChallenge() throws ChallengeNotFoundException {
        UUID id = challengeManager.createChallenge(1, 2);

        Challenge c = challengeManager.acceptChallenge(id, 2);
        assertEquals(c.getCreator(),1);
        assertEquals(c.getInvited(),2);
        assertEquals(c.getId(),id);

    }

    @Test
    public void testIncorrectInviteID() {
        challengeManager.createChallenge(1,2);
        try {
            challengeManager.acceptChallenge(UUID.randomUUID(),2);
            fail("exception should have been thrown");
        } catch (ChallengeNotFoundException e) {
            //exception
        }

    }

    @Test
    public void testIncorrectPlayerId() {
        challengeManager.createChallenge(1,2);
        try {
            challengeManager.acceptChallenge(UUID.randomUUID(),3);
            fail("exception should have been thrown");
        } catch (ChallengeNotFoundException e) {
            //exception
        }

    }

}

package com.cubeia.game.poker.challenge.impl;

import com.cubeia.game.poker.challenge.api.Challenge;
import com.cubeia.game.poker.challenge.api.ChallengeConfiguration;
import com.cubeia.game.poker.challenge.api.ChallengeManager;
import com.cubeia.game.poker.challenge.api.ChallengeNotFoundException;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ChallengeManagerImpl implements ChallengeManager {

    private Map<UUID,Challenge> challenges = new ConcurrentHashMap<UUID, Challenge>();


    @Override
    public UUID createChallenge(int creator, int invited ,ChallengeConfiguration config) {
        Challenge c = new Challenge(UUID.randomUUID(),creator,invited,config);
        challenges.put(c.getId(),c);
        return c.getId();
    }

    @Override
    public Challenge acceptChallenge(UUID challengeId, int invited) throws ChallengeNotFoundException {
        if(!challenges.containsKey(challengeId)) {
            throw new ChallengeNotFoundException("invite with id notId");
        }
        Challenge challenge = challenges.get(challengeId);
        if(challenge.getInvited() != invited) {
            throw new ChallengeNotFoundException("Invited player id didn't match");
        }
        return challenges.get(challengeId);
    }

    @Override
    public Challenge removeChallenge(UUID challengeId) {
        return challenges.remove(challengeId);
    }


}

package com.cubeia.game.poker.challenge.api;

import java.util.UUID;

/**
 * Handles challenges
 */
public interface ChallengeManager {

    /**
     * Creates an invite
     * @param creator - playerId of the player that created the challenge
     * @param invited - the playerId of the player invited to the challenge
     * @return the challenge id that was created
     */
    UUID createChallenge(int creator, int invited);


    Challenge acceptChallenge(UUID challengeId,int invited) throws ChallengeNotFoundException;

    public Challenge removeChallenge(UUID challengeId);

}

package com.cubeia.game.poker.challenge.api;


public class ChallengeNotFoundException extends Exception {
    public ChallengeNotFoundException() {
    }

    public ChallengeNotFoundException(String message) {
        super(message);
    }

    public ChallengeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChallengeNotFoundException(Throwable cause) {
        super(cause);
    }
}

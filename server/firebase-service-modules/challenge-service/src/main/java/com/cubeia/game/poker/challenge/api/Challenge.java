package com.cubeia.game.poker.challenge.api;


import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Challenge implements Serializable {

    private static final long serialVersionUID = -6090446708071759841L;

    private final UUID id;
    private final int creator;
    private final int invited;
    private final Date created;
    private final ChallengeConfiguration configuration;


    public Challenge(UUID id, int creator, int invited, ChallengeConfiguration configuration) {
        this.id = id;
        this.creator = creator;
        this.invited = invited;
        this.configuration = configuration;
        this.created = new Date();
    }

    public int getInvited() {
        return invited;
    }

    public int getCreator() {
        return creator;
    }

    public UUID getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public ChallengeConfiguration getConfiguration() {
        return configuration;
    }
}

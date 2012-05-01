package com.cubeia.backend.cashgame;

import java.io.Serializable;

/**
 * A strongly typed backend player session identifier.
 * <p/>
 * An implementation must hold the playerId of the player this session belongs to.
 * <p/>
 * All implementations should have reliable hashCode and equals methods.
 */
public interface PlayerSessionId extends Serializable {

    int getPlayerId();
}

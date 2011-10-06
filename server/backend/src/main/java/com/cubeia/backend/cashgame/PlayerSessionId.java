package com.cubeia.backend.cashgame;

/**
 * A strongly types backend player session identifier.
 * 
 * An implementation must hold the playerId of the player this session belongs to.
 * 
 * All implementations should have reliable hashCode and equals methods.
 */
public interface PlayerSessionId {

	int getPlayerId();
}

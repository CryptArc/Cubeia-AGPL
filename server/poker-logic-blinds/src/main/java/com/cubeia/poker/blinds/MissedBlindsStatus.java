package com.cubeia.poker.blinds;

/**
 * Enum for describing which blinds have been missed.
 * 
 * A new player will have NOT_ENTERED_YET as his status.
 * 
 * @author viktor
 *
 */
public enum MissedBlindsStatus {
	NOT_ENTERED_YET, NO_MISSED_BLINDS, MISSED_SMALL_BLIND, MISSED_BIG_AND_SMALL_BLIND;
}

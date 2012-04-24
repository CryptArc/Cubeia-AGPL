package com.cubeia.backend.cashgame;

import java.io.Serializable;

/**
 * A strongly typed identifier for a backend table identifier.
 * 
 * All implementations must be Serializable and should have reliable hashCode and equals methods.
 */
public interface TableId extends Serializable {}

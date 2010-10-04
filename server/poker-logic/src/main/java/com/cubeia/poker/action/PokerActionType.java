package com.cubeia.poker.action;

/**
 * 
 * NOTE!!!! 
 * If you add a definition to this class you must also add to the protocol
 * implementation and the translation adapter code.
 * 
 * (For firebase this would be protocol.xml and ActionTransformer in the 
 * poker-game project). 
 * 
 * Failure to do so will result in missing options sent to the client!
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public enum PokerActionType {
	SMALL_BLIND, BIG_BLIND, CALL, CHECK, BET, RAISE, FOLD, DECLINE_ENTRY_BET

}

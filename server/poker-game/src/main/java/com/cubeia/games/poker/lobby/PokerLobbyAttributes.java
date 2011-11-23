package com.cubeia.games.poker.lobby;

/**
 * <p>Attributes used in the lobby on poker tables.</p>
 *  
 * <p>Use the enums using .name() to get the String representation used for the
 * lobby attribute.</p>
 * 
 * @author Fredrik Johansson, Cubeia Ltd
 */
public enum PokerLobbyAttributes {
	
	/** 
	 * <p>Int attribute. > 0 means remove from lobby asap</p>
	 * 
	 * <p><em>NOTE: If you set this flag then the table will be forcibly 
	 * removed from the system even if there are seated players.</em></p>
	 * 
	 * <p>This enum is actually mirrored from se.jadestone.dicearena.gamecontract.table.LobbyTableAttribute
	 * which is a bit of duplication, but we don't want a dependency to all that.</p>
	 */
	TABLE_READY_FOR_CLOSE,
	VISIBLE_IN_LOBBY
}

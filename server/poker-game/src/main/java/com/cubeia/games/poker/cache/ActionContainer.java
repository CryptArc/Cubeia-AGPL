package com.cubeia.games.poker.cache;

import com.cubeia.firebase.api.action.GameAction;

public class ActionContainer {
	
	private final Integer playerId;
	
	private final GameAction gameAction;
	
	private final Long timestamp = System.currentTimeMillis();

	private ActionContainer(Integer playerId, GameAction gameAction) {
		this.playerId = playerId;
		this.gameAction = gameAction;
	}

	public static ActionContainer createPublic(GameAction gameAction) {
		return new ActionContainer(null, gameAction);
	}

	public static ActionContainer createPrivate(int playerId, GameAction gameAction) {
		return new ActionContainer(playerId, gameAction);
	}

	public int getPlayerId() { return playerId; }

	public GameAction getGameAction() { return gameAction; }

	public Long getTimestamp() { return timestamp; }
	
	public boolean isPublic() {
		return playerId == null;
	}
}

package com.cubeia.games.poker.cache;

import com.cubeia.firebase.api.action.GameAction;

public class ActionContainer {

    private final Integer playerId;

    private final Integer excludedPlayerId;

    private final GameAction gameAction;

    private final Long timestamp = System.currentTimeMillis();

    private ActionContainer(Integer playerId, GameAction gameAction, Integer excludedPlayerId) {
        this.playerId = playerId;
        this.gameAction = gameAction;
        this.excludedPlayerId = excludedPlayerId;
    }

    private ActionContainer(GameAction gameAction, Integer excludedPlayerId) {
        this(null, gameAction, excludedPlayerId);
    }

    private ActionContainer(Integer playerId, GameAction gameAction) {
        this(playerId, gameAction, null);
    }

    private ActionContainer(GameAction gameAction) {
        this(null, gameAction, null);
    }

    public static ActionContainer createPublic(GameAction gameAction) {
        return new ActionContainer(null, gameAction);
    }

    public static ActionContainer createPublic(GameAction gameAction, Integer excludedPlayerId) {
        return new ActionContainer(null, gameAction, excludedPlayerId);
    }

    public static ActionContainer createPrivate(int playerId, GameAction gameAction) {
        return new ActionContainer(playerId, gameAction);
    }

    public int getPlayerId() {
        return playerId;
    }

    public Integer getExcludedPlayerId() {
        return excludedPlayerId;
    }

    public GameAction getGameAction() {
        return gameAction;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public boolean isPublic() {
        return playerId == null;
    }
}

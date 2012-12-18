package com.cubeia.games.poker.model;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.poker.player.PokerPlayer;

public interface GamePokerPlayer extends PokerPlayer {

	public PlayerSessionId getPlayerSessionId();
	
}

package com.cubeia.poker;

public interface IPokerState {

	void init(PokerSettings settings);
	
	void notifyPlayerSittingOut(int playerId);

	int getAnteLevel();

}
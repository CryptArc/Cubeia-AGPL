package com.cubeia.poker.states;

import java.io.Serializable;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;

public interface PokerGameState extends Serializable {

	public void timeout(PokerState context);

	public void act(PokerAction action, PokerState pokerGame);

	public String getStateDescription();

}

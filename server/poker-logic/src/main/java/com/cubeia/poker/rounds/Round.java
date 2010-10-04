package com.cubeia.poker.rounds;

import java.io.Serializable;

import com.cubeia.poker.action.PokerAction;

public interface Round extends Serializable {

	public void act(PokerAction action);

	public void timeout();

	public String getStateDescription();

	public boolean isFinished();

	public void visit(RoundVisitor visitor);

}

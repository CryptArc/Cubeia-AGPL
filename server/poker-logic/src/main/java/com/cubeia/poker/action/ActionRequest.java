package com.cubeia.poker.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a request for action from a player. An action request might contain
 * several options, for example "check", "bet" or "fold".
 *
 */
public class ActionRequest implements Serializable {

	private static final long serialVersionUID = 7626744583853784962L;

	private List<PossibleAction> options = new ArrayList<PossibleAction>();

	private int playerId;
	
	private long timeToAct = -1;

	public String toString() {
		return "ActionRequest pid["+playerId+"] time["+timeToAct+"] options["+options+"]";
	}
	
	public void setOptions(List<PossibleAction> options) {
		this.options  = options;
	}

	public Iterable<PossibleAction> getOptions() {
		return options;
	}

	public boolean matches(PokerAction action) {
		// TODO: Temp impl, add validation of amounts.
		return isOptionEnabled(action.getActionType());
	}

	public int getPlayerId() {
		return playerId;
	}
	
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public long getTimeToAct() {
		return timeToAct;
	}

	public void setTimeToAct(long timeToAct) {
		this.timeToAct = timeToAct;
	}

	public boolean isOptionEnabled(PokerActionType option) {
		for (PossibleAction action : options) {
			if (action.allows(option)) {
				return true;
			}
		}
		return false;
	}

	public void enable(PossibleAction option) {
		options.add(option);
	}

	public PossibleAction getOption(PokerActionType type) {
		PossibleAction result = null;
		for (PossibleAction option : options) {
			if (option.allows(type)) {
				result = option;
			}
		}
		return result;
	}
}
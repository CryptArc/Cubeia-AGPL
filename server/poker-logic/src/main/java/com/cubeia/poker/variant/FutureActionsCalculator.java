package com.cubeia.poker.variant;

import java.util.ArrayList;
import java.util.List;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

public class FutureActionsCalculator  {

	

	/**
	 * Calculate what a player can do in the future given that the state does not change.
	 * i.e. the check next and fold next checkboxes
	 * @param player
	 * @return
	 */
	public List<PokerActionType> calculateFutureActionOptionList(PokerPlayer player, Long highestBet) {
		List<PokerActionType> options = new ArrayList<PokerActionType>();
		
		// players that are all in or has folded should not have anything
		if (player.hasFolded() || player.isAllIn())	{
			return options;
		}
		
		if (player.getBetStack() >= highestBet)	{
			options.add(PokerActionType.CHECK);
		}
		
		options.add(PokerActionType.FOLD);
		
		
		return options;
	}
	
	/* (non-Javadoc)
	 * @see com.cubeia.poker.variant.FutureActionsCalculator#getEmptyFutureOptionList()
	 */
	public List<PokerActionType> getEmptyFutureOptionList() {
		return new ArrayList<PokerActionType>();
	}
}

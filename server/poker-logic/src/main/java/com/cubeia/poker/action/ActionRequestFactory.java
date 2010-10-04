package com.cubeia.poker.action;

import java.io.Serializable;
import java.util.Arrays;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.BetStrategy;

public class ActionRequestFactory implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final BetStrategy betStrategy;
	
	public ActionRequestFactory(BetStrategy betStrategy) {
		this.betStrategy = betStrategy;
	}
	
	public ActionRequest createFoldCallRaiseActionRequest(PokerPlayer p) {
		PossibleAction fold = new PossibleAction(PokerActionType.FOLD, 0);
		PossibleAction call = new PossibleAction(PokerActionType.CALL, betStrategy.getCallAmount(p));
		PossibleAction raise = new PossibleAction(PokerActionType.RAISE, betStrategy.getMinRaiseToAmount(p), betStrategy.getMaxRaiseToAmount(p));
		
		ActionRequest request = new ActionRequest();
		/* We will check:
		 * 1. If the player have more cash than a call
		 * 2. If raise min amount is > call (can be 0 if all other players are all in)
		 */
		if (p.getBalance() > call.getMinAmount() && raise.getMinAmount() > call.getMinAmount()) {
			request.setOptions(Arrays.asList(fold, call, raise));
		} else {
			request.setOptions(Arrays.asList(fold, call));
		}
		request.setPlayerId(p.getId());
		return request;
	}
	

	public ActionRequest createFoldCheckBetActionRequest(PokerPlayer p) {
		PossibleAction fold = new PossibleAction(PokerActionType.FOLD, 0);
		PossibleAction check = new PossibleAction(PokerActionType.CHECK, 0);
		PossibleAction bet = new PossibleAction(PokerActionType.BET, betStrategy.getMinBetAmount(p), betStrategy.getMaxBetAmount(p));
		ActionRequest request = new ActionRequest();
		request.setOptions(Arrays.asList(fold, check, bet));
		request.setPlayerId(p.getId());
		return request;		
	}

}

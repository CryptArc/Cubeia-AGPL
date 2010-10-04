package com.cubeia.poker.rounds.blinds;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

public class WaitingForEntryBetState extends AbstractBlindsState {

	private static final long serialVersionUID = 1L;

	public void bigBlind(int playerId, BlindsRound blindsRound) {
		PokerPlayer player = blindsRound.getGame().getPlayer(playerId);
		if (player.getActionRequest().isOptionEnabled(PokerActionType.BIG_BLIND)) {
			player.setHasPostedEntryBet(true);
			player.addBet(100);
			blindsRound.bigBlindPosted();
		} else {
			throw new IllegalArgumentException("Player " + player + " is not allowed to post big blind. Options were " + player.getActionRequest());
		}
	}

	public void declineEntryBet(Integer playerId, BlindsRound blindsRound) {
		PokerPlayer player = blindsRound.getGame().getPlayer(playerId);
		if (player.getActionRequest().isOptionEnabled(PokerActionType.DECLINE_ENTRY_BET)) {
			blindsRound.entryBetDeclined(player);
		} else {
			throw new IllegalArgumentException("Player " + player + " is not allowed to decline entry bet.");
		}
	}

	public void timeout(BlindsRound context) {
		PokerPlayer nextEntryBetter = context.getNextEntryBetter();
		declineEntryBet(nextEntryBetter.getId(), context);
	}

}

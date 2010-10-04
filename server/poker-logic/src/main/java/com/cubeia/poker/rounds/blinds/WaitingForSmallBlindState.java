package com.cubeia.poker.rounds.blinds;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;

public class WaitingForSmallBlindState extends AbstractBlindsState {

	private static final long serialVersionUID = 4983163822097132780L;
	
	@Override
	public void smallBlind(int playerId, BlindsRound context) {
		int smallBlind = context.getBlindsInfo().getSmallBlindPlayerId();
		if (smallBlind == playerId) {
			PokerPlayer player = context.getGame().getPlayer(playerId);
			player.addBet(context.getBlindsInfo().getSmallBlindLevel());
			context.smallBlindPosted();
		} else {
			throw new IllegalArgumentException("Expected player " + smallBlind + " to act, but got action from " + playerId);
		}
		
	}
	
	@Override
	public void declineEntryBet(Integer playerId, BlindsRound context) {
		int smallBlind = context.getBlindsInfo().getSmallBlindPlayerId();
		if (smallBlind == playerId) {
			PokerPlayer player = context.getGame().getPlayer(playerId);
			player.setSitOutStatus(SitOutStatus.MISSED_SMALL_BLIND);
			context.getBlindsInfo().setHasDeadSmallBlind(true);
			context.smallBlindDeclined(player);
		} else {
			throw new IllegalArgumentException("Expected player " + smallBlind + " to act, but got action from " + playerId);
		}		
	}
	
	@Override
	public void timeout(BlindsRound context) {
		if (context.isTournamentBlinds()) {
			smallBlind(context.getBlindsInfo().getSmallBlindPlayerId(), context);
		} else {
			int smallBlind = context.getBlindsInfo().getSmallBlindPlayerId();
			PokerPlayer player = context.getGame().getPlayer(smallBlind);
			player.setSitOutStatus(SitOutStatus.MISSED_SMALL_BLIND);
			context.getBlindsInfo().setHasDeadSmallBlind(true);
			context.smallBlindDeclined(player);
		}
	}
}

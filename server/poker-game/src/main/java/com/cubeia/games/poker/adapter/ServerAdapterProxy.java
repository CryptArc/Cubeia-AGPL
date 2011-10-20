package com.cubeia.games.poker.adapter;

import java.util.Collection;
import java.util.List;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.rake.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.tournament.RoundReport;

public abstract class ServerAdapterProxy implements ServerAdapter {

	public abstract ServerAdapter getAdaptee();
	
	@Override
	public void notifyBuyInInfo(int playerId, boolean mandatoryBuyin) {
		if(getAdaptee() != null) {
			getAdaptee().notifyBuyInInfo(playerId, mandatoryBuyin);
		}
	}
	
	@Override
	public void scheduleTimeout(long millis) {
		if(getAdaptee() != null) {
			getAdaptee().scheduleTimeout(millis);
		}
	}

	@Override
	public void requestAction(ActionRequest request) {
		if(getAdaptee() != null) {
			getAdaptee().requestAction(request);
		}
	}

	@Override
	public void notifyCommunityCards(List<Card> cards) {
		if(getAdaptee() != null) {
			getAdaptee().notifyCommunityCards(cards);
		}
	}

	@Override
	public void notifyDealerButton(int seatId) {
		if(getAdaptee() != null) {
			getAdaptee().notifyDealerButton(seatId);
		}
	}

	@Override
	public void notifyPrivateCards(int playerId, List<Card> cards) {
		if(getAdaptee() != null) {
			getAdaptee().notifyPrivateCards(playerId, cards);
		}
	}

	@Override
	public void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand) {
		if(getAdaptee() != null) {
			getAdaptee().notifyBestHand(playerId, handType, cardsInHand);
		}
	}

	@Override
	public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
		if(getAdaptee() != null) {
			getAdaptee().notifyPrivateExposedCards(playerId, cards);
		}
	}

	@Override
	public void exposePrivateCards(int playerId, List<Card> cards) {
		if(getAdaptee() != null) {
			getAdaptee().exposePrivateCards(playerId, cards);
		}
	}

	@Override
	public void notifyNewHand() {
		if(getAdaptee() != null) {
			getAdaptee().notifyNewHand();
		}
	}
	
	@Override
	public void notifyRakeInfo(RakeInfoContainer rakeInfoContainer) {
        if(getAdaptee() != null) {
            getAdaptee().notifyRakeInfo(rakeInfoContainer);
        }
	}

	@Override
	public void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus) {
		if(getAdaptee() != null) {
			getAdaptee().notifyHandEnd(handResult, handEndStatus);
		}
	}

	@Override
	public void notifyPlayerBalance(PokerPlayer player) {
		if(getAdaptee() != null) {
			getAdaptee().notifyPlayerBalance(player);
		}
	}

	@Override
	public void notifyActionPerformed(PokerAction action, long resultingBalance) {
		if(getAdaptee() != null) {
			getAdaptee().notifyActionPerformed(action, resultingBalance);
		}
	}
	
	@Override
	public void notifyNewRound() {
		if(getAdaptee() != null) {
			getAdaptee().notifyNewRound();
		}
	}

	@Override
	public void reportTournamentRound(RoundReport report) {
		if(getAdaptee() != null) {
			getAdaptee().reportTournamentRound(report);
		}
	}

	@Override
	public void cleanupPlayers() {
		if(getAdaptee() != null) {
			getAdaptee().cleanupPlayers();
		}
	}

	@Override
	public void notifyPotUpdates(Collection<Pot> iterable, Collection<PotTransition> potTransitions) {
		if(getAdaptee() != null) {
			getAdaptee().notifyPotUpdates(iterable, potTransitions);
		}
	}

	@Override
	public void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status) {
		if(getAdaptee() != null) {
			getAdaptee().notifyPlayerStatusChanged(playerId, status);
		}
	}

	@Override
	public void notifyDeckInfo(int size, Rank rankLow) {
		if(getAdaptee() != null) {
			getAdaptee().notifyDeckInfo(size, rankLow);
		}
	}
}

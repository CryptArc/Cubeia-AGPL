/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.gametypes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import ca.ualberta.cs.poker.Card;
import ca.ualberta.cs.poker.Deck;
import ca.ualberta.cs.poker.Hand;

import com.cubeia.poker.GameType;
import com.cubeia.poker.IPokerState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.model.PlayerHands;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.rounds.AnteRound;
import com.cubeia.poker.rounds.AnteRoundHelper;
import com.cubeia.poker.rounds.BettingRound;
import com.cubeia.poker.rounds.DealCommunityCardsRound;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.rounds.blinds.BlindsRound;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.util.HandResultCalculator;

public class Telesina implements GameType, RoundVisitor {

	private static final long serialVersionUID = -1523110440727681601L;

    private static transient Logger log = Logger.getLogger(Telesina.class);

	private Round currentRound;

	private Deck deck;

	/**
	 * 0 = pre flop 1 = flop 2 = turn 3 = river
	 */
	private int roundId;

	private BlindsInfo blindsInfo = new BlindsInfo();

	private final PokerState state;
	
	// TODO: random should be injected
	private Random random = new Random();
	
	private HandResultCalculator handResultCalculator = new HandResultCalculator();

	public Telesina(PokerState state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
	    return "Telesina, current round["+currentRound+"] roundId["+roundId+"] ";
	}
	
	@Override
	public void startHand() {
		log.debug("start hand");
		initHand();
	}

	private void initHand() {	
		log.debug("init hand");
		
		// TODO: use Telesina deck here, size of deck is determined by table size (or players in hand)
		deck = new Deck(getRandom().nextInt());
		// FIXME: Use better seed for the shuffle
		deck.shuffle();
		
		blindsInfo.setAnteLevel(state.getAnteLevel());
		
		currentRound = new AnteRound(this, new AnteRoundHelper());
		
//		// TODO: put last in ante round (see below)
//		dealPocketCards();
//		dealExposedCards();
		
		roundId = 0;
	}

	private Random getRandom() {
		return random;
	}

	@Override
	public void act(PokerAction action) {
		currentRound.act(action);
		checkFinishedRound();
	}

	private void checkFinishedRound() {
		if (currentRound.isFinished()) {
			handleFinishedRound();
		}
	}

	private void dealPocketCards(PokerPlayer p, int n) {
		for (int i = 0; i < n; i++) {
			p.getPocketCards().addCard(deck.dealCard());
		}
		state.notifyPrivateCards(p.getId(), p.getPocketCards().getCards());
	}
	
	private void dealExposedCards(PokerPlayer p, int n) {
		ArrayList<Card> cardsDealt = new ArrayList<Card>();
		for (int i = 0; i < n; i++) {
			Card card = deck.dealCard();
			cardsDealt.add(card);
			p.getPocketCards().addCard(card);
		}
		
		state.notifyPrivateCards(p.getId(), cardsDealt);
		state.exposePrivateCards(p.getId(), cardsDealt);
	}

	private void dealCommunityCards(int n) {
		List<Card> dealt = new LinkedList<Card>();
		for (int i = 0; i < n; i++) {
			dealt.add(deck.dealCard());
		}
		state.getCommunityCards().addAll(dealt);
		state.notifyCommunityCards(dealt);
	}

	public void handleFinishedRound() {
		log.debug("handle finished round");
		currentRound.visit(this);
	}
	
	private void reportPotUpdate() {
        state.updatePot();
    }

    /**
	 * Expose all pocket cards for players still in the hand
	 * i.e. not folded.
	 */
	private void exposeShowdownCards() {
        if (state.countNonFoldedPlayers() > 1) {
            for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
                if (!p.hasFolded()) {
                    state.exposePrivateCards(p.getId(), p.getPocketCards().getCards());
                }
            }
        }
    }

    private void startBettingRound() {
    	log.trace("Starting new betting round. Round ID: "+(roundId+1));
		currentRound = new BettingRound(this, blindsInfo.getDealerButtonSeatId());
		roundId++;
	}
    
	private boolean isHandFinished() {
		return (roundId >= 3 || state.countNonFoldedPlayers() <= 1);
	}

	public int countPlayersSittingIn() {
		int sittingIn = 0;
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			if (!p.isSittingOut()) {
				sittingIn++;
			}
		}

		return sittingIn;
	}	

	public void dealCommunityCards() {
		if (roundId == 0) {
			dealCommunityCards(3);
		} else {
			dealCommunityCards(1);
		}
	}

	private void handleFinishedHand(HandResult handResult) {	
		log.debug("Hand over. Result: "+handResult.getPlayerHands());
		state.notifyHandFinished(handResult, HandEndStatus.NORMAL);
	}
	
	private void handleCanceledHand() {
		state.notifyHandFinished(new HandResult(), HandEndStatus.CANCELED_TOO_FEW_PLAYERS);
	}	

	private HandResult createHandResult() {
		HandResult result = new HandResult();
		PlayerHands playerHands = createHandHolder();
		result.setPlayerHands(playerHands);
		Map<PokerPlayer, Result> playerResults = handResultCalculator.getPlayerResults(result.getPlayerHands(), state.getPotHolder(), 
				state.getCurrentHandPlayerMap());
		result.setResults(playerResults);
		return result;
	}

	private PlayerHands createHandHolder() {
		PlayerHands holder = new PlayerHands();
		for (PokerPlayer player : state.getCurrentHandPlayerMap().values()) {
			if (!player.hasFolded()) {
				Hand h = new Hand();
				h.addCards(player.getPocketCards().getCards());
				h.addCards(state.getCommunityCards());
				holder.addHand(player.getId(), h);
			}
		}

		return holder;
	}

	private void moveChipsToPot() {
		
		state.getPotHolder().moveChipsToPot(state.getCurrentHandSeatingMap().values());
		
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			p.setHasActed(false);
			p.clearActionRequest();
			p.commitBetStack();
		}
	}

	@Override
	public void requestAction(ActionRequest r) {
//		if (blindRequested(r)  &&  state.isTournamentTable()) {
//			state.getServerAdapter().scheduleTimeout(state.getTimingProfile().getTime(Periods.AUTO_POST_BLIND_DELAY));
//		} else {
		state.requestAction(r);
//		}
	}
	
	@Override
	public void scheduleRoundTimeout() {
		log.debug("scheduleRoundTimeout in: "+ state.getTimingProfile().getTime(Periods.RIVER));
		state.getServerAdapter().scheduleTimeout(state.getTimingProfile().getTime(Periods.RIVER));
	}

//	private boolean blindRequested(ActionRequest r) {
//		return r.isOptionEnabled(PokerActionType.SMALL_BLIND) || r.isOptionEnabled(PokerActionType.BIG_BLIND);
//	}

	@Override
	public BlindsInfo getBlindsInfo() {
		return blindsInfo;
	}

	@Override
	public void prepareNewHand() {
		state.getCommunityCards().clear();
		for (PokerPlayer player : state.getCurrentHandPlayerMap().values()) {
			player.clearHand();
			player.setHasFolded(false);
		}		
	}

	@Override
	public ServerAdapter getServerAdapter() {
		return state.getServerAdapter();
	}

	@Override
	public void timeout() {
		log.debug("Timeout");
		currentRound.timeout();
		checkFinishedRound();
	}

	@Override
	public IPokerState getState() {
		return state;
	}

	@Override
	public String getStateDescription() {
		return currentRound == null ? "th-round=null" : currentRound.getClass() + "_" + currentRound.getStateDescription();
	}

	@Override
	public void visit(AnteRound anteRound) {
		log.debug("visit ante round");
		
		if (anteRound.isCanceled()) {
		    handleCanceledHand();
		} else {
		    moveChipsToPot();
		    reportPotUpdate();
		    
		    dealPocketCards();
		    dealExposedCards();
		    
		    prepareBettingRound();
		}
	}
	
	@Override
	public void visit(BettingRound bettingRound) {
		moveChipsToPot();
		reportPotUpdate();
		
		if (isHandFinished()) {
		    exposeShowdownCards();
			handleFinishedHand(createHandResult());
			state.getPotHolder().clearPots();
		} else {
			// Start deal community cards round
			currentRound = new DealCommunityCardsRound(this);
			// Schedule timeout for the community cards round
			scheduleRoundTimeout();
		}		
	}

	@Override
	public void visit(BlindsRound blindsRound) {
		throw new UnsupportedOperationException("blinds round not supported in telesina");
//		if (blindsRound.isCanceled()) {
//			handleCanceledHand();
//		} else {
//			updateBlindsInfo(blindsRound);
//			dealPocketCards();
//			prepareBettingRound();
//		}
	}
	
	@Override
	public void visit(DealCommunityCardsRound round) {
		startBettingRound();
	}

	private void prepareBettingRound() {
		currentRound = new BettingRound(this, getBlindsInfo().getDealerButtonSeatId());
	}

	private void updateBlindsInfo(BlindsRound blindsRound) {
		this.blindsInfo = blindsRound.getBlindsInfo();
	}

	private void dealPocketCards() {
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			if (!p.isSittingOut()) {
				dealPocketCards(p, 1);
			}
		}
	}

	private void dealExposedCards() {
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			if (!p.isSittingOut()) {
				dealExposedCards(p, 1);
			}
		}
	}
}

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

package com.cubeia.poker.variant.texasholdem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cubeia.poker.GameType;
import com.cubeia.poker.IPokerState;
import com.cubeia.poker.PokerSettings;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Deck;
import com.cubeia.poker.hand.IndexCardIdGenerator;
import com.cubeia.poker.hand.Shuffler;
import com.cubeia.poker.hand.StandardDeck;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.RevealOrderCalculator;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.DealCommunityCardsRound;
import com.cubeia.poker.rounds.DealExposedPocketCardsRound;
import com.cubeia.poker.rounds.DealInitialPocketCardsRound;
import com.cubeia.poker.rounds.ExposePrivateCardsRound;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.betting.DefaultPlayerToActCalculator;
import com.cubeia.poker.rounds.betting.NoLimitBetStrategy;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.rounds.blinds.BlindsRound;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.util.HandResultCalculator;
import com.cubeia.poker.variant.HandResultCreator;

public class TexasHoldem implements GameType, RoundVisitor {

	private static final long serialVersionUID = -1523110440727681601L;

    private static transient Logger log = Logger.getLogger(TexasHoldem.class);

	private Round currentRound;

	private Deck deck;

	/**
	 * 0 = pre flop 1 = flop 2 = turn 3 = river
	 */
	private int roundId;

	private BlindsInfo blindsInfo = new BlindsInfo();

//	@Inject
	private final PokerState state;
	
	private final RNGProvider rngProvider;
	
	private HandResultCalculator handResultCalculator = new HandResultCalculator(Collections.reverseOrder(new TexasHoldemHandComparator()));

	public TexasHoldem(RNGProvider rngProvider, PokerState state) {
	    this.rngProvider = rngProvider;
		this.state = state;
		
	}
	
	@Override
	public String toString() {
	    return "TexasHoldem, current round["+currentRound+"] roundId["+roundId+"] ";
	}
	
	@Override
	public void startHand() {
		initHand();
	}

	private void initHand() {				
		deck = new StandardDeck(new Shuffler<Card>(rngProvider.getRNG()), new IndexCardIdGenerator());
		
		currentRound = new BlindsRound(this, state.isTournamentTable());
		roundId = 0;
	}

//	@Override
//	public SortedMap<Integer, PokerPlayer> getSeatingMap() {
//		return seatingMap;
//	}

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
		    p.addPocketCard(deck.deal(), false);
		}
		state.notifyPrivateCards(p.getId(), p.getPocketCards().getCards());
	}

	private void dealCommunityCards(int n) {
		List<Card> dealt = new LinkedList<Card>();
		for (int i = 0; i < n; i++) {
			dealt.add(deck.deal());
		}
		state.getCommunityCards().addAll(dealt);
		state.notifyCommunityCards(dealt);
	}

//	@Override
//	public PokerPlayer getPlayer(int playerId) {
//		PokerPlayer pokerPlayer = game.getCurrentHandPlayerMap().get(playerId);
//		return pokerPlayer;
//	}

//	@Override
//	public Iterable<PokerPlayer> getPlayers() {
//		return game.getCurrentHandPlayerMap().values();
//	}

	public void handleFinishedRound() {
		currentRound.visit(this);
	}
	
	private void reportPotUpdate() {
        state.notifyPotAndRakeUpdates(Collections.<PotTransition>emptyList());
    }
	
	public int getCurrentRoundId(){
		return roundId;
	}

    private void startBettingRound() {
    	log.trace("Starting new betting round. Round ID: "+(roundId+1));
		currentRound = new BettingRound(this, blindsInfo.getDealerButtonSeatId(), new DefaultPlayerToActCalculator(), 
		    new ActionRequestFactory(new NoLimitBetStrategy()));
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

	private void moveChipsToPot() {
		
		state.getPotHolder().moveChipsToPotAndTakeBackUncalledChips(state.getCurrentHandSeatingMap().values());
		
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			p.setHasActed(false);
			p.clearActionRequest();
		}
	}

	@Override
	public void requestAction(ActionRequest r) {
		if (blindRequested(r) && state.isTournamentTable()) {
			state.getServerAdapter().scheduleTimeout(state.getTimingProfile().getTime(Periods.AUTO_POST_BLIND_DELAY));
		} else {
			state.requestAction(r);
		}
	}
	
    @Override
    public void requestMultipleActions(Collection<ActionRequest> requests) {
        throw new UnsupportedOperationException("sending multiple action requests not implemented");
    }
	
	@Override
	public void scheduleRoundTimeout() {
		log.debug("scheduleRoundTimeout in: "+ state.getTimingProfile().getTime(Periods.RIVER));
		state.getServerAdapter().scheduleTimeout(state.getTimingProfile().getTime(Periods.RIVER));
	}

	private boolean blindRequested(ActionRequest r) {
		return r.isOptionEnabled(PokerActionType.SMALL_BLIND) || r.isOptionEnabled(PokerActionType.BIG_BLIND);
	}

//	public void requestAction(PokerPlayer player,
//			PossibleAction ... possibleActions) {
//		ActionRequest actionRequest = new ActionRequest();
//		List<PossibleAction> options = new ArrayList<PossibleAction>();
//
//		for (PossibleAction action : possibleActions) {
//			options.add(action);
//		}
//
//		actionRequest.setOptions(options);
//		actionRequest.setPlayerId(player.getId());
//		player.setActionRequest(actionRequest);
//		logDebug("Requesting action " + actionRequest);
//		game.requestAction(actionRequest);
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
	public void visit(BettingRound bettingRound) {
		moveChipsToPot();
		reportPotUpdate();
		
		if (isHandFinished()) {
		    state.exposeShowdownCards();
		    PokerPlayer playerAtDealerButton = state.getPlayerAtDealerButton();
		    List<Integer> playerRevealOrder = new RevealOrderCalculator().calculateRevealOrder(state.getCurrentHandSeatingMap(), state.getLastPlayerToBeCalled(), playerAtDealerButton);
		    Set<PokerPlayer> muckingPlayers = new HashSet<PokerPlayer>();
            HandResult handResult = new HandResultCreator(new TexasHoldemHandCalculator()).createHandResult(state.getCommunityCards(), handResultCalculator, state.getPotHolder(), state.getCurrentHandPlayerMap(), playerRevealOrder, muckingPlayers);
            
            handleFinishedHand(handResult);
			state.getPotHolder().clearPots();
		} else {		
			// Start deal community cards round
			currentRound = new DealCommunityCardsRound(this);
			// Schedule timeout for the community cards round
			scheduleRoundTimeout();
		}		
	}
	
	@Override
	public void visit(ExposePrivateCardsRound exposePrivateCardsRound) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void visit(AnteRound anteRound) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public void visit(BlindsRound blindsRound) {
		if (blindsRound.isCanceled()) {
			handleCanceledHand();
		} else {
			updateBlindsInfo(blindsRound);
			dealPocketCards();
			prepareBettingRound();
		}
	}
	
	@Override
	public void visit(DealCommunityCardsRound round) {
		startBettingRound();
	}

	@Override
	public void visit(DealExposedPocketCardsRound round) {
	    throw new UnsupportedOperationException(round.getClass().getSimpleName() + " round not allowed in Texas Holdem");
	}
	
	@Override
	public void visit(DealInitialPocketCardsRound round) {
	    throw new UnsupportedOperationException(round.getClass().getSimpleName() + " round not allowed in Texas Holdem");
	}
	
	private void prepareBettingRound() {
		int bbSeatId = blindsInfo.getBigBlindSeatId();
		currentRound = new BettingRound(this, bbSeatId, new DefaultPlayerToActCalculator(), new ActionRequestFactory(new NoLimitBetStrategy()));
	}

	private void updateBlindsInfo(BlindsRound blindsRound) {
		this.blindsInfo = blindsRound.getBlindsInfo();
	}

	private void dealPocketCards() {
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			if (!p.isSittingOut()) {
				dealPocketCards(p, 2);
			}
		}
	}
	

	@Override
	// FIXME: Implement for Texas Hold'em.
	public void sendAllNonFoldedPlayersBestHand() {
		log.warn("Implement sendAllNonFoldedPlayersBestHand for Texas Hold'em.");
	}
	
	@Override
	public boolean canPlayerBuyIn(PokerPlayer player, PokerSettings settings) {
	    // TODO: check if players balance + pending balance is big enough to pay ante/small/big blind
	    return true;
	}
}

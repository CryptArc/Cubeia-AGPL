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

package com.cubeia.poker.variant.telesina;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.poker.GameType;
import com.cubeia.poker.IPokerState;
import com.cubeia.poker.PokerSettings;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.Rank;
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
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.rounds.blinds.BlindsRound;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.util.HandResultCalculator;
import com.cubeia.poker.variant.HandResultCreator;
import com.google.common.annotations.VisibleForTesting;

/**
 * Telesina game.
 * 
 * Game rounds:
 *  1  AnteRound, 1 private + 1 public pocket cards
 *  2  BettingRound 
 *  3  DealPocketCardsRound, 1 + 2
 *  4  BettingRound
 *  5  DealPocketCardsRound, 1 + 3
 *  6  BettingRound 
 *  7  DealPocketCardsRound, 1 + 4
 *  8  BettingRound 
 *  9  DealVelaCard, 1 + 4 & 1 community (vela)
 * 10  Betting round
 * 
 */
public class Telesina implements GameType, RoundVisitor {

	private static final int VELA_ROUND_ID = 4;

	private static final long serialVersionUID = -1523110440727681601L;

	private static transient Logger log = LoggerFactory.getLogger(Telesina.class);

	private Round currentRound;

	private TelesinaDeck deck;

	/**
	 * Betting round sequence id:
	 * 0 -> ante (1 private + 1 public pocket cards)
	 * 1 -> betting round 1 (1 + 2)
	 * 2 -> betting round 2 (1 + 3)
	 * 3 -> betting round 3 (1 + 4)
	 * 4 -> vela betting round (1 + 4 + vela)
	 * 
	 */
	private int bettingRoundId;

	@VisibleForTesting
	BlindsInfo blindsInfo = new BlindsInfo();

	final PokerState state;
	
	private final RNGProvider rngProvider;
	
    private final TelesinaDeckFactory deckFactory;

    private final TelesinaRoundFactory roundFactory;

	public Telesina(RNGProvider rng, PokerState state, TelesinaDeckFactory deckFactory, TelesinaRoundFactory roundFactory) {
		this.rngProvider = rng;
        this.state = state;
        this.deckFactory = deckFactory;
        this.roundFactory = roundFactory;

	}
	
	@Override
	public String toString() {
	    return "Telesina, current round["+getCurrentRound()+"] roundId["+getBettingRoundId()+"] ";
	}
	
	@Override
	public void startHand() {
		log.debug("start hand");
		initHand();
	}

	private void initHand() {	
		log.debug("init hand");
		resetPlayerPostedEntryBets();
		deck = deckFactory.createNewDeck(rngProvider.getRNG(), state.getTableSize());
		try {
		state.notifyDeckInfo(deck.getTotalNumberOfCardsInDeck(), deck.getDeckLowestRank());
		} catch(Throwable th) {
			th.printStackTrace();
		}
		blindsInfo.setAnteLevel(state.getAnteLevel());
		setCurrentRound(roundFactory.createAnteRound(this));
		resetBettingRoundId();
	}

    protected void resetPlayerPostedEntryBets() {
        for (PokerPlayer p : getState().getCurrentHandSeatingMap().values()) {
            p.setHasPostedEntryBet(false);
        }
    }
	
	@Override
	public void act(PokerAction action) {
		getCurrentRound().act(action);
		checkFinishedRound();
	}

	private void checkFinishedRound() {
		if (getCurrentRound().isFinished()) {
			handleFinishedRound();
		}
	}

	private void dealHiddenPocketCards(PokerPlayer p, int n) {
        ArrayList<Card> cardsDealt = new ArrayList<Card>();
		for (int i = 0; i < n; i++) {
		    Card card = deck.deal();
		    cardsDealt.add(card);
            p.addPocketCard(card, false);
		}
		log.debug("notifying user {} of private cards: {}", p.getId(), cardsDealt);
		state.notifyPrivateCards(p.getId(), cardsDealt);
	}
	
	private void dealExposedPocketCards(PokerPlayer player, int n) {
		ArrayList<Card> cardsDealt = new ArrayList<Card>();
		for (int i = 0; i < n; i++) {
			Card card = deck.deal();
			cardsDealt.add(card);
			player.addPocketCard(card, true);
		}
        log.debug("notifying all users of private exposed cards to {}: {}", player.getId(), cardsDealt);
		state.notifyPrivateExposedCards(player.getId(), cardsDealt);
	}

	private void dealCommunityCards(int n) {
		List<Card> dealt = new LinkedList<Card>();
		for (int i = 0; i < n; i++) {
			dealt.add(deck.deal());
		}
		state.getCommunityCards().addAll(dealt);
		state.notifyCommunityCards(dealt);
		
        TelesinaHandStrengthEvaluator handStrengthEvaluator = new TelesinaHandStrengthEvaluator(getDeckLowestRank());       
		
		for (PokerPlayer player : state.getCurrentHandPlayerMap().values()) {
		    calculateAndSendBestHandToPlayer(handStrengthEvaluator, player);
		}
	}

	public void handleFinishedRound() {
		log.debug("handle finished round: {}", getCurrentRound());
		getCurrentRound().visit(this);
	}
	
	private void reportPotAndRakeUpdates(Collection<PotTransition> potTransitions) {
        state.notifyPotAndRakeUpdates(potTransitions);
    }

    private void startBettingRound() {
		setCurrentRound(roundFactory.createBettingRound(this, blindsInfo.getDealerButtonSeatId()));
		incrementBettingRoundId();
		log.debug("started new betting round, betting round id = {}", getBettingRoundId());
		state.notifyNewRound();
	}

    @VisibleForTesting
    protected boolean isHandFinished() {
		return (getBettingRoundId() >= 5 || state.countNonFoldedPlayers() <= 1);
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
	    dealCommunityCards(1);
	}

	private void handleFinishedHand(HandResult handResult) {	
		log.debug("Hand over. Result: "+handResult.getPlayerHands());
		state.notifyHandFinished(handResult, HandEndStatus.NORMAL);
	}
	
	@VisibleForTesting
	protected void handleCanceledHand() {
	    log.debug("hand canceled in round {}: {}", getCurrentRound(), HandEndStatus.CANCELED_TOO_FEW_PLAYERS);
		state.notifyHandFinished(new HandResult(), HandEndStatus.CANCELED_TOO_FEW_PLAYERS);
		
		// return antes
		for (PokerPlayer p : state.getCurrentHandPlayerMap().values()) {
		    p.returnAllBets();
		    state.notifyPlayerBalance(p.getId());
		}
		
		state.notifyRakeInfo();
		
	}

	private Collection<PotTransition> moveChipsToPot() {
		Collection<PotTransition> potTransitions = state.getPotHolder().moveChipsToPot(state.getCurrentHandSeatingMap().values());
		
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			p.setHasActed(false);
			p.clearActionRequest();
		}
		
		return potTransitions;
	}

	@Override
	public void requestAction(ActionRequest r) {
		state.requestAction(r);
	}
	
	@Override
	public void requestMultipleActions(Collection<ActionRequest> requests) {
	    state.requestMultipleActions(requests);
	}
	
	@Override
	public void scheduleRoundTimeout() {
		log.debug("scheduleRoundTimeout in: "+ state.getTimingProfile().getTime(Periods.RIVER));
		state.getServerAdapter().scheduleTimeout(state.getTimingProfile().getTime(Periods.RIVER));
	}

	@Override
	public BlindsInfo getBlindsInfo() {
		return blindsInfo;
	}

	@Override
	public void prepareNewHand() {
		state.getCommunityCards().clear();
	}

	@Override
	public ServerAdapter getServerAdapter() {
		return state.getServerAdapter();
	}

	@Override
	public void timeout() {
		log.debug("Timeout");
		getCurrentRound().timeout();
		checkFinishedRound();
	}

	@Override
	public IPokerState getState() {
		return state;
	}

	@Override
	public String getStateDescription() {
		return getCurrentRound() == null ? "th-round=null" : getCurrentRound().getClass() + "_" + getCurrentRound().getStateDescription();
	}

	@Override
	public void visit(AnteRound anteRound) {
	    // TODO: remove this stuff when done testing crashes!!!
	    if (System.getProperty("crash") != null) {
	        throw new RuntimeException("Panic! Omg!");
	    }
	    
		if (anteRound.isCanceled()) {
		    handleCanceledHand();
		} else {
		    log.debug("ante round finished");
		    
		    Collection<PotTransition> potTransitions = moveChipsToPot();
		    reportPotAndRakeUpdates(potTransitions);
		    
		    startDealInitialCardsRound();
		}
	}
	
	
	private void startDealInitialCardsRound() {
		setCurrentRound(roundFactory.createDealInitialCardsRound(this));
		scheduleRoundTimeout();
		
	}

	private Set<PokerPlayer> getMuckingPlayers(){
		boolean allButOneHasFolded = state.countNonFoldedPlayers() <= 1;
		if(allButOneHasFolded){
			return new HashSet<PokerPlayer>( state.getCurrentHandSeatingMap().values());
		}
		return new HashSet<PokerPlayer>( );
	}
	
	@Override
	public void visit(BettingRound bettingRound) {
		state.setLastPlayerToBeCalled(bettingRound.getLastPlayerToBeCalled());
		
	    if (!isHandFinished()) {
    		Collection<PotTransition> potTransitions = moveChipsToPot();
    		reportPotAndRakeUpdates(potTransitions);
	    }
	    
		if (isHandFinished()) {
		    
		    returnAllBets();
		    
		    state.exposeShowdownCards();

			PokerPlayer playerAtDealerButton = state.getPlayerAtDealerButton();
			List<Integer> playerRevealOrder = new RevealOrderCalculator().calculateRevealOrder(state.getCurrentHandSeatingMap(), state.getLastPlayerToBeCalled(), playerAtDealerButton);
			HandResultCreator resultCreator = new HandResultCreator(new TelesinaHandStrengthEvaluator(getDeckLowestRank()));
		    HandResultCalculator resultCalculator = new HandResultCalculator(new TelesinaHandComparator(deck.getDeckLowestRank()));			
			Map<Integer, PokerPlayer> players = state.getCurrentHandPlayerMap();
			Set<PokerPlayer> muckingPlayers = getMuckingPlayers();
			
			HandResult handResult = resultCreator.createHandResult(state.getCommunityCards(), resultCalculator, state.getPotHolder(), players, playerRevealOrder, muckingPlayers);
						
            handleFinishedHand(handResult);
			state.getPotHolder().clearPots();
			
		} else { 
			if (state.isAtLeastAllButOneAllIn() && !state.hasAllPlayersExposedCards()){
				setCurrentRound(roundFactory.createExposePrivateCardsRound(this));
				scheduleRoundTimeout();
			} else {
				startDealPocketOrVelaCardRound();
			}
		}
	}

	@Override
	public void visit(ExposePrivateCardsRound exposePrivateCardsRound) {
		startDealPocketOrVelaCardRound();
	}
	
	private void startDealPocketOrVelaCardRound() {
		if (getBettingRoundId() == VELA_ROUND_ID) {
		    setCurrentRound(roundFactory.createDealCommunityCardsRound(this));
		} else {
		    setCurrentRound(roundFactory.createDealExposedPocketCardsRound(this));
		}
		scheduleRoundTimeout();
	}

	private void returnAllBets() {
	    for (PokerPlayer player : state.getCurrentHandSeatingMap().values()) {
	        player.returnAllBets();
	    }
    }

    @Override
	public void visit(BlindsRound blindsRound) {
		throw new UnsupportedOperationException("blinds round not supported in telesina");
	}
	
	@Override
	public void visit(DealCommunityCardsRound round) {
		startBettingRound();
	}

	@Override
	public void visit(DealExposedPocketCardsRound round) {
        log.debug("deal pocked cards round finished (betting round {})", getBettingRoundId());
        startBettingRound();
	}
	
	@Override
	public void visit(DealInitialPocketCardsRound round) {
		startBettingRound();
	}
		
	public void dealInitialPocketCards() {
		dealHiddenPocketCards();
		dealExposedPocketCards();
	}
	
	private void dealHiddenPocketCards() {
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			if (!p.isSittingOut() && !p.hasFolded()) {
				dealHiddenPocketCards(p, 1);
			}
		}
	}

	@VisibleForTesting
	public void dealExposedPocketCards() {
	    TelesinaHandStrengthEvaluator handStrengthEvaluator = new TelesinaHandStrengthEvaluator(getDeckLowestRank());	    
	    
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			if (!p.hasFolded()) {
				dealExposedPocketCards(p, 1);
				calculateAndSendBestHandToPlayer(handStrengthEvaluator, p);
			}
		}
	}

	public void sendAllNonFoldedPlayersBestHand(){
		TelesinaHandStrengthEvaluator handStrengthEvaluator = new TelesinaHandStrengthEvaluator(getDeckLowestRank());
		for (PokerPlayer p : state.getCurrentHandSeatingMap().values()) {
			if (!p.hasFolded()) {
				calculateAndSendBestHandToPlayer(handStrengthEvaluator, p);
			}
		}
	}
	
	
	/**
	 * Calculate the best hand for the player and send it.
	 * @param handStrengthEvaluator hand calculator
	 * @param player player
	 */
    protected void calculateAndSendBestHandToPlayer(TelesinaHandStrengthEvaluator handStrengthEvaluator, PokerPlayer player) {
        List<Card> playerCards = new ArrayList<Card>(player.getPocketCards().getCards());
        playerCards.addAll(state.getCommunityCards());
        Hand playerHand = new Hand(playerCards);
        HandStrength bestHandStrength = handStrengthEvaluator.getBestHandStrength(playerHand);
        state.getServerAdapter().notifyBestHand(player.getId(), bestHandStrength.getHandType(), bestHandStrength.getCards(), player.isExposingPocketCards());
    }

	@VisibleForTesting
    protected Round getCurrentRound() {
        return currentRound;
    }

    private void setCurrentRound(Round newRound) {
        log.debug("moved to new round: {} -> {}", currentRound, newRound);
        this.currentRound = newRound;
        // state.notifyNewRound();
    }

    @VisibleForTesting
    protected int getBettingRoundId() {
        return bettingRoundId;
    }

    private void incrementBettingRoundId() {
        this.bettingRoundId++;
    }
    
    private void resetBettingRoundId() {
        this.bettingRoundId = 0;
    }

	public Rank getDeckLowestRank() {
		return deck.getDeckLowestRank();
	}
	
	@Override
	public boolean canPlayerBuyIn(PokerPlayer player, PokerSettings pokerSettings) {
	    return player.getBalance() + player.getPendingBalance() >= pokerSettings.getAnteLevel();
	}

	
	
}

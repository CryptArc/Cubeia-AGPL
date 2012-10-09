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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.blinds.BlindsCalculator;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Deck;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandInfo;
import com.cubeia.poker.hand.IndexCardIdGenerator;
import com.cubeia.poker.hand.Shuffler;
import com.cubeia.poker.hand.StandardDeck;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.HandResultCalculator;
import com.cubeia.poker.result.RevealOrderCalculator;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.rounds.ante.AnteRound;
import com.cubeia.poker.rounds.betting.ActionRequestFactory;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.betting.DefaultPlayerToActCalculator;
import com.cubeia.poker.rounds.betting.NoLimitBetStrategy;
import com.cubeia.poker.rounds.blinds.BlindsRound;
import com.cubeia.poker.rounds.dealing.DealCommunityCardsRound;
import com.cubeia.poker.rounds.dealing.DealExposedPocketCardsRound;
import com.cubeia.poker.rounds.dealing.DealInitialPocketCardsRound;
import com.cubeia.poker.rounds.dealing.Dealer;
import com.cubeia.poker.rounds.dealing.ExposePrivateCardsRound;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.variant.AbstractGameType;
import com.cubeia.poker.variant.HandResultCreator;
import com.google.common.annotations.VisibleForTesting;

public class TexasHoldem extends AbstractGameType implements RoundVisitor, Dealer {

    private static final long serialVersionUID = -1523110440727681601L;

    private static transient Logger log = Logger.getLogger(TexasHoldem.class);

    private Round currentRound;

    private Deck deck;

    /**
     * 0 = pre flop 1 = flop 2 = turn 3 = river
     */
    private int roundId;

    private final TexasHoldemHandCalculator handEvaluator = new TexasHoldemHandCalculator();

    private HandResultCalculator handResultCalculator = new HandResultCalculator(handEvaluator);

    private RevealOrderCalculator revealOrderCalculator;

    public TexasHoldem() {
        revealOrderCalculator = new RevealOrderCalculator();
    }

    @Override
    public String toString() {
        return "TexasHoldem, current round[" + currentRound + "] roundId[" + roundId + "] ";
    }

    @Override
    public void startHand() {
        initHand();
    }

    private void initHand() {
        deck = new StandardDeck(new Shuffler<Card>(getServerAdapter().getSystemRNG()), new IndexCardIdGenerator());
        currentRound = new BlindsRound(context, serverAdapterHolder, new BlindsCalculator(new NonRandomSeatProvider()));
        roundId = 0;
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
            p.addPocketCard(deck.deal(), false);
        }
        getServerAdapter().notifyPrivateCards(p.getId(), p.getPocketCards().getCards());
    }

    private void dealCommunityCards(int n) {
        List<Card> dealt = new LinkedList<Card>();
        for (int i = 0; i < n; i++) {
            dealt.add(deck.deal());
        }
        context.getCommunityCards().addAll(dealt);
        getServerAdapter().notifyCommunityCards(dealt);
    }

    public void handleFinishedRound() {
        currentRound.visit(this);
    }

    private void reportPotUpdate() {
        notifyPotAndRakeUpdates(Collections.<PotTransition>emptyList());
    }

    private void startBettingRound() {
        log.trace("Starting new betting round. Round ID: " + (roundId + 1));
        currentRound = createBettingRound(context.getBlindsInfo().getDealerButtonSeatId());
        roundId++;
    }

    private BettingRound createBettingRound(int seatIdToStartBettingFrom) {
        DefaultPlayerToActCalculator playerToActCalculator = new DefaultPlayerToActCalculator();
        ActionRequestFactory requestFactory = new ActionRequestFactory(new NoLimitBetStrategy());
        TexasHoldemFutureActionsCalculator futureActionsCalculator = new TexasHoldemFutureActionsCalculator();
        int betLevel = getBetLevel(roundId, context.getSettings().getBigBlindAmount());
        return new BettingRound(seatIdToStartBettingFrom, context, serverAdapterHolder, playerToActCalculator, requestFactory, futureActionsCalculator, betLevel);
    }

    private int getBetLevel(int roundId, int bigBlindAmount) {
        int betLevel = (roundId < 2) ? bigBlindAmount : bigBlindAmount * 2;
        log.debug("Bet level for round " + roundId + " = " + betLevel);
        return betLevel;
    }

    private boolean isHandFinished() {
        return (roundId >= 3 || context.countNonFoldedPlayers() <= 1);
    }

    public void dealCommunityCards() {
        if (roundId == 0) {
            dealCommunityCards(3);
        } else {
            dealCommunityCards(1);
        }
    }

    @Override
    public void dealExposedPocketCards() {
        currentRound = new DealCommunityCardsRound(this);
        // Schedule timeout for the community cards round
        scheduleRoundTimeout();
    }

    @Override
    public void dealInitialPocketCards() {
        // This is used in Telesina but not Hold'em, please unify.
    }

    private void handleCanceledHand() {
        notifyHandFinished(new HandResult(), HandEndStatus.CANCELED_TOO_FEW_PLAYERS);
    }

    private void moveChipsToPot() {

        context.getPotHolder().moveChipsToPotAndTakeBackUncalledChips(context.getCurrentHandSeatingMap().values());

        for (PokerPlayer p : context.getCurrentHandSeatingMap().values()) {
            p.setHasActed(false);
            p.clearActionRequest();
        }
    }

    @Override
    public void requestMultipleActions(Collection<ActionRequest> requests) {
        throw new UnsupportedOperationException("sending multiple action requests not implemented");
    }

    @Override
    public void scheduleRoundTimeout() {
        log.debug("scheduleRoundTimeout in: " + context.getTimingProfile().getTime(Periods.RIVER));
        getServerAdapter().scheduleTimeout(context.getTimingProfile().getTime(Periods.RIVER));
    }

    @Override
    public void prepareNewHand() {
        context.getCommunityCards().clear();
        for (PokerPlayer player : context.getCurrentHandPlayerMap().values()) {
            player.clearHand();
            player.setHasFolded(false);
        }
    }

    @Override
    public void timeout() {
        log.debug("Timeout");
        currentRound.timeout();
        checkFinishedRound();
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
            handleFinishedHand();
        } else if (allInShowdown()) {
            log.debug("All-in showdown, exposing pocket cards.");
            currentRound = new ExposePrivateCardsRound(this, calculateRevealOrder());
            scheduleRoundTimeout();
        } else {
            // Start deal community cards round
            currentRound = new DealCommunityCardsRound(this);
            // Schedule timeout for the community cards round
            scheduleRoundTimeout();
        }
    }

    private boolean allInShowdown() {
        return (context.isAtLeastAllButOneAllIn() && !context.haveAllPlayersExposedCards());
    }

    @VisibleForTesting
    void handleFinishedHand() {
        List<Integer> playerRevealOrder = calculateRevealOrder();

        exposeShowdownCards(playerRevealOrder);
        Set<PokerPlayer> muckingPlayers = context.getMuckingPlayers();
        HandResult handResult = new HandResultCreator(new TexasHoldemHandCalculator()).createHandResult(context.getCommunityCards(), handResultCalculator, context.getPotHolder(), context.getCurrentHandPlayerMap(), playerRevealOrder, muckingPlayers);

        notifyHandFinished(handResult, HandEndStatus.NORMAL);
        context.getPotHolder().clearPots();
    }

    private List<Integer> calculateRevealOrder() {
        PokerPlayer playerAtDealerButton = context.getPlayerInDealerSeat();
        return revealOrderCalculator.calculateRevealOrder(context.getCurrentHandSeatingMap(), context.getLastPlayerToBeCalled(), playerAtDealerButton, context.countNonFoldedPlayers());
    }

    @Override
    public void visit(ExposePrivateCardsRound exposePrivateCardsRound) {
        currentRound = new DealCommunityCardsRound(this);
        scheduleRoundTimeout();
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
        updateHandStrengths();
        startBettingRound();
    }

    private void updateHandStrengths() {
        for (PokerPlayer player : context.getCurrentHandSeatingMap().values()) {
            Hand hand = new Hand();
            hand.addCards(player.getPocketCards().getCards());
            hand.addCards(context.getCommunityCards());
            HandInfo handInfo = handEvaluator.getBestHandInfo(hand);
            if (handInfo.getCards() == null) {
                log.warn("Cards in best hand is null for player " + player + " pocket cards: "
                         + player.getPocketCards().getCards() + " community: " + context.getCommunityCards());
            }
            serverAdapterHolder.get().notifyBestHand(player.getPlayerId(), handInfo.getHandType(), handInfo.getCards(), false);
        }
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
        currentRound = createBettingRound(context.getBlindsInfo().getBigBlindSeatId());
    }

    private void updateBlindsInfo(BlindsRound blindsRound) {
        context.setBlindsInfo(blindsRound.getBlindsInfo());
    }

    private void dealPocketCards() {
        for (PokerPlayer p : context.getCurrentHandSeatingMap().values()) {
            if (!p.isSittingOut()) {
                dealPocketCards(p, 2);
            }
        }
        updateHandStrengths();
    }

    @Override
    // TODO: Implement for Texas Hold'em.
    public void sendAllNonFoldedPlayersBestHand() {
        log.warn("Implement sendAllNonFoldedPlayersBestHand for Texas Hold'em.");
    }

    @Override
    public boolean canPlayerAffordEntryBet(PokerPlayer player, PokerSettings settings, boolean includePending) {
        return player.getBalance() + (includePending ? player.getPendingBalanceSum() : 0) >= settings.getAnteAmount();
    }

    @Override
    public boolean isCurrentlyWaitingForPlayer(int playerId) {
        return currentRound.isWaitingForPlayer(playerId);
    }

    public void setRevealOrderCalculator(RevealOrderCalculator revealOrderCalculator) {
        this.revealOrderCalculator = revealOrderCalculator;
    }
}

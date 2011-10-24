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

package com.cubeia.poker.rounds.betting;

import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.poker.GameType;
import com.cubeia.poker.action.ActionRequestFactory;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundVisitor;
import com.google.common.annotations.VisibleForTesting;

public class BettingRound implements Round, BettingRoundContext {

	private static final long serialVersionUID = -8666356150075950974L;

	private static transient Logger log = LoggerFactory.getLogger(BettingRound.class);

	private final GameType gameType;

	private long highBet = 0;

	private int playerToAct = 0;
	
	private final ActionRequestFactory actionRequestFactory;

	private boolean isFinished = false;

	/** Last highest bet that any raise must match */
	private long lastBetSize = 0;

    private final PlayerToActCalculator playerToActCalculator;
    
    private PokerPlayer lastPlayerToPlaceABet; 
    
    private PokerPlayer lastPlayerToBeCalled;
	
	public BettingRound(GameType gameType, int dealerSeatId, PlayerToActCalculator playerToActCalculator) {
		this.gameType = gameType;
        this.playerToActCalculator = playerToActCalculator;
		actionRequestFactory = new ActionRequestFactory(new NoLimitBetStrategy(this));
		if (gameType != null && gameType.getState() != null) { // can be null in unit tests
			lastBetSize = gameType.getState().getEntryBetLevel();
		}
		initBettingRound(dealerSeatId);
	}

	@Override
	public String toString() {
		return "BettingRound, isFinished["+isFinished+"]";
	}
	
	private void initBettingRound(int dealerSeatId) {
		log.debug("Init new betting round - dealer: "+dealerSeatId);
		SortedMap<Integer, PokerPlayer> seatingMap = gameType.getState().getCurrentHandSeatingMap();
		for (PokerPlayer p : seatingMap.values()) {
			if (p.getBetStack() > highBet) {
				highBet = p.getBetStack();
			}
			p.clearActionRequest();
		}

		// Check if we should request actions at all
		PokerPlayer p = playerToActCalculator.getFirstPlayerToAct(dealerSeatId, gameType.getState().getCurrentHandSeatingMap(), gameType.getState().getCommunityCards());
		
		if (p == null || allOtherPlayersAreAllIn(p)) {
			// No or only one player can act. We are currently in an all-in show down scenario
			log.debug("Schedule all in timeout from initBettingRound");
			isFinished = true;
			gameType.scheduleRoundTimeout();
		} else {
			requestAction(p);
		}
	}
	
    public void act(PokerAction action) {
		log.debug("Act : "+action);
		PokerPlayer player = gameType.getState().getPlayerInCurrentHand(action.getPlayerId());

		verifyValidAction(action, player);
		handleAction(action, player);
		gameType.getServerAdapter().notifyActionPerformed(action, player.getBalance());
		// FIXME: Perhaps the new status information could be included in the action performed packet?
		if (player.getBalance() <= 0) {
			gameType.getServerAdapter().notifyPlayerStatusChanged(player.getId(), PokerPlayerStatus.ALLIN);
		}
		
		if (roundFinished()) {
			isFinished = true;
		} else {
			requestNextAction(player.getSeatId());
		}
	}

	private void requestNextAction(int lastSeatId) {
		PokerPlayer player = playerToActCalculator.getNextPlayerToAct(lastSeatId, gameType.getState().getCurrentHandSeatingMap());
		if (player == null) {
			isFinished = true;
		} else {
			requestAction(player);
		}
	}

	/**
	 * Get the player's available actions and send a request to the client
	 * or perform default action if the player is sitting out.
	 * 
	 * @param p
	 */
	private void requestAction(PokerPlayer p) {
		playerToAct = p.getId();
		if (p.getBetStack() < highBet) {
			p.setActionRequest(actionRequestFactory.createFoldCallRaiseActionRequest(p));
		} else {
			p.setActionRequest(actionRequestFactory.createFoldCheckBetActionRequest(p));
		}
		
		if (p.isSittingOut()){
			performDefaultActionForPlayer(p);
		} else {
			gameType.requestAction(p.getActionRequest());
		}
	}

	private boolean roundFinished() {
		/*
		 * If there's only one non folded player left, the round (and hand) is
		 * finished.
		 */
		if (gameType.getState().countNonFoldedPlayers() < 2) {
			return true;
		}

		for (PokerPlayer p : gameType.getState().getCurrentHandSeatingMap().values()) {
			if (!p.hasFolded() && !p.hasActed()) {
				return false;
			}
		}
		return true;
	}

	private void handleAction(PokerAction action, PokerPlayer player) {
		switch (action.getActionType()) {
		case CALL:
			call(player);
			break;
		case CHECK:
			check(player);
			break;
		case FOLD:
			fold(player);
			break;
		case RAISE:
		    
		    log.debug("raise action: player = {}, betAmount = {}, raiseAmount = {}, action = {}", 
		        new Object[] {player.getId(), action.getBetAmount(), action.getRaiseAmount(), action.toString()});
		    
			setRaiseByAmount(player, action);
			raise(player, action.getBetAmount());
			break;
		case BET:
			bet(player, action.getBetAmount());
			break;
		default:
			throw new IllegalArgumentException();
		}
		player.setHasActed(true);
	}

	
	private void verifyValidAction(PokerAction action, PokerPlayer player) {
		if (playerToAct != action.getPlayerId()) {
			throw new IllegalArgumentException("Expected " + playerToAct + " to act, but got action from:" + player.getId());
		}

		if (!player.getActionRequest().matches(action)) {
			throw new IllegalArgumentException("Player " + player.getId() + " tried to act " + action.getActionType() + " but his options were "
					+ player.getActionRequest().getOptions());
		}
	}

	private void raise(PokerPlayer player, long amount) {
		if (amount <= highBet) {
			throw new IllegalArgumentException("PokerPlayer["+player.getId()+"] incorrect raise amount. Highbet["+highBet+"] amount["+amount+"]. " +
					"Amounts must be larger than current highest bet");
		}
		
		/** Check if player went all in with a below minimum raise */
		if (! (amount < 2*lastBetSize && player.isAllIn())) {
			lastBetSize = amount-highBet;
		} else {
			log.debug("Player["+player.getId()+"] made a below min raise but is allin.");
		}
		
		highBet = amount;
		lastPlayerToPlaceABet = player;
		player.addBet(highBet - player.getBetStack());
		resetHasActed();
	}
	
	private void setRaiseByAmount(PokerPlayer player, PokerAction action) {
		action.setRaiseAmount(action.getBetAmount() - highBet);
	}


	private void bet(PokerPlayer player, long amount) {
		lastBetSize = amount;
		highBet = highBet + amount;
		lastPlayerToPlaceABet = player;
		player.addBet(highBet - player.getBetStack());
		resetHasActed();
	}

	private void resetHasActed() {
		for (PokerPlayer p : gameType.getState().getCurrentHandSeatingMap().values()) {
			if (!p.hasFolded()) {
				p.setHasActed(false);
			}
		}
	}

	private void fold(PokerPlayer player) {
		player.setHasFolded(true);
	}

	private void check(PokerPlayer player) {
		// Nothing to do.
	}

	@VisibleForTesting
	protected void call(PokerPlayer player) {
		player.addBet(getAmountToCall(player));
		lastPlayerToBeCalled = lastPlayerToPlaceABet;
		gameType.getState().call();
	}

	private long getAmountToCall(PokerPlayer player) {
		return Math.min(highBet - player.getBetStack(), player.getBalance());
	}

	public void timeout() {
		PokerPlayer player = gameType.getState().getPlayerInCurrentHand(playerToAct);
		if (player == null) {
			// throw new IllegalStateException("Expected " + playerToAct + " to act, but that player can not be found at the table!");
			log.debug("Expected " + playerToAct + " to act, but that player can not be found at the table! I will assume everyone is all in");
			return; // Are we allin?
		}
		performDefaultActionForPlayer(player);
	}

	private void performDefaultActionForPlayer(PokerPlayer player) {
		log.debug("Perform default action for player sitting out: "+player);
		if (player.getActionRequest().isOptionEnabled(PokerActionType.CHECK)) {
			act(new PokerAction(player.getId(), PokerActionType.CHECK, true));
		} else {
			act(new PokerAction(player.getId(), PokerActionType.FOLD, true));
		}
	}

	public String getStateDescription() {
		return "playerToAct=" + playerToAct + " roundFinished=" + roundFinished();
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void visit(RoundVisitor visitor) {
		visitor.visit(this);
	}

	public boolean allOtherPlayersAreAllIn(PokerPlayer thisPlayer) {
		for (PokerPlayer player : gameType.getState().getCurrentHandSeatingMap().values()) {
			if (!player.isSittingOut() && !player.hasFolded() && !player.equals(thisPlayer) && !player.isAllIn()) {
				return false;
			}
		}
		return true;
	}

	public long getHighestBet() {
		return highBet;
	}

	public long getMinBet() {
		return gameType.getState().getEntryBetLevel();
	}

	public long getSizeOfLastBetOrRaise() {
		return lastBetSize;
	}

	public PokerPlayer getLastPlayerToBeCalled() {
		return lastPlayerToBeCalled;
	}
}

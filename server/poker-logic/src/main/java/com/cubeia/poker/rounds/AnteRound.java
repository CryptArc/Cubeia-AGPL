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

package com.cubeia.poker.rounds;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.cubeia.poker.GameType;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.util.PokerUtils;

public class AnteRound implements Round {

	private static final long serialVersionUID = -6452364533249060511L;

	private static transient Logger log = Logger.getLogger(AnteRound.class);

	private final GameType game;

	private boolean finished = false;

	private int playerToAct = 0;
	
	public AnteRound(GameType game) {
		this.game = game;
		
		clearPlayerActionOptions();
		
		Collection<PokerPlayer> players = game.getState().getCurrentHandSeatingMap().values();
		
		// TODO: how do we decide dealer? for now always use first player...
		moveDealerButtonToSeatId(players.iterator().next().getSeatId());
		
		
//		allPlayersAnteBet(players, game.getBlindsInfo().getAnteLevel());
		
		
//		PokerPlayer player = getNextPlayerToAct(dealerSeatId);
		requestNextAction(game.getBlindsInfo().getDealerButtonSeatId());
	}
	
	private PokerPlayer getNextPlayerToAct(int lastActedSeatId) {
		PokerPlayer next = null;

		List<PokerPlayer> players = PokerUtils.unwrapList(game.getState().getCurrentHandSeatingMap(), lastActedSeatId + 1);
		for (PokerPlayer player : players) {
			if (!player.hasFolded() && !player.hasActed() && !player.isSittingOut() && !player.isAllIn()) {
				next = player;
				break;
			}
		}
		return next;
	}
	
	private void moveDealerButtonToSeatId(int newDealerSeatId) {
		BlindsInfo blindsInfo = game.getBlindsInfo();
		blindsInfo.setDealerButtonSeatId(newDealerSeatId);
		game.getState().notifyDealerButton(blindsInfo.getDealerButtonSeatId());
	}
	
	private void requestNextAction(int lastSeatId) {
		PokerPlayer p = getNextPlayerToAct(lastSeatId);

		if (p == null) {
			finished = true;
		} else {
			requestAnte(p, game.getBlindsInfo().getAnteLevel());
			playerToAct = p.getId();
		}
	}
	
	
	private void requestAnte(PokerPlayer player, int anteLevel) {
//		getBlindsInfo().setSmallBlind(smallBlind);
		
		player.enableOption(new PossibleAction(PokerActionType.SMALL_BLIND, anteLevel));
		player.enableOption(new PossibleAction(PokerActionType.DECLINE_ENTRY_BET));
		game.requestAction(player.getActionRequest());
	}
	
	
	private void addAnteBet(PokerPlayer player, int anteLevel) {
		player.addBet(anteLevel);
	}
	
	private void clearPlayerActionOptions() {
		SortedMap<Integer, PokerPlayer> seatingMap = game.getState().getCurrentHandSeatingMap();
		for (PokerPlayer p : seatingMap.values()) {
			p.clearActionRequest();
		}
	}
	
	
	public void act(PokerAction action) {
		
		log.debug("act on: " + action);
		
		switch (action.getActionType()) {
		case SMALL_BLIND:
			PokerPlayer player = game.getState().getPlayerInCurrentHand(action.getPlayerId());
			addAnteBet(player, game.getBlindsInfo().getAnteLevel());
			player.setHasActed(true);
			break;
		default:
			throw new IllegalArgumentException(action.getActionType() + " is not legal here");
		}
//		getGame().getState().getPlayerInCurrentHand(action.getPlayerId()).clearActionRequest();
//		game.getServerAdapter().notifyActionPerformed(action);
	}
	

	public void timeout() {
//		currentState.timeout(this);
	}

	public String getStateDescription() {
		return "currentState=null";
	}

	public boolean isFinished() {
		return finished;
	}
	
	public void visit(RoundVisitor visitor) {
		visitor.visit(this);
	}
}

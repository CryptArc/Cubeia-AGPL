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
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.cubeia.poker.GameType;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;

public class AnteRound implements Round {

	private static final long serialVersionUID = -6452364533249060511L;

	private static transient Logger log = Logger.getLogger(AnteRound.class);

	private final GameType game;

	private boolean finished = false;

	public AnteRound(GameType game) {
		this.game = game;
		
		clearPlayerActionOptions();
		
		Collection<PokerPlayer> players = game.getState().getCurrentHandSeatingMap().values();
		allPlayersAnteBet(players, game.getBlindsInfo().getAnteLevel());
		
		// TODO: dealer button should change between hands
		game.getServerAdapter().notifyDealerButton(0);
		
		finished = true;
	}
	
	private void allPlayersAnteBet(Collection<PokerPlayer> players, int anteLevel) {
		log.debug("ante for all players, ante = " + anteLevel + ", players = " + players);
		
		for (PokerPlayer player : players) {
			log.debug("player " + player.getId() + " bets ante of: " + anteLevel);
			
			placeAnteBet(player, anteLevel);
			PokerAction action = new PokerAction(player.getId(), PokerActionType.SMALL_BLIND);
			action.setBetAmount(anteLevel);
			game.getServerAdapter().notifyActionPerformed(action);
		}
	}
	
	
	private void placeAnteBet(PokerPlayer player, int anteLevel) {
		player.addBet(anteLevel);
	}
	
	private void clearPlayerActionOptions() {
		SortedMap<Integer, PokerPlayer> seatingMap = game.getState().getCurrentHandSeatingMap();
		for (PokerPlayer p : seatingMap.values()) {
			p.clearActionRequest();
		}
	}
	
	
	public void act(PokerAction action) {
		switch (action.getActionType()) {
//		case SMALL_BLIND:
//			addAnteBet(action.getPlayerId());
//			currentState.smallBlind(action.getPlayerId(), this);
//			break;
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

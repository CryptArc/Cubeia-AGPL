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

package com.cubeia.poker.rounds.ante;

import java.util.Collection;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.cubeia.poker.GameType;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class AnteRound implements Round {

	private static final long serialVersionUID = -6452364533249060511L;

	private static transient Logger log = Logger.getLogger(AnteRound.class);

	private final GameType game;

//	private boolean finished = false;

//	private int playerToAct = 0;
	
	private AnteRoundHelper anteRoundHelper;
	
	public AnteRound(GameType game, AnteRoundHelper anteRoundHelper) {
		this.game = game;
        this.anteRoundHelper = anteRoundHelper;
		
		clearPlayerActionOptions();
		
		Collection<PokerPlayer> players = game.getState().getCurrentHandSeatingMap().values();
		
		// TODO: how do we decide dealer? for now always use first player...
		moveDealerButtonToSeatId(players.iterator().next().getSeatId());
		
		
		requestNextAction(game.getBlindsInfo().getDealerButtonSeatId());
	}
	
	private void moveDealerButtonToSeatId(int newDealerSeatId) {
		BlindsInfo blindsInfo = game.getBlindsInfo();
		blindsInfo.setDealerButtonSeatId(newDealerSeatId);
		game.getState().notifyDealerButton(blindsInfo.getDealerButtonSeatId());
	}
	
	private void requestNextAction(int lastSeatId) {
		PokerPlayer p = anteRoundHelper.getNextPlayerToAct(lastSeatId, game.getState().getCurrentHandSeatingMap());

		if (p != null) {
			anteRoundHelper.requestAnte(p, game.getBlindsInfo().getAnteLevel(), game);
//			playerToAct = p.getId();
		}
	}
	
	
	private void clearPlayerActionOptions() {
		SortedMap<Integer, PokerPlayer> seatingMap = game.getState().getCurrentHandSeatingMap();
		for (PokerPlayer p : seatingMap.values()) {
			p.clearActionRequest();
		}
	}
	
	public void act(PokerAction action) {
		
		log.debug("act on: " + action);
		PokerPlayer player = game.getState().getPlayerInCurrentHand(action.getPlayerId());
		
		switch (action.getActionType()) {
		case ANTE:
			player.addBet(game.getBlindsInfo().getAnteLevel());
			player.setHasActed(true);
			player.setHasPostedEntryBet(true);
			break;
		case DECLINE_ENTRY_BET:
            player.setHasActed(true);
            player.setHasPostedEntryBet(false);
            break;
		default:
			throw new IllegalArgumentException(action.getActionType() + " is not legal here");
		}
		
		if (!anteRoundHelper.hasAllPlayersActed(game.getState().getCurrentHandPlayerMap().values())) {
			requestNextAction(player.getSeatId());
		}
	}

	public void timeout() {
//		currentState.timeout(this);
	}

	public String getStateDescription() {
		return "currentState=null";
	}

	public boolean isFinished() {
	    for (PokerPlayer player : game.getState().getCurrentHandSeatingMap().values()) {
	        if (!player.hasActed()) {
	            return false;
	        }
	    }
		return true;
	}
	
	public void visit(RoundVisitor visitor) {
		visitor.visit(this);
	}

    public boolean isCanceled() {
        if (!isFinished()) {
            return false;
        } else {
            Collection<PokerPlayer> hasPostedEntryBet = Collections2.filter(game.getState().getCurrentHandPlayerMap().values(), new Predicate<PokerPlayer>() {
                @Override
                public boolean apply(PokerPlayer player) { return player.hasPostedEntryBet(); }
            });
            return hasPostedEntryBet.size() < 2;
        }
        
    }
}

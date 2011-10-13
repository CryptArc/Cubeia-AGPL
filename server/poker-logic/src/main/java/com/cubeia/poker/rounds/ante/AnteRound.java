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
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class AnteRound implements Round {

	private static final long serialVersionUID = -6452364533249060511L;

	private static transient Logger log = Logger.getLogger(AnteRound.class);

	private final GameType game;

	private AnteRoundHelper anteRoundHelper;

	private Integer playerToAct;
	
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
			playerToAct = p.getId();
		}
	}
	
	
	private void clearPlayerActionOptions() {
		SortedMap<Integer, PokerPlayer> seatingMap = game.getState().getCurrentHandSeatingMap();
		for (PokerPlayer p : seatingMap.values()) {
			p.clearActionRequest();
		}
		playerToAct = null;
	}
	
	public void act(PokerAction action) {
		log.debug("Act: "+action);
		PokerPlayer player = game.getState().getPlayerInCurrentHand(action.getPlayerId());
		verifyValidAnte(player);
		
		switch (action.getActionType()) {
		case ANTE:
			player.addBet(game.getBlindsInfo().getAnteLevel());
			player.setHasActed(true);
			player.setHasPostedEntryBet(true);
			// TODO Check why the client is sending 0 as amount...
			action.setBetAmount(game.getBlindsInfo().getAnteLevel());
			game.getServerAdapter().notifyActionPerformed(action, player.getBalance());
			break;
		case DECLINE_ENTRY_BET:
            player.setHasActed(true);
            player.setHasFolded(true);
            player.setHasPostedEntryBet(false);
            game.getServerAdapter().notifyActionPerformed(action, player.getBalance());
            setPlayerSitOut(player);
            break;
		default:
			throw new IllegalArgumentException(action.getActionType() + " is not legal here");
		}
		
		game.getState().getPlayerInCurrentHand(action.getPlayerId()).clearActionRequest();
		
		
		boolean hasAllPlayersActed = anteRoundHelper.hasAllPlayersActed(game.getState().getCurrentHandPlayerMap().values());
		boolean allPlayersButOneIsOut = (numberOfPlayersPayedAnte() + numberOfPendingPlayers()) <= 1;
		
		if (!hasAllPlayersActed && !allPlayersButOneIsOut) 
		{
			requestNextAction(player.getSeatId());
		}
		else
		{
			setAllPlayersToDeclineEntryBet();
		}
	}
	
	
	private void setAllPlayersToDeclineEntryBet() {
		Collection<PokerPlayer> players = getAllSeatedPlayers();
		for (PokerPlayer pokerPlayer : players) {
			if(!pokerPlayer.hasActed()){				
				pokerPlayer.setHasPostedEntryBet(false);
				pokerPlayer.setHasActed(true);
			}
		}		
	}

	private int numberOfPendingPlayers() {
		Collection<PokerPlayer> players = getAllSeatedPlayers();
		int counter = 0;
		for (PokerPlayer pokerPlayer : players) {
			if(!pokerPlayer.hasActed()){
				++counter;
			}
				
		}
		return counter;
	}

	private Collection<PokerPlayer> getAllSeatedPlayers() {
		return game.getState().getCurrentHandSeatingMap().values();
	}

	private int numberOfPlayersPayedAnte() {
		Collection<PokerPlayer> players = getAllSeatedPlayers();
		int counter = 0;
		for (PokerPlayer pokerPlayer : players) {
			if(pokerPlayer.hasPostedEntryBet()){
				++counter;
			}
				
		}
		return counter;
	}

	private void setPlayerSitOut(PokerPlayer player) {
		player.setSitOutStatus(SitOutStatus.MISSSED_ANTE);
		game.getState().notifyPlayerSittingOut(player.getId());
	}

	/**
	 * Verify that this player is allowed to place ante.
	 * 
	 * @param player
	 * @throws IllegalArgumentException if the player was not allowed to place ANTE
	 */
	private void verifyValidAnte(PokerPlayer player) {
		PossibleAction option = player.getActionRequest().getOption(PokerActionType.ANTE);
		if (option == null) {
			throw new IllegalArgumentException("Illegal ante request from player ["+player+"]");
		}
	} 

	public void timeout() {
		log.debug("Player["+playerToAct+"] ante timed out. Will decline entry bet.");
		act(new PokerAction(playerToAct, PokerActionType.DECLINE_ENTRY_BET, true));
	}

	public String getStateDescription() {
		return "currentState=null";
	}

	public boolean isFinished() {
	    for (PokerPlayer player : getAllSeatedPlayers()) {
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

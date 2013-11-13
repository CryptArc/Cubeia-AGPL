/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.poker.rounds.discard;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.DiscardAction;
import com.cubeia.poker.action.DiscardRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.rounds.betting.PlayerToActCalculator;
import com.cubeia.poker.timing.Periods;
import com.google.common.collect.Lists;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscardRound implements Round {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6746436704056656995L;
	private static final Logger log = Logger.getLogger(DiscardRound.class);
    private final PokerContext context;
    private final ServerAdapterHolder serverAdapterHolder;
    private final int cardsToDiscard;
    private int playerToAct;
    private boolean forceDiscard;

    public DiscardRound(PokerContext context,
                        ServerAdapterHolder serverAdapterHolder,
                        PlayerToActCalculator playerToActCalculator,
                        int cardsToDiscard,
                        boolean forceDiscard)  {
        this.context = context;
        this.serverAdapterHolder = serverAdapterHolder;
        this.cardsToDiscard = cardsToDiscard;
        this.forceDiscard = forceDiscard;
        initRound();
    }

    private void initRound() {
        resetHasActed();
        requestDiscard(context.getPlayersInHand());
    }

    private void resetHasActed() {
        for (PokerPlayer pokerPlayer : context.getPlayersInHand()) {
            pokerPlayer.setHasActed(false);
        }
    }

    private void requestDiscard(Collection<PokerPlayer> players) {
        // Check if we should request actions at all
		Collection<PokerPlayer> activePlayers = new ArrayList<PokerPlayer>();
         for (PokerPlayer player : players) {
             if (player.isSittingOut()) {
                 activePlayers.remove(player);
             }
         }
         requestDiscardFromAllPlayersInHand(activePlayers);
    }

    private void requestDiscardFromAllPlayersInHand(Collection<PokerPlayer> players) {
    	ArrayList<ActionRequest> requests = new ArrayList<ActionRequest>();
    	 for (PokerPlayer player : context.getPlayersInHand()) {
             ActionRequest request = getActionRequest(player);
             requests.add(request);
         }
    	 serverAdapterHolder.get().requestMultipleActions(requests);

	}

	private ActionRequest getActionRequest(PokerPlayer player) {
        playerToAct = player.getId();
        ActionRequest actionRequest = new ActionRequest();
        actionRequest.enable(new DiscardRequest(cardsToDiscard));
        actionRequest.setTimeToAct(context.getTimingProfile().getTime(Periods.ACTION_TIMEOUT));
        actionRequest.setPlayerId(player.getId());
        return actionRequest;
    }

    @Override
    public boolean act(PokerAction action) {
        PokerPlayer player = context.getPlayerInCurrentHand(action.getPlayerId());
        if (action instanceof DiscardAction && isValidAction(action, player)) {
            DiscardAction discard = (DiscardAction) action;
            log.debug("Player " + player.getId() + " discards: " + discard.getCardsToDiscard());
            player.setHasActed(true);
            player.discard(discard.getCardsToDiscard());
            player.clearActionRequest();
            serverAdapterHolder.get().notifyDiscards(discard, player);
            return true;
        } else {
            return false;
        }
    }


    private boolean isValidAction(PokerAction action, PokerPlayer player) {
/*        if (!action.getPlayerId().equals(playerToAct)) {
            log.warn("Expected " + playerToAct + " to act, but got action from:" + player.getId());
            return false;
        }
       */
        return true;
    }

    private Collection<PokerPlayer> getAllSeatedPlayers() {
        return context.getCurrentHandSeatingMap().values();
    }
    
    @Override
    public void timeout() {
    	for (PokerPlayer player : getAllSeatedPlayers()) {
            if (!player.hasActed()) {
            	if (forceDiscard) {
            		List<Integer> forcedCardsToDiscard = Lists.newArrayList();
            		for (int i = 0; i < this.cardsToDiscard; i++) {
            			forcedCardsToDiscard.add(i);
            		}
            		player.setHasActed(true);
            		player.discard(forcedCardsToDiscard);
            		DiscardAction action = new DiscardAction(playerToAct, forcedCardsToDiscard);
            		serverAdapterHolder.get().notifyDiscards(action, player);
            	}
            }
    	}
    }

    @Override
    public boolean isFinished() {
        for (PokerPlayer player : context.getPlayersInHand()) {
            if (!player.hasActed()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void visit(RoundVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getStateDescription() {
        return null;
    }
}

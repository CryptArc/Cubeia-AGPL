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

package com.cubeia.poker.states;

import org.apache.log4j.Logger;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;

public class WaitingToStartSTM extends AbstractPokerGameSTM {

	private static final long serialVersionUID = -4837159720440582936L;
	
	private static transient Logger log = Logger.getLogger(WaitingToStartSTM.class);

	public String toString() {
	    return "WaitingToStartState";
	}
	
	@Override
	public void timeout(PokerState state) {
		if (!state.isTournamentTable()) {
			state.setHandFinished(false);
			state.getServerAdapter().performPendingBuyIns(state.getSeatedPlayers());
			state.commitPendingBalances();
						
		    state.sitOutPlayersMarkedForSitOutNextRound();
		    state.cleanupPlayers(); // Will remove disconnected and leaving players
			
			if (state.getPlayersReadyToStartHand().size() > 1) {
				state.startHand();
			} else {
				state.setHandFinished(true);
				state.setState(PokerState.NOT_STARTED);
				log.info("WILL NOT START NEW HAND, TOO FEW PLAYERS SEATED: " + state.getPlayersReadyToStartHand().size() + " sitting in of " + state.getSeatedPlayers().size());
			}
		} else {
			log.debug("Ignoring timeout in waiting to start state, since tournament hands are started by the tournament manager.");
		}
	}
	
	public void act(PokerAction action, PokerState pokerGame) {
		log.info("Discarding out of order action: "+action);
	}

}

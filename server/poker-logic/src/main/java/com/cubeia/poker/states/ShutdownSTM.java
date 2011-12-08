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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;

public class ShutdownSTM extends AbstractPokerGameSTM {

    private static Logger log = LoggerFactory.getLogger(ShutdownSTM.class);
    
	private static final long serialVersionUID = 7076228045164551068L;

	public String toString() {
	    return "ShutdownState";
	}
	   
	@Override
	public void act(PokerAction action, PokerState pokerGame) {
	    log.warn("table {} is shut down, dropping incoming action: {}", pokerGame.getId(), action);
	}
	
	@Override
	public void timeout(PokerState pokerGame) {
        log.warn("table {} is shut down, dropping incoming timout", pokerGame.getId());
	}
}

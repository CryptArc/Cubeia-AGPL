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

import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.variant.telesina.Telesina;

/**
 * Telesina specific round for dealing new pocket cards between betting rounds.
 * This round has been separated for timing reasons.
 */
public class DealInitialPocketCardsRound implements Round {

	private static final long serialVersionUID = 1L;

	public DealInitialPocketCardsRound(Telesina telesina) {
		telesina.dealInitialPocketCards();
	}

	@Override
	public void act(PokerAction action) {
		throw new IllegalStateException("Perform action not allowed during DealInitialPocketCardsRoun. Action received: "+action);
	}

	@Override
	public String getStateDescription() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public void visit(RoundVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void timeout() {}
	
	@Override
	public boolean isWaitingForPlayer(int playerId) {
		return false;
	}

}

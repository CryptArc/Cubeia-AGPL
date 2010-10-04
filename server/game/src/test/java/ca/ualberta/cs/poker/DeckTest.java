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

package ca.ualberta.cs.poker;

import junit.framework.TestCase;

public class DeckTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDeal() {
		Deck deck = new Deck();
		
		for (int i = 0; i < 52; i++) {
			Card card = deck.deal();
			assertNotNull(card);
		}
	}

}

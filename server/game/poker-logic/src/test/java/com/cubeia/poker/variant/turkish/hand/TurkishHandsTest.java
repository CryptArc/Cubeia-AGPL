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

package com.cubeia.poker.variant.turkish.hand;

import com.cubeia.poker.*;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.settings.RakeSettings;


import com.cubeia.poker.variant.turkish.TurkishDeck;
import com.cubeia.poker.variant.turkish.TurkishDeckFactory;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Random;

import static com.cubeia.poker.action.PokerActionType.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TurkishHandsTest extends AbstractTurkishHandTester {

    @Override
    protected void setUp() throws Exception {
        super.setUpTurkish(new TurkishDeckFactory(), new BigDecimal(10));
    }


    @Test
    public void testAllButOneFoldsOnSidePotFinishesTheHand() throws Exception {
        TurkishDeckFactory deckFactory = mock(TurkishDeckFactory.class);

        TurkishDeck deck = mock(TurkishDeck.class);
        when(deckFactory.createNewDeck(Mockito.any(Random.class), Mockito.anyInt())).thenReturn(deck);
        when(deck.deal()).thenReturn(
                new Card(1, "9H"), new Card(2, "TH"), new Card(3, "JH"), new Card(4, "QH"), new Card(5, "KH"), // Unknown :-(
                new Card(6, "9D"), new Card(7, "JD"), new Card(8, "KC"), new Card(9, "QC"), new Card(10, "KS"),
                new Card(1, "9H"), new Card(2, "TH"), new Card(3, "JH"), new Card(4, "QH"), new Card(5, "KH"), // Unknown :-(
                new Card(6, "9D"), new Card(7, "JD"), new Card(8, "KC"), new Card(9, "QC"), new Card(10, "KS"));

        super.setUpTurkish(deckFactory, new BigDecimal(2), new RakeSettings(new BigDecimal("0.01"), new BigDecimal("5.00"), new BigDecimal("1.50")));

        MockPlayer[] mp = TestUtils.createMockPlayers(2);
        
        setBalanceAndPlayerId(0, mp, 1995583417, 170);
        setBalanceAndPlayerId(1, mp, 1995583572, 200);

        
        int[] p = TestUtils.createPlayerIdArray(mp);
        addPlayers(game, mp);

        // Force start
        game.timeout();

        // ANTE
        act(p[0], ANTE);
        act(p[1], ANTE);

        assertThat(mp[0].getBalance(), is(bd(170 - 2)));
        assertThat(mp[1].getBalance(), is(bd(200 - 2)));

        // make deal initial pocket cards round end
        game.timeout();
        
        act(p[1], BET);
        act(p[0], CALL);
        
        discard(p[0], new int[0]);
        discard(p[1], new int[0]);
        
        game.timeout();

    }

    private BigDecimal bd(int i) {
        return new BigDecimal(i).setScale(2);
    }

    private void setBalanceAndPlayerId(int index, MockPlayer[] mp, int playerId, int balance) {
        mp[index].setPlayerId(playerId);
        mp[index].setBalance(new BigDecimal(balance).setScale(2));
    }


}

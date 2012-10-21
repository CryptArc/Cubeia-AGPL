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

package com.cubeia.poker.variant.telesina;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.settings.PokerSettings;

public class TelesinaCanPlayerBuyInTest {

    @Test
    public void testCanPlayerAffordEntryBet() {
        PokerPlayer player = mock(PokerPlayer.class);

        Telesina telesina = new Telesina(null, null, null);

        int anteLevel = 20;
        PokerSettings settings = new PokerSettings(anteLevel, anteLevel, anteLevel * 2, 0, 0, null, 0, null, null, null);

        when(player.getBalance()).thenReturn((long) anteLevel + 1);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(true));

        when(player.getBalance()).thenReturn((long) anteLevel + 0);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(true));

        when(player.getBalance()).thenReturn((long) anteLevel - 1);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(false));
    }

    @Test
    public void testCanPlayerAffordEntryBetWithPending() {
        PokerPlayer player = mock(PokerPlayer.class);

        Telesina telesina = new Telesina(null, null, null);

        int anteLevel = 20;
        PokerSettings settings = new PokerSettings(anteLevel, anteLevel, anteLevel * 2, 0, 0, null, 0, null, null, null);

        when(player.getPendingBalanceSum()).thenReturn((long) anteLevel + 1);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(true));
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, false), is(false));

        when(player.getPendingBalanceSum()).thenReturn((long) anteLevel + 0);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(true));
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, false), is(false));

        when(player.getPendingBalanceSum()).thenReturn((long) anteLevel - 1);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(false));
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, false), is(false));
    }


    @Test
    public void testCanPlayerAffordEntryBetWithBothPendingAndNormal() {
        PokerPlayer player = mock(PokerPlayer.class);

        Telesina telesina = new Telesina(null, null, null);

        int anteLevel = 20;
        PokerSettings settings = new PokerSettings(anteLevel, anteLevel, anteLevel * 2, 0, 0, null, 0, null, null, null);

        when(player.getBalance()).thenReturn((long) anteLevel / 2);
        when(player.getPendingBalanceSum()).thenReturn((long) anteLevel / 2);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, false), is(false));
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(true));

        when(player.getBalance()).thenReturn((long) anteLevel - 1);
        when(player.getPendingBalanceSum()).thenReturn((long) anteLevel);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, false), is(false));
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(true));

        when(player.getBalance()).thenReturn((long) anteLevel);
        when(player.getPendingBalanceSum()).thenReturn((long) anteLevel);
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, false), is(true));
        assertThat(telesina.canPlayerAffordEntryBet(player, settings, true), is(true));
    }

}

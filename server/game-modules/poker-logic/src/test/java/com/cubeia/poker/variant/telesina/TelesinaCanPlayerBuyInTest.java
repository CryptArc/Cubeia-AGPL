package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.PokerSettings;
import com.cubeia.poker.player.PokerPlayer;
import org.junit.Test;

import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelesinaCanPlayerBuyInTest {

    @Test
    public void testCanPlayerAffordEntryBet() {
        PokerPlayer player = mock(PokerPlayer.class);

        Telesina telesina = new Telesina(null, null, null, null, null);

        int anteLevel = 20;
        PokerSettings settings = new PokerSettings(anteLevel, 0, 0, null, TELESINA, 0, null, null, null);

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

        Telesina telesina = new Telesina(null, null, null, null, null);

        int anteLevel = 20;
        PokerSettings settings = new PokerSettings(anteLevel, 0, 0, null, TELESINA, 0, null, null, null);

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

        Telesina telesina = new Telesina(null, null, null, null, null);

        int anteLevel = 20;
        PokerSettings settings = new PokerSettings(anteLevel, 0, 0, null, TELESINA, 0, null, null, null);

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

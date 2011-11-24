package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.cubeia.poker.PokerSettings;
import com.cubeia.poker.player.PokerPlayer;

public class TelesinaCanPlayerBuyInTest {
    
    @Test
    public void testCanPlayerBuyIn() {
        PokerPlayer player = mock(PokerPlayer.class);
        
        Telesina telesina = new Telesina(null, null, null, null);
        
        int anteLevel = 20;
        PokerSettings settings = new PokerSettings(anteLevel, 0, 0, null, TELESINA, 0, null, null, null);
        
        when(player.getBalance()).thenReturn((long) anteLevel + 1);
        assertThat(telesina.canPlayerBuyIn(player, settings), is(true));
        
        when(player.getBalance()).thenReturn((long) anteLevel + 0);
        assertThat(telesina.canPlayerBuyIn(player, settings), is(true));

        when(player.getBalance()).thenReturn((long) anteLevel - 1);
        assertThat(telesina.canPlayerBuyIn(player, settings), is(false));
    }
    
    @Test
    public void testCanPlayerBuyInWithPending() {
        PokerPlayer player = mock(PokerPlayer.class);
        
        Telesina telesina = new Telesina(null, null, null, null);
        
        int anteLevel = 20;
        PokerSettings settings = new PokerSettings(anteLevel, 0, 0, null, TELESINA, 0, null, null, null);
        
        when(player.getPendingBalance()).thenReturn((long) anteLevel + 1);
        assertThat(telesina.canPlayerBuyIn(player, settings), is(true));
        
        when(player.getPendingBalance()).thenReturn((long) anteLevel + 0);
        assertThat(telesina.canPlayerBuyIn(player, settings), is(true));

        when(player.getPendingBalance()).thenReturn((long) anteLevel - 1);
        assertThat(telesina.canPlayerBuyIn(player, settings), is(false));
    }
    
    
}

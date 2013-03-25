package com.cubeia.poker.rounds.betting;


import com.cubeia.poker.player.PokerPlayer;
import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PotLimitBetStrategyTest extends TestCase {

    private PotLimitBetStrategy strategy;

    private BettingRoundContext context;

    private PokerPlayer player;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = mock(BettingRoundContext.class);
        player = mock(PokerPlayer.class);
        strategy = new PotLimitBetStrategy(50);
    }

    public void testGetMinAmount() {
        when(player.getBalance()).thenReturn(100L);
        long minAmount = strategy.getMinBetAmount(context,player);
        assertEquals(50L,minAmount);
    }
    public void testGetMaxAmount() {
        when(player.getBalance()).thenReturn(100L);
        when(context.getPotSize()).thenReturn(80L);
        long maxAmount = strategy.getMaxBetAmount(context,player);
        assertEquals(80L,maxAmount);
    }

    public void testGetMaxAmountPotGreaterThanStack() {
        when(player.getBalance()).thenReturn(100L);
        when(context.getPotSize()).thenReturn(200L);
        long maxAmount = strategy.getMaxBetAmount(context,player);
        assertEquals(100L,maxAmount);
    }

    public void testGetMaxRaiseToAmount() {
        when(player.getBalance()).thenReturn(100L);
        when(player.getBetStack()).thenReturn(10L);
        when(context.getHighestBet()).thenReturn(20L);
        when(context.getPotSize()).thenReturn(30L);
        assertEquals(60L,strategy.getMaxRaiseToAmount(context,player));

        when(player.getBalance()).thenReturn(1000L);
        when(player.getBetStack()).thenReturn(4L);
        when(context.getHighestBet()).thenReturn(8L);
        when(context.getPotSize()).thenReturn(16L);
        assertEquals(28L, strategy.getMaxRaiseToAmount(context,player));

    }

    //10 + 10
    //20 + 40 + ?
    public void testGetMaxRaiseToAmountAllIn() {
        when(player.getBalance()).thenReturn(100L);
        when(player.getBetStack()).thenReturn(20L);
        when(context.getHighestBet()).thenReturn(40L);
        when(context.getPotSize()).thenReturn(80L);
        assertEquals(120L,strategy.getMaxRaiseToAmount(context,player));
    }
    //pot 10 pre-flop
    //balance 40 bet 20
    //raise to 40
    //balance 20 bet stack 20 max raise = 0
    public void testGetMaxRaiseWhenNoMoneyToRaiseWith() {
        when(player.getBalance()).thenReturn(20L);
        when(player.getBetStack()).thenReturn(20L);
        when(context.getHighestBet()).thenReturn(40L);
        when(context.getPotSize()).thenReturn(70L);
        assertEquals(0L,strategy.getMaxRaiseToAmount(context,player));
    }

}

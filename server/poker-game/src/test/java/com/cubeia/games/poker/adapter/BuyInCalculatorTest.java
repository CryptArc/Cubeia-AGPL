package com.cubeia.games.poker.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.cubeia.games.poker.adapter.BuyInCalculator.MinAndMaxBuyInResult;

public class BuyInCalculatorTest {

    @Test
    public void testCalculateBelowMax() {
        int tableMinBuyIn = 100;
        int tableMaxBuyIn = 20000;
        int anteLevel = 20;
        BuyInCalculator blc = new BuyInCalculator();
        
        MinAndMaxBuyInResult result;
        
        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, 0);
        assertThat(result.getMinBuyIn(), is(tableMinBuyIn));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn));
        assertThat(result.isBuyInPossible(), is(true));
        
        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, 70);
        assertThat(result.getMinBuyIn(), is(tableMinBuyIn - 70));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn - 70));
        assertThat(result.isBuyInPossible(), is(true));
        
        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, 99);
        assertThat(result.getMinBuyIn(), is(anteLevel));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn - 99));
        assertThat(result.isBuyInPossible(), is(true));
        
        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMinBuyIn);
        assertThat(result.getMinBuyIn(), is(anteLevel));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn - tableMinBuyIn));
        assertThat(result.isBuyInPossible(), is(true));
        
        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, 5000);
        assertThat(result.getMinBuyIn(), is(anteLevel));
        assertThat(result.getMaxBuyIn(), is(tableMaxBuyIn - 5000));
        assertThat(result.isBuyInPossible(), is(true));
    }
    
    @Test
    public void testCalculateBalanceNearMax() {
        int tableMinBuyIn = 100;
        int tableMaxBuyIn = 20000;
        int anteLevel = 20;
        BuyInCalculator blc = new BuyInCalculator();
        
        MinAndMaxBuyInResult result;

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMaxBuyIn - anteLevel);
        assertThat(result.getMinBuyIn(), is(anteLevel));
        assertThat(result.getMaxBuyIn(), is(anteLevel));
        assertThat(result.isBuyInPossible(), is(true));
        
        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMaxBuyIn - anteLevel / 2);
        assertThat(result.getMinBuyIn(), is(anteLevel / 2));
        assertThat(result.getMaxBuyIn(), is(anteLevel / 2));
        assertThat(result.isBuyInPossible(), is(true));
        
    }
    
    @Test
    public void testCalculateBalanceAboveMax() {
        int tableMinBuyIn = 100;
        int tableMaxBuyIn = 20000;
        int anteLevel = 20;
        BuyInCalculator blc = new BuyInCalculator();
        
        MinAndMaxBuyInResult result;

        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMaxBuyIn);
        assertThat(result.getMinBuyIn(), is(0));
        assertThat(result.getMaxBuyIn(), is(0));
        assertThat(result.isBuyInPossible(), is(false));
        
        result = blc.calculateBuyInLimits(tableMinBuyIn, tableMaxBuyIn, anteLevel, tableMaxBuyIn + 1);
        assertThat(result.getMinBuyIn(), is(0));
        assertThat(result.getMaxBuyIn(), is(0));
        assertThat(result.isBuyInPossible(), is(false));
    }

    @Test
    public void testCalculateReserveAmount() {
        int tableMaxBuyIn = 20000;
        BuyInCalculator blc = new BuyInCalculator();
        
        assertThat(blc.calculateAmountToReserve(tableMaxBuyIn, 5000, 20000), is(20000 - 5000));
        assertThat(blc.calculateAmountToReserve(tableMaxBuyIn, 5000, 2000), is(2000));
        assertThat(blc.calculateAmountToReserve(tableMaxBuyIn, 0, 20000), is(20000));
        assertThat(blc.calculateAmountToReserve(tableMaxBuyIn, 20000, 20000), is(0));
    }
    
}

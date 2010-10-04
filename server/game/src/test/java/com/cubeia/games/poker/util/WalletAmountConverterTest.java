package com.cubeia.games.poker.util;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class WalletAmountConverterTest extends TestCase {

    public void testConvertToWalletAmount() {
        WalletAmountConverter wac = new WalletAmountConverter(2);
        
        BigDecimal a = wac.convertToWalletAmount(-12345);
        assertEquals(new BigDecimal("-123.45"), a);
        assertEquals(2, a.scale());
    }

    public void testConvertToInternalScaledAmount() {
        WalletAmountConverter wac = new WalletAmountConverter(2);
        int a = wac.convertToInternalScaledAmount(new BigDecimal("-123"));
        assertEquals(-12300, a);
    }

}

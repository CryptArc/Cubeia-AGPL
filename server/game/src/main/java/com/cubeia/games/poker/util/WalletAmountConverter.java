package com.cubeia.games.poker.util;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Conversion utility between wallet and internal representation of amounts. 
 * @author w
 */
public class WalletAmountConverter {
    private int decimalDigits = 2;

    /**
     * Creates a converter instance with the default number of fractional digits (2).
     */
    public WalletAmountConverter() {
        this(Currency.getInstance("USD").getDefaultFractionDigits());
    }
    
    public WalletAmountConverter(int digits) {
        this.decimalDigits = digits;
    }
    
    /**
     * Converts the internal amount (which is scaled up by the number of decimal digits
     * of the current currency) to an unscaled big decimal used by the wallet.
     * @param amount the amount to convert
     * @return the converted amount
     */
    public BigDecimal convertToWalletAmount(long amount) {
        return new BigDecimal(amount).scaleByPowerOfTen(-decimalDigits);
    }
    
    /**
     * Converts an unscaled wallet amount to the internal (upscaled) representation.
     * The amount is multiplied by 10^n where n is the number of decimal digits in
     * used currency.
     * @param amount the amount to convert
     * @return the converted amount
     */
    public int convertToInternalScaledAmount(BigDecimal amount) {
        return amount.scaleByPowerOfTen(decimalDigits).intValueExact();
    }
    
}

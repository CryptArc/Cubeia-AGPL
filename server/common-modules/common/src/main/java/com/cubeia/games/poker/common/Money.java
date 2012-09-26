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

package com.cubeia.games.poker.common;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Immutable domain object representing money.
 * <p/>
 * The value must be multiplied by 10^fractionalDigits. For example: $12.34
 * should be stored as Money(1234, "USD", 2).
 *
 * @author w
 */
public final class Money implements Serializable {
    private static final long serialVersionUID = 6524466586945917257L;

    private final String currencyCode;
    private final int fractionalDigits;
    private final long amount;

    public Money(long amount, String currencyCode, int fractionalDigits) {
        super();
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.fractionalDigits = fractionalDigits; 
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public int getFractionalDigits() {
        return fractionalDigits;
    }

    public long getAmount() {
        return amount; 
    }

    /**
     * Returns a new money object by adding the given money to this money.
     * The currencies must be the same.
     *
     * @param m money to add
     * @return the sum of this money and the given money
     * @throws IllegalArgumentException if the currencies are incompatible
     */
    public Money add(Money m) {
        if (getFractionalDigits() != m.getFractionalDigits() || !getCurrencyCode().equals(m.getCurrencyCode())) {
            throw new IllegalArgumentException("incompatible currencies: this = " +
                    getCurrencyCode() + "+" + getFractionalDigits() + ", other = " +
                    m.getCurrencyCode() + "+" + m.getFractionalDigits());
        }
        return new Money(getAmount() + m.getAmount(), getCurrencyCode(), getFractionalDigits());
    }

    /**
     * Subtract the given money from this money.
     * This is the same as doing this.add(that.negate()).
     *
     * @param m money to subtract
     * @return the result
     */
    public Money subtract(Money m) {
        return this.add(m.negate());
    }

    /**
     * Returns a new money object with the given scalar amount added.
     *
     * @param amount amount to add
     * @return new money with amount added
     */
    public Money add(long amount) {
        return new Money(getAmount() + amount, getCurrencyCode(), getFractionalDigits());
    }

    public Money negate() {
        return new Money(-getAmount(), getCurrencyCode(), getFractionalDigits());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (amount ^ (amount >>> 32));
        result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
        result = prime * result + fractionalDigits;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        if (amount != other.amount)
            return false;
        if (currencyCode == null) {
            if (other.currencyCode != null)
                return false;
        } else if (!currencyCode.equals(other.currencyCode))
            return false;
        if (fractionalDigits != other.fractionalDigits)
            return false;
        return true;
    }

    @Override
    public String toString() {
        BigDecimal amountBd = new BigDecimal(getAmount());
        amountBd = amountBd.movePointLeft(getFractionalDigits());
        return "" + amountBd + " " + getCurrencyCode();
    }
}

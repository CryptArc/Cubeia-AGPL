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

package com.cubeia.poker.handhistory.api;

import java.io.Serializable;

public class Amount implements Serializable {

    private static final long serialVersionUID = -1731897823595502084L;

	public static enum Type {
        BET,
        RAISE,
        STACK,
        OTHER
    }

    public Amount() {
    }

    public static Amount bet(long amount) {
        return new Amount(Type.BET, amount);
    }

    public static Amount raise(long amount) {
        return new Amount(Type.RAISE, amount);
    }

    public static Amount stack(long amount) {
        return new Amount(Type.STACK, amount);
    }

    public static Amount other(long amount) {
        return new Amount(Type.OTHER, amount);
    }

    private Type type;
    private long amount;

    private Amount(Type type, long amount) {
        this.type = type;
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (amount ^ (amount >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Amount other = (Amount) obj;
        if (amount != other.amount)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Amount [type=" + type + ", amount=" + amount + "]";
    }
}

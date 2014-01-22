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

public enum BestHandType {
    NOT_RANKED(0, "Not ranked"),
    HIGH_CARD(1, "High Card"),
    PAIR(2, "Pair"),
    TWO_PAIRS(3, "Two Pairs"),
    THREE_OF_A_KIND(4, "Three Of A Kind"),
    STRAIGHT(5, "Straight"),
    FLUSH(7, "Flush"),
    FULL_HOUSE(6, "Full House"),
    FOUR_OF_A_KIND(8, "Four Of A Kind"),
    STRAIGHT_FLUSH(9, "Straight Flush"),
    ROYAL_STRAIGHT_FLUSH(10, "Royal Straight Flush");

    public int specialHandTypeValue;
    private String name;

    private BestHandType() {
    }

    private BestHandType(int specialHandTypeValue, String name) {
        this.specialHandTypeValue = specialHandTypeValue;
        this.name = name;
    }

    private BestHandType(int specialHandTypeValue) {
        this.specialHandTypeValue = specialHandTypeValue;
    }

    public int getSpecialHandTypeValue() {
        return specialHandTypeValue;
    }

    public void setSpecialHandTypeValue(int specialHandTypeValue) {
        this.specialHandTypeValue = specialHandTypeValue;
    }

    public String getName() {
        return name;
    }
}
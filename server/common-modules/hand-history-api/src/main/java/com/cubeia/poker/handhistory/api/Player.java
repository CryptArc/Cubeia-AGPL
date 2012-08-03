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

public class Player {

    private int id;

    private long initialBalance;
    private int seatId;
    private String name;

    public Player() {
    }

    public Player(int id) {
        this.id = id;
    }

    public Player(int id, int seatId, long initialBalance, String name) {
        this.id = id;
        this.seatId = seatId;
        this.initialBalance = initialBalance;
        this.name = name;
    }

    public long getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(long balance) {
        this.initialBalance = balance;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getSeatId() {
        return seatId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result
                + (int) (initialBalance ^ (initialBalance >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + seatId;
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
        Player other = (Player) obj;
        if (id != other.id)
            return false;
        if (initialBalance != other.initialBalance)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (seatId != other.seatId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Player [id=" + id + ", initialBalance=" + initialBalance
                + ", seatId=" + seatId + ", name=" + name + "]";
    }
}

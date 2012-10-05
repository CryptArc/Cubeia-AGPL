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

public class PlayerAction extends HandHistoryEvent {

    private static final long serialVersionUID = 2633660241901849321L;

	public enum Type {
        SMALL_BLIND, BIG_BLIND, CALL, CHECK, BET, RAISE, FOLD, DECLINE_ENTRY_BET, ANTE, BIG_BLIND_PLUS_DEAD_SMALL_BLIND, DEAD_SMALL_BLIND;
    }

    private Type action;
    private Amount amount;
    private boolean timeout;

    private int playerId;

    public PlayerAction() {
    }

    public PlayerAction(int playerId) {
        this.playerId = playerId;
    }

    public PlayerAction(int playerId, Type action) {
        this.playerId = playerId;
        this.action = action;
    }

    public PlayerAction(int playerId, Type action, Amount amount) {
        this.playerId = playerId;
        this.action = action;
        this.amount = amount;
    }

    public Type getAction() {
        return action;
    }

    public void setAction(Type action) {
        this.action = action;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Amount getAmount() {
        return amount;
    }

    public boolean isTimout() {
        return timeout;
    }

    public void setTimout(boolean timout) {
        this.timeout = timout;
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + (playerId ^ (playerId >>> 32));
        result = prime * result + (timeout ? 1231 : 1237);
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
        PlayerAction other = (PlayerAction) obj;
        if (action != other.action)
            return false;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (playerId != other.playerId)
            return false;
        if (timeout != other.timeout)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PlayerAction [action=" + action + ", amount=" + amount + ", timeout=" + timeout + ", playerId=" + playerId + "]";
    }
}

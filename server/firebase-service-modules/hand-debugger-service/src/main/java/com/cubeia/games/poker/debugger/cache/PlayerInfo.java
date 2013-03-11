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

package com.cubeia.games.poker.debugger.cache;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
public class PlayerInfo {

    private final int tableId;
    private final int playerId;
    private final String name;
    private final boolean isSittingIn;
    private final long balance;
    private final long betstack;
    private final Date timestamp;

    PlayerInfo(int tableId, int playerId, String name, boolean isSittingIn, long balance, long betstack, Date timestamp) {
        this.tableId = tableId;
        this.playerId = playerId;
        this.name = name;
        this.isSittingIn = isSittingIn;
        this.balance = balance;
        this.betstack = betstack;
        this.timestamp = timestamp;
    }

    public int getTableId() {
        return tableId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public boolean isSittingIn() {
        return isSittingIn;
    }

    public long getBalance() {
        return balance;
    }

    public long getBetstack() {
        return betstack;
    }

    public Date getTimestamp() {
        return new Date(timestamp.getTime());
    }

    @Override
    public String toString() {
        return "PlayerInfo [tableId=" + tableId + ", playerId=" + playerId + ", name=" + name + ", isSittingIn="
                + isSittingIn + ", balance=" + balance + ", betstack=" + betstack + ", timestamp=" + timestamp + "]";
    }


}
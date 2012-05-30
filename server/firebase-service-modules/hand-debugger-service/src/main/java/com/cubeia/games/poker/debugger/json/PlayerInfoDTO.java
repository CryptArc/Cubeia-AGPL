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

package com.cubeia.games.poker.debugger.json;

import com.cubeia.games.poker.debugger.cache.PlayerInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
public class PlayerInfoDTO {

    public int tableId;
    public int playerId;
    public String name;
    public boolean isSittingIn;
    public long balance;
    public long betstack;
    public Date timestamp;

    public static PlayerInfoDTO createFrom(PlayerInfo info) {
        if (info == null) {
            return null;
        }

        PlayerInfoDTO dto = new PlayerInfoDTO();
        dto.tableId = info.getTableId();
        dto.playerId = info.getPlayerId();
        dto.name = info.getName();
        dto.isSittingIn = info.isSittingIn();
        dto.balance = info.getBalance();
        dto.betstack = info.getBetstack();
        dto.timestamp = info.getTimestamp();
        return dto;
    }

}
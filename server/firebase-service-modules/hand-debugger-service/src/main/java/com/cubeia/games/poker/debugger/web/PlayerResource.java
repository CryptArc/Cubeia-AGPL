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

package com.cubeia.games.poker.debugger.web;

import com.cubeia.games.poker.debugger.cache.PlayerInfo;
import com.cubeia.games.poker.debugger.cache.TablePlayerInfoCache;
import com.cubeia.games.poker.debugger.json.PlayerInfoDTO;
import com.cubeia.games.poker.debugger.json.PlayerInfoListDTO;
import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Path("/player")
@Produces({MediaType.APPLICATION_JSON})
public class PlayerResource {

    @Inject
    TablePlayerInfoCache cache;

    @GET
    @Path("{playerId}")
    public PlayerInfoListDTO getPlayerInfo(@PathParam("playerId") int playerId) {
        PlayerInfo playerInfoById = cache.getPlayerInfoById(playerId);
        System.err.println("player info by player id = " + playerId + ": " + playerInfoById);

        return createPlayerInfoListDTO(Collections.singletonList(playerInfoById));
    }

    @GET
    @Path("table/{tableId}")
    public PlayerInfoListDTO getHandHistory(@PathParam("tableId") int tableId) {
        List<PlayerInfo> playerInfos = new ArrayList<PlayerInfo>(cache.getPlayerInfosByTableId(tableId));

        Collections.sort(playerInfos, new Comparator<PlayerInfo>() {
            @Override
            public int compare(PlayerInfo o1, PlayerInfo o2) {
                return o1.getPlayerId() - o2.getPlayerId();
            }
        });

        return createPlayerInfoListDTO(playerInfos);
    }

    private PlayerInfoListDTO createPlayerInfoListDTO(List<PlayerInfo> playerInfos) {
        PlayerInfoListDTO pil = new PlayerInfoListDTO();
        if (playerInfos != null && playerInfos.size() > 0) {
            pil.setPlayers(new ArrayList<PlayerInfoDTO>());
            for (PlayerInfo pi : playerInfos) {
                pil.getPlayers().add(PlayerInfoDTO.createFrom(pi));
            }
        }
        return pil;
    }

}

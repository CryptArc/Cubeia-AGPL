package com.cubeia.games.poker.debugger.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cubeia.games.poker.debugger.cache.PlayerInfo;
import com.cubeia.games.poker.debugger.cache.TablePlayerInfoCache;
import com.cubeia.games.poker.debugger.json.PlayerInfoDTO;
import com.cubeia.games.poker.debugger.json.PlayerInfoListDTO;
import com.google.inject.Inject;

@Path("/player")
@Produces({ MediaType.APPLICATION_JSON })
public class PlayerResource {
	
	@Inject TablePlayerInfoCache cache;
	
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
        if (playerInfos != null  &&  playerInfos.size() > 0) {
            pil.setPlayers(new ArrayList<PlayerInfoDTO>());
            for (PlayerInfo pi : playerInfos) {
                pil.getPlayers().add(PlayerInfoDTO.createFrom(pi));
            }
        }
        return pil;
    }
    
}

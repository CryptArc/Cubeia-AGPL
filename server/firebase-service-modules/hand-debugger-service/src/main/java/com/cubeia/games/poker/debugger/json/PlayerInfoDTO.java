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
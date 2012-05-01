package com.cubeia.games.poker.debugger.json;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
public class PlayerInfoListDTO {
    private List<PlayerInfoDTO> players;

    public PlayerInfoListDTO() {
    }

    public List<PlayerInfoDTO> getPlayers() {
        return players;
    }

    ;

    public void setPlayers(List<PlayerInfoDTO> players) {
        this.players = players;
    }
}

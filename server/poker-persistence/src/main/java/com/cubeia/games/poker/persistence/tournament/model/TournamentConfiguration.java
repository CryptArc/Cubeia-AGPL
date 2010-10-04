package com.cubeia.games.poker.persistence.tournament.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class TournamentConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    
    private String name;
    
    @Enumerated(value=EnumType.STRING)
    private TournamentStartType startType = TournamentStartType.SIT_AND_GO;
    
    private int seatsPerTable = 10;
    
    private int timingType = 0;
    
    private int minPlayers = 0;
    
    private int maxPlayers = 0;
    
    public TournamentConfiguration() {}
    
    public String toString() {
        return "id["+id+"] name["+name+"] startType["+startType+"] seats["+seatsPerTable+"] timing["+timingType+"] min["+minPlayers+"] max["+maxPlayers+"] ";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TournamentStartType getStartType() {
        return startType;
    }

    public void setStartType(TournamentStartType startType) {
        this.startType = startType;
    }

    public int getSeatsPerTable() {
        return seatsPerTable;
    }

    public void setSeatsPerTable(int seatsPerTable) {
        this.seatsPerTable = seatsPerTable;
    }

    public int getTimingType() {
        return timingType;
    }

    public void setTimingType(int timingType) {
        this.timingType = timingType;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayer) {
        this.maxPlayers = maxPlayer;
    }
    
    
    
}

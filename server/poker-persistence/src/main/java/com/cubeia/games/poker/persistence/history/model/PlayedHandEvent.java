package com.cubeia.games.poker.persistence.history.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PlayedHandEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private int playerId = -1;
    
    private Long bet;
    
    private EventType type;
    
    private PlayedHand hand;

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Long getBet() {
        return bet;
    }

    public void setBet(Long bet) {
        this.bet = bet;
    }

    @Enumerated(EnumType.STRING)
    public EventType getType() {   
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    @ManyToOne
    public PlayedHand getHand() {
        return hand;
    }

    public void setHand(PlayedHand hand) {
        this.hand = hand;
    }
    
    
}

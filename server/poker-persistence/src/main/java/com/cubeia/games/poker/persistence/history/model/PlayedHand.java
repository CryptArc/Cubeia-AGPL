package com.cubeia.games.poker.persistence.history.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.log4j.Logger;

@Entity
public class PlayedHand implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    private static Logger log = Logger.getLogger(PlayedHand.class);
    
	private Integer id;

    private int tableId = -1;
    
    private Date date = new Date();
    
    private Set<PlayedHandEvent> events;

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToMany(mappedBy="hand", cascade={CascadeType.ALL})
    public Set<PlayedHandEvent> getEvents() {
    	if (events.size() > 150) {
    		log.warn("LARGE HAND HISTORY WARNING: Events="+events.size());
    	}
        return events;
    }

    public void setEvents(Set<PlayedHandEvent> events) {
        this.events = events;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}

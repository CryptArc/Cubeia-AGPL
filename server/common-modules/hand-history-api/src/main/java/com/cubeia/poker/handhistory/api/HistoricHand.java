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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoricHand implements Serializable {

    private static final long serialVersionUID = 6433976199796752600L;

    /** This is not the hand id! Only here for json de-serialization. (TODO: Fixable?) */
    private String id;
    private Table table;
    
    private long startTime;
    private long endTime;

    private DeckInfo deckInfo;
    private Results results;

    private final List<HandHistoryEvent> events = new ArrayList<HandHistoryEvent>();
    private final  List<Player> seats = new ArrayList<Player>(6);

    public HistoricHand() { }

    public HistoricHand(String id) {
        this.id = id;
    }
    
    public Table getTable() {
		return table;
	}
    
    public void setTable(Table table) {
		this.table = table;
	}

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public void setDeckInfo(DeckInfo deckInfo) {
        this.deckInfo = deckInfo;
    }

    public DeckInfo getDeckInfo() {
        return deckInfo;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<HandHistoryEvent> getEvents() {
        return events;
    }

    public List<Player> getSeats() {
        return seats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deckInfo == null) ? 0 : deckInfo.hashCode());
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + ((events == null) ? 0 : events.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((results == null) ? 0 : results.hashCode());
		result = prime * result + ((seats == null) ? 0 : seats.hashCode());
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		result = prime * result + ((table == null) ? 0 : table.hashCode());
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
		HistoricHand other = (HistoricHand) obj;
		if (deckInfo == null) {
			if (other.deckInfo != null)
				return false;
		} else if (!deckInfo.equals(other.deckInfo))
			return false;
		if (endTime != other.endTime)
			return false;
		if (events == null) {
			if (other.events != null)
				return false;
		} else if (!events.equals(other.events))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (results == null) {
			if (other.results != null)
				return false;
		} else if (!results.equals(other.results))
			return false;
		if (seats == null) {
			if (other.seats != null)
				return false;
		} else if (!seats.equals(other.seats))
			return false;
		if (startTime != other.startTime)
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

    @Override
	public String toString() {
		return "HistoricHand [id=" + id + ", table=" + table + ", startTime="
				+ startTime + ", endTime=" + endTime + ", deckInfo=" + deckInfo
				+ ", results=" + results + ", events=" + events + ", seats="
				+ seats + "]";
	}
}

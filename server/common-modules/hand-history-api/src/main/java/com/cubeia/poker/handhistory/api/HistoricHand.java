package com.cubeia.poker.handhistory.api;

import java.util.ArrayList;
import java.util.List;

public class HistoricHand {

    private final HandIdentification handId;

    private long startTime;
    private long endTime;

    private DeckInfo deckInfo;
    private Results results;

    private final List<HandHistoryEvent> events = new ArrayList<HandHistoryEvent>();
    private final List<Player> seats = new ArrayList<Player>(6);

    public HistoricHand(HandIdentification id) {
        this.handId = id;
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

    public HandIdentification getHandId() {
        return handId;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((deckInfo == null) ? 0 : deckInfo.hashCode());
        result = prime * result + (int) (endTime ^ (endTime >>> 32));
        result = prime * result + ((events == null) ? 0 : events.hashCode());
        result = prime * result + ((handId == null) ? 0 : handId.hashCode());
        result = prime * result + ((results == null) ? 0 : results.hashCode());
        result = prime * result + ((seats == null) ? 0 : seats.hashCode());
        result = prime * result + (int) (startTime ^ (startTime >>> 32));
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
        if (handId == null) {
            if (other.handId != null)
                return false;
        } else if (!handId.equals(other.handId))
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
        return true;
    }

    @Override
    public String toString() {
        return "HistoricHand [handId=" + handId + ", startTime=" + startTime
                + ", endTime=" + endTime + ", deckInfo=" + deckInfo
                + ", events=" + events + ", seats=" + seats + ", results="
                + results + "]";
    }
}

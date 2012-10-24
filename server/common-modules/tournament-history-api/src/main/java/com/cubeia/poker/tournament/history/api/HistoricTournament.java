package com.cubeia.poker.tournament.history.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

public class HistoricTournament implements Serializable {

    private String id;

    private int tournamentId;

    private int tournamentTemplateId;

    private String tournamentName;

    private long startTime;

    private long endTime;

    private List<TournamentEvent> events = new ArrayList<TournamentEvent>();

    private List<PlayerPosition> positions = new ArrayList<PlayerPosition>();

    public HistoricTournament() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getTournamentTemplateId() {
        return tournamentTemplateId;
    }

    public void setTournamentTemplateId(int tournamentTemplateId) {
        this.tournamentTemplateId = tournamentTemplateId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
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

    public List<TournamentEvent> getEvents() {
        return events;
    }

    public void setEvents(List<TournamentEvent> events) {
        this.events = events;
    }

    public List<PlayerPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<PlayerPosition> positions) {
        this.positions = positions;
    }
}

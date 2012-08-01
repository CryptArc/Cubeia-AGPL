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

package com.cubeia.games.poker.tournament.configuration;

import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructure;

import java.io.Serializable;

/**
 * This used to be an entity class in the now discarded
 * poker-persistence module.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class TournamentConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private int seatsPerTable = 10;

    private int timingType = 0;

    private int minPlayers = 0;

    private int maxPlayers = 0;

    private BlindsStructure blindsStructure;

    public TournamentConfiguration() {
    }

    public String toString() {
        return "id[" + id + "] name[" + name + "] seats[" + seatsPerTable + "] timing[" + timingType + "] min[" + minPlayers + "] max[" + maxPlayers + "] ";
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

    public BlindsStructure getBlindsStructure() {
        return blindsStructure;
    }

    public void setBlindsStructure(BlindsStructure blindsStructure) {
        this.blindsStructure = blindsStructure;
    }
}

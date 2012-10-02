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

import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructureFactory;
import com.cubeia.poker.timing.Timings;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class SitAndGoConfiguration implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(cascade = {CascadeType.ALL})
    private TournamentConfiguration configuration;

    public SitAndGoConfiguration() {
    }

    public TournamentConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(TournamentConfiguration configuration) {
        this.configuration = configuration;
    }

    public SitAndGoConfiguration(String name, int capacity, Timings timings) {
        configuration = new TournamentConfiguration();
        configuration.setName(name);
        configuration.setMinPlayers(capacity);
        configuration.setMaxPlayers(capacity);
        configuration.setSeatsPerTable(10);
        configuration.setTimingType(timings.ordinal());
        configuration.setBlindsStructure(BlindsStructureFactory.createDefaultBlindsStructure());
    }

    public SitAndGoConfiguration(String name, int capacity) {
        this(name, capacity, Timings.DEFAULT);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

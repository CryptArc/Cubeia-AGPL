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

import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This is the configuration for one stream of scheduled tournaments.
 * <p/>
 * Given this configuration, we can get the schedule and figure out when to start tournaments.
 */
@Entity
public class ScheduledTournamentConfiguration implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(cascade = {CascadeType.ALL})
    private TournamentSchedule schedule;

    @ManyToOne(cascade = {CascadeType.ALL})
    private TournamentConfiguration configuration;

    public ScheduledTournamentConfiguration() {
        this.configuration = new TournamentConfiguration();
    }

    public ScheduledTournamentConfiguration(TournamentSchedule schedule, String name, int id) {
        this.schedule = schedule;
        this.configuration = new TournamentConfiguration();
        configuration.setName(name);
        configuration.setId(id);
    }

    public TournamentSchedule getSchedule() {
        return schedule;
    }

    public ScheduledTournamentInstance spawnConfigurationForNextInstance(DateTime startTime) {
        return new ScheduledTournamentInstance(this, startTime);
    }

    public void setSchedule(TournamentSchedule schedule) {
        this.schedule = schedule;
    }

    public TournamentConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(TournamentConfiguration configuration) {
        this.configuration = configuration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

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

package com.cubeia.games.poker.tournament.activator.configuration;

import org.joda.time.DateTime;

/**
 * This is the configuration for one stream of scheduled tournaments.
 *
 * Given this configuration, we can get the schedule and figure out when to start tournaments.
 */
public class ScheduledTournamentConfiguration extends TournamentConfiguration {

    private TournamentSchedule schedule;

    public ScheduledTournamentConfiguration(TournamentSchedule schedule) {
        this.schedule = schedule;
    }

    public TournamentSchedule getSchedule() {
        return schedule;
    }

    public ScheduledTournamentInstance spawnConfigurationForNextInstance(DateTime startTime) {
        return new ScheduledTournamentInstance(this, startTime);
    }

}

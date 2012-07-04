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

import com.cubeia.poker.timing.Timings;

public class SitAndGoConfiguration extends TournamentConfiguration {

    public SitAndGoConfiguration(String name, int capacity, Timings timings) {
        super.setName(name);
        super.setMinPlayers(capacity);
        super.setMaxPlayers(capacity);
        super.setSeatsPerTable(10);
        super.setStartType(TournamentStartType.SIT_AND_GO);
        super.setTimingType(timings.ordinal());
    }

    public SitAndGoConfiguration(String name, int capacity) {
        this(name, capacity, Timings.DEFAULT);
    }
}

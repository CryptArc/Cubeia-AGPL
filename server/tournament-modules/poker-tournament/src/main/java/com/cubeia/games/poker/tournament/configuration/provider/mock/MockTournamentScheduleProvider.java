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

package com.cubeia.games.poker.tournament.configuration.provider.mock;

import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentSchedule;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructureFactory;
import com.cubeia.games.poker.tournament.configuration.provider.TournamentScheduleProvider;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.Collection;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * This is a mock provider for providing the tournament schedule. A real implementation would fetch the tournament
 * schedule from a database.
 */
public class MockTournamentScheduleProvider implements TournamentScheduleProvider {

    @Override
    public Collection<ScheduledTournamentConfiguration> getTournamentSchedule() {
        Collection<ScheduledTournamentConfiguration> tournamentConfigurations = Lists.newArrayList();
        ScheduledTournamentConfiguration everyTenMinutes = everyTenMinutes();
        everyTenMinutes.getConfiguration().setMinPlayers(2);
        everyTenMinutes.getConfiguration().setMaxPlayers(100);
        everyTenMinutes.getConfiguration().setBlindsStructure(BlindsStructureFactory.createDefaultBlindsStructure());
        tournamentConfigurations.add(everyTenMinutes);
        return tournamentConfigurations;
    }

    private ScheduledTournamentConfiguration everyTenMinutes() {
        TournamentSchedule tournamentSchedule = new TournamentSchedule(new DateTime(2011, 7, 5, 9, 0, 0).toDate(), new DateTime(2022, 7, 5, 9, 0, 0).toDate(),
                "0 */10 * * * ?", 3, 5, 5);
        return new ScheduledTournamentConfiguration(tournamentSchedule, "Every Ten Minutes", 1);
    }
}

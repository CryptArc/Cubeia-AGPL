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
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentSchedule;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructureFactory;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructure;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructureParser;
import com.cubeia.games.poker.tournament.configuration.provider.TournamentScheduleProvider;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * This is a mock provider for providing the tournament schedule. A real implementation would fetch the tournament
 * schedule from a database.
 */
public class MockTournamentScheduleProvider implements TournamentScheduleProvider {

    @Override
    public Collection<ScheduledTournamentConfiguration> getTournamentSchedule() {
        InputStream resourceAsStream = getClass().getResourceAsStream("default_payouts.csv");
        PayoutStructure payouts = new PayoutStructureParser().parsePayouts(resourceAsStream);
        Collection<ScheduledTournamentConfiguration> tournamentConfigurations = Lists.newArrayList();
        ScheduledTournamentConfiguration everyTenMinutes = everyTenMinutes();
        TournamentConfiguration configuration = everyTenMinutes.getConfiguration();
        configuration.setMinPlayers(2);
        configuration.setMaxPlayers(100);
        configuration.setBlindsStructure(BlindsStructureFactory.createDefaultBlindsStructure());
        configuration.setBuyIn(BigDecimal.valueOf(10));
        configuration.setFee(BigDecimal.valueOf(1));
        configuration.setPayoutStructure(payouts);
        configuration.setCurrency("EUR");
        configuration.setTimingType(TimingFactory.getRegistry().getDefaultTimingProfile());
        tournamentConfigurations.add(everyTenMinutes);
        return tournamentConfigurations;
    }

    @Override
    public ScheduledTournamentConfiguration getScheduledTournamentConfiguration(int id) {
        return null;
    }

    @Override
    public SitAndGoConfiguration getSitAndGoTournamentConfiguration(int id) {
        return null;
    }

    private ScheduledTournamentConfiguration everyTenMinutes() {
        TournamentSchedule tournamentSchedule = new TournamentSchedule(new DateTime(2011, 7, 5, 9, 0, 0).toDate(), new DateTime(2022, 7, 5, 9, 0, 0).toDate(),
                "0 */10 * * * ?", 3, 5, 5);
        return new ScheduledTournamentConfiguration(tournamentSchedule, "Every Ten Minutes", 1);
    }
}

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

import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.provider.SitAndGoConfigurationProvider;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.timing.Timings;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;


/**
 * The mock provider creates new tournament automatically without the need of a database.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class MockSitAndGoConfigurationProvider implements SitAndGoConfigurationProvider {

    private static transient Logger log = Logger.getLogger(MockSitAndGoConfigurationProvider.class);

    private Map<String, SitAndGoConfiguration> requestedTournaments = newHashMap();

    /*------------------------------------------------

       LIFECYCLE METHODS

    ------------------------------------------------*/

    public MockSitAndGoConfigurationProvider() {
        requestedTournaments.put("Heads up", createSitAndGoConfiguration("Heads up", 2, TimingFactory.getRegistry().getTimingProfile("DEFAULT")));
        requestedTournaments.put("5 Players", createSitAndGoConfiguration("5 Players", 5, TimingFactory.getRegistry().getTimingProfile("SUPER_EXPRESS")));
        requestedTournaments.put("10 Players", createSitAndGoConfiguration("10 Players", 10, TimingFactory.getRegistry().getTimingProfile("SUPER_EXPRESS")));
        requestedTournaments.put("20 Players", createSitAndGoConfiguration("20 Players", 20, TimingFactory.getRegistry().getTimingProfile("DEFAULT")));
        requestedTournaments.put("100 Players", createSitAndGoConfiguration("100 Players", 100, TimingFactory.getRegistry().getTimingProfile("SUPER_EXPRESS")));
        requestedTournaments.put("1000 Players", createSitAndGoConfiguration("1000 Players", 1000, TimingFactory.getRegistry().getTimingProfile("SUPER_EXPRESS")));
        requestedTournaments.put("2000 Players", createSitAndGoConfiguration("2000 Players", 2000, TimingFactory.getRegistry().getTimingProfile("DEFAULT")));
        requestedTournaments.put("5000 Players", createSitAndGoConfiguration("5000 Players", 5000, TimingFactory.getRegistry().getTimingProfile("EXPRESS")));
        requestedTournaments.put("10000 Players", createSitAndGoConfiguration("10000 Players", 10000, TimingFactory.getRegistry().getTimingProfile("EXPRESS")));
    }

    private SitAndGoConfiguration createSitAndGoConfiguration(String name, int capacity, TimingProfile timings) {
        SitAndGoConfiguration configuration = new SitAndGoConfiguration(name, capacity, timings);
        configuration.getConfiguration().setBuyIn(BigDecimal.valueOf(10));
        configuration.getConfiguration().setFee(BigDecimal.valueOf(1));

        return configuration;
    }

    public Collection<SitAndGoConfiguration> getConfigurations() {
        return requestedTournaments.values();
    }

}

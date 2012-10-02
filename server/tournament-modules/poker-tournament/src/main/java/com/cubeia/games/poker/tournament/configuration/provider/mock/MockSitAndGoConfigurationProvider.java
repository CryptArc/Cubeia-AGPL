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
import com.cubeia.poker.timing.Timings;
import org.apache.log4j.Logger;

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
        requestedTournaments.put("Heads up", new SitAndGoConfiguration("Heads up", 2));
        requestedTournaments.put("5 Players", new SitAndGoConfiguration("5 Players", 5, Timings.SUPER_EXPRESS));
        requestedTournaments.put("10 Players", new SitAndGoConfiguration("10 Players", 10, Timings.SUPER_EXPRESS));
        requestedTournaments.put("20 Players", new SitAndGoConfiguration("20 Players", 20));
        requestedTournaments.put("100 Players", new SitAndGoConfiguration("100 Players", 100, Timings.SUPER_EXPRESS));
        requestedTournaments.put("1000 Players", new SitAndGoConfiguration("1000 Players", 1000, Timings.SUPER_EXPRESS));
        requestedTournaments.put("2000 Players", new SitAndGoConfiguration("2000 Players", 2000));
        requestedTournaments.put("5000 Players", new SitAndGoConfiguration("5000 Players", 5000, Timings.EXPRESS));
        requestedTournaments.put("10000 Players", new SitAndGoConfiguration("10000 Players", 10000, Timings.EXPRESS));
    }

    public Collection<SitAndGoConfiguration> getConfigurations() {
        return requestedTournaments.values();
    }

}

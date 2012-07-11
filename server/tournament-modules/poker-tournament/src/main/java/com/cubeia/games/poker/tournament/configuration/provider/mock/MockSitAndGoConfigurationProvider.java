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
        requestedTournaments.put("headsup", new SitAndGoConfiguration("headsup", 2));
        requestedTournaments.put("ten", new SitAndGoConfiguration("ten", 10, Timings.SUPER_EXPRESS));
        requestedTournaments.put("hundred", new SitAndGoConfiguration("hundred", 100, Timings.SUPER_EXPRESS));
        requestedTournaments.put("1k", new SitAndGoConfiguration("1k", 1000, Timings.SUPER_EXPRESS));
        requestedTournaments.put("Five-oh", new SitAndGoConfiguration("Five-oh", 5000, Timings.EXPRESS));
        requestedTournaments.put("Big Ten", new SitAndGoConfiguration("Big Ten", 10000, Timings.EXPRESS));
        requestedTournaments.put("Twenty", new SitAndGoConfiguration("Twenty", 20));
        requestedTournaments.put("Faaivssouzand", new SitAndGoConfiguration("Faaivssouzand", 5000));
        requestedTournaments.put("Tensouzand", new SitAndGoConfiguration("Tensouzand", 10000));
        requestedTournaments.put("Oansouzand", new SitAndGoConfiguration("Oansouzand", 1000));
        requestedTournaments.put("2k", new SitAndGoConfiguration("2k", 2000));
    }

    public Collection<SitAndGoConfiguration> getConfigurations() {
        return requestedTournaments.values();
    }

}

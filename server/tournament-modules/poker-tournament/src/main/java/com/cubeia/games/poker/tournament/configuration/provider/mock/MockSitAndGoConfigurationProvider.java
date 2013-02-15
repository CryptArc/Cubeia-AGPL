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
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructure;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructureParser;
import com.cubeia.games.poker.tournament.configuration.provider.SitAndGoConfigurationProvider;
import com.cubeia.poker.timing.TimingProfile;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static com.cubeia.poker.timing.TimingFactory.getRegistry;
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
        InputStream resourceAsStream = getClass().getResourceAsStream("default_payouts.csv");
        PayoutStructure payouts = new PayoutStructureParser().parsePayouts(resourceAsStream);
        requestedTournaments.put("Heads up", createSitAndGoConfiguration("Heads up", 2, getRegistry().getTimingProfile("DEFAULT"), payouts));
        requestedTournaments.put("5 Players", createSitAndGoConfiguration("5 Players", 5, getRegistry().getTimingProfile("SUPER_EXPRESS"), payouts));
        requestedTournaments.put("10 Players", createSitAndGoConfiguration("10 Players", 10, getRegistry().getTimingProfile("SUPER_EXPRESS"), payouts));
        requestedTournaments.put("20 Players", createSitAndGoConfiguration("20 Players", 20, getRegistry().getTimingProfile("DEFAULT"), payouts));
        requestedTournaments.put("100 Players", createSitAndGoConfiguration("100 Players", 100, getRegistry().getTimingProfile("SUPER_EXPRESS"), payouts));
        requestedTournaments.put("1000 Players", createSitAndGoConfiguration("1000 Players", 1000, getRegistry().getTimingProfile("SUPER_EXPRESS"), payouts));
        requestedTournaments.put("2000 Players", createSitAndGoConfiguration("2000 Players", 2000, getRegistry().getTimingProfile("DEFAULT"), payouts));
        requestedTournaments.put("5000 Players", createSitAndGoConfiguration("5000 Players", 5000, getRegistry().getTimingProfile("EXPRESS"), payouts));
        requestedTournaments.put("10000 Players", createSitAndGoConfiguration("10000 Players", 10000, getRegistry().getTimingProfile("EXPRESS"), payouts));
    }

    private SitAndGoConfiguration createSitAndGoConfiguration(String name, int capacity, TimingProfile timings, PayoutStructure payoutStructure) {
        SitAndGoConfiguration configuration = new SitAndGoConfiguration(name, capacity, timings);
        configuration.getConfiguration().setBuyIn(BigDecimal.valueOf(10));
        configuration.getConfiguration().setFee(BigDecimal.valueOf(1));
        configuration.getConfiguration().setPayoutStructure(payoutStructure);
        configuration.getConfiguration().setCurrency("EUR");

        return configuration;
    }

    public Collection<SitAndGoConfiguration> getConfigurations() {
        return requestedTournaments.values();
    }

}

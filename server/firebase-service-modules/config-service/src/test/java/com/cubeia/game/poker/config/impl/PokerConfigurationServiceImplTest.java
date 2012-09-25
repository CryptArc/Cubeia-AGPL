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

package com.cubeia.game.poker.config.impl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.cubeia.firebase.api.service.ServiceRegistryAdapter;
import com.cubeia.firebase.api.service.config.ClusterConfigProviderContract;
import com.cubeia.firebase.api.service.config.ClusterConfigProviderContractAdapter;
import com.cubeia.firebase.guice.inject.FirebaseModule;
import com.cubeia.game.poker.config.api.PokerSystemConfig;
import com.cubeia.games.poker.common.Money;
import com.google.inject.Guice;
import com.google.inject.Inject;

public class PokerConfigurationServiceImplTest {

    @Inject
    private PokerConfigurationServiceImpl service;

    @Before
    public void setup() {
        Guice.createInjector(new FirebaseModule(new Registry())).injectMembers(this);
    }

    @Test
    public void checkSystemCurrency() {
        Assert.assertEquals(new Money(100, "SEK", 2), service.createSystemMoney(100));
    }

    private final class Registry extends ServiceRegistryAdapter {

        public Registry() {
            ClusterConfigProviderContractAdapter ad = new ClusterConfigProviderContractAdapter();
            ad.addConfiguration(PokerSystemConfig.class, new PokerSystemConfig() {

                @Override
                public int getSystemCurrencyFractions() {
                    return 2;
                }

                @Override
                public String getSystemCurrencyCode() {
                    return "SEK";
                }
            });
            super.addImplementation(ClusterConfigProviderContract.class, ad);
        }
    }
}

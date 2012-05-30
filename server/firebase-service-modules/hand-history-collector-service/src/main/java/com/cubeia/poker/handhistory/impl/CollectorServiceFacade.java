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

package com.cubeia.poker.handhistory.impl;

import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceService;
import com.cubeia.poker.handhistory.api.*;

import java.util.List;

public class CollectorServiceFacade extends GuiceService implements HandHistoryCollectorService {

    @Override
    public Configuration getConfigurationHelp() {
        return new Configuration() {

            @Override
            public ContractsConfig getServiceContract() {
                return new ContractsConfig(CollectorServiceImpl.class, HandHistoryCollectorService.class);
            }
        };
    }


    @Override
    public void reportEvent(int tableId, HandHistoryEvent event) {
        g().reportEvent(tableId, event);
    }

    @Override
    public void startHand(HandIdentification id, List<Player> seats) {
        g().startHand(id, seats);
    }

    @Override
    public void stopHand(int tableId) {
        g().stopHand(tableId);
    }

    @Override
    public void cancelHand(int tableId) {
        g().cancelHand(tableId);
    }

    @Override
    public void reportDeckInfo(int tableId, DeckInfo deckInfo) {
        g().reportDeckInfo(tableId, deckInfo);
    }

    @Override
    public void reportResults(int tableId, Results res) {
        g().reportResults(tableId, res);
    }

    // --- PRIVATE METHODS --- //

    private HandHistoryCollectorService g() {
        return guice(HandHistoryCollectorService.class);
    }
}

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

package com.cubeia.games.poker.activator;

import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.timing.TimingFactory;
import com.google.inject.Singleton;

import java.math.BigDecimal;
import java.util.List;

import static com.cubeia.poker.settings.RakeSettings.createDefaultRakeSettings;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;
import static java.util.Collections.singletonList;

@Singleton
public class SimpleTableConfigTemplateProvider implements TableConfigTemplateProvider {

    @Override
    public List<TableConfigTemplate> getTemplates() {
        TableConfigTemplate t = new TableConfigTemplate();
        t.setId(0);
        t.setSmallBlind(50);
        t.setBigBlind(100);
        t.setMinBuyIn(1000);
        t.setMaxBuyIn(10000);
        t.setSeats(10);
        t.setVariant(TEXAS_HOLDEM);
        t.setTiming(TimingFactory.getRegistry().getDefaultTimingProfile());
        t.setBetStrategy(BetStrategyType.NO_LIMIT);
        t.setTTL(60000);
        t.setMinEmptyTables(5);
        t.setMinTables(10);
        t.setRakeSettings(createDefaultRakeSettings(new BigDecimal(0.02)));
        return singletonList(t);
    }
}

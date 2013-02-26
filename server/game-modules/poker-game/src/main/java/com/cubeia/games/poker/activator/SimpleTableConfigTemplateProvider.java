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
import java.util.Arrays;
import java.util.List;

import static com.cubeia.poker.settings.RakeSettings.createDefaultRakeSettings;
import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;

@Singleton
public class SimpleTableConfigTemplateProvider implements TableConfigTemplateProvider {

    @Override
    public List<TableConfigTemplate> getTemplates() {
        TableConfigTemplate texasNoLimit = new TableConfigTemplate();
        texasNoLimit.setId(0);
        texasNoLimit.setSmallBlind(50);
        texasNoLimit.setBigBlind(100);
        texasNoLimit.setMinBuyIn(1000);
        texasNoLimit.setMaxBuyIn(10000);
        texasNoLimit.setSeats(10);
        texasNoLimit.setVariant(TEXAS_HOLDEM);
        texasNoLimit.setTiming(TimingFactory.getRegistry().getDefaultTimingProfile());
        texasNoLimit.setBetStrategy(BetStrategyType.NO_LIMIT);
        texasNoLimit.setTTL(60000);
        texasNoLimit.setMinEmptyTables(5);
        texasNoLimit.setMinTables(10);
        texasNoLimit.setRakeSettings(createDefaultRakeSettings(new BigDecimal(0.02)));
        texasNoLimit.setCurrency("EUR");

        TableConfigTemplate texasFixedLimit = new TableConfigTemplate();
        texasFixedLimit.setId(1);
        texasFixedLimit.setSmallBlind(50);
        texasFixedLimit.setBigBlind(100);
        texasFixedLimit.setMinBuyIn(1000);
        texasFixedLimit.setMaxBuyIn(10000);
        texasFixedLimit.setSeats(10);
        texasFixedLimit.setVariant(TEXAS_HOLDEM);
        texasFixedLimit.setTiming(TimingFactory.getRegistry().getDefaultTimingProfile());
        texasFixedLimit.setBetStrategy(BetStrategyType.FIXED_LIMIT);
        texasFixedLimit.setTTL(60000);
        texasFixedLimit.setMinEmptyTables(5);
        texasFixedLimit.setMinTables(10);
        texasFixedLimit.setRakeSettings(createDefaultRakeSettings(new BigDecimal(0.02)));
        texasFixedLimit.setCurrency("EUR");

        TableConfigTemplate telesina = new TableConfigTemplate();
        telesina.setId(2);
        telesina.setAnte(100);
        telesina.setSmallBlind(0);
        telesina.setBigBlind(0);
        telesina.setMinBuyIn(1000);
        telesina.setMaxBuyIn(10000);
        telesina.setSeats(6);
        telesina.setVariant(TELESINA);
        telesina.setTiming(TimingFactory.getRegistry().getDefaultTimingProfile());
        telesina.setBetStrategy(BetStrategyType.NO_LIMIT);
        telesina.setTTL(60000);
        telesina.setMinEmptyTables(1);
        telesina.setMinTables(1);
        telesina.setRakeSettings(createDefaultRakeSettings(new BigDecimal(0.02)));
        telesina.setCurrency("EUR");

        return Arrays.asList(texasNoLimit, texasFixedLimit, telesina);
    }
}

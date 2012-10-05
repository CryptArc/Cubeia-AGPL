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

import static com.cubeia.poker.timing.Timings.DEFAULT;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;
import static java.util.Collections.singletonList;

import java.util.List;

import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.google.inject.Singleton;

@Singleton
public class SimpleTableConfigTemplateProvider implements TableConfigTemplateProvider {

    @Override
    public List<TableConfigTemplate> getTemplates() {
        TableConfigTemplate t = new TableConfigTemplate();
        t.setId(0);
        t.setAnte(100);
        t.setSeats(10);
        t.setVariant(TEXAS_HOLDEM);
        t.setTiming(DEFAULT);
        t.setTTL(60000);
        t.setMinEmptyTables(5);
        t.setMinTables(10);
        return singletonList(t);
    }
}

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

package com.cubeia.games.poker.tournament.configuration.blinds;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BlindsStructure implements Serializable {

    private static final Logger log = Logger.getLogger(BlindsStructure.class);

    private long timePerLevel;

    private List<BlindsLevel> blindsLevels;

    public BlindsStructure(long millisPerLevel, List<BlindsLevel> blindsLevels) {
        checkNotNull(blindsLevels, "List of blinds levels can't be null");
        checkArgument(millisPerLevel > 0, "Time per level must be > 0");
        checkArgument(!blindsLevels.isEmpty(), "List of blinds levels can't be empty.");

        this.timePerLevel = millisPerLevel;
        this.blindsLevels = blindsLevels;
    }

    public long getTimeToNextLevel() {
        return timePerLevel;
    }

    public Iterator<BlindsLevel> getLevelIterator() {
        return blindsLevels.iterator();
    }
}

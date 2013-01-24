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

package com.cubeia.poker.shutdown.api;

import com.cubeia.firebase.api.service.Contract;

import java.util.List;

/**
 * Service contract for shutting down the system.
 *
 * Will be called via JMX.
 *
 */
public interface ShutdownServiceContract extends Contract {

    /**
     * Checks if the system is currently in shutting down mode.
     * @return true if the system is shutting down and false if not
     */
    public boolean isShuttingDown();

    void shutDownTournament(int tournamentId);
}

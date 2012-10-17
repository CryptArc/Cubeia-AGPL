/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.tournament.history;

import com.cubeia.poker.tournament.history.api.HistoricTournament;
import com.cubeia.poker.tournament.history.storage.api.TournamentHistoryPersistenceService;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.UUID;

public class LoggingHistoryPersister implements TournamentHistoryPersistenceService {

    private static final Logger log = Logger.getLogger(LoggingHistoryPersister.class);

    @Override
    public String createHistoricTournament() {
        return UUID.randomUUID().toString();
    }

    @Override
    public HistoricTournament getHistoricTournament(String id) {
        return null;
    }

    @Override
    public void playerOut(int playerId, int position, String historicId, Date now) {
        log.info("Tournament[ " + historicId + "]. Player " + playerId +  " finished in place " + position);
    }

    @Override
    public void playerMoved(int playerId, int tableId, String historicId, Date now) {
        log.info("Tournament[ " + historicId + "]. Player " + playerId +  " was moved to table " + tableId);
    }

    @Override
    public void statusChanged(String status, String historicId, Date now) {
        log.info("Tournament[ " + historicId + "]. Status updated to " + status);
    }

    @Override
    public void blindsUpdated(String historicId, int ante, int smallBlind, int bigBlind, Date now) {
        log.info("Tournament[ " + historicId + "]. Blinds updated to " + ante + " / " + smallBlind + " / " + bigBlind);
    }
}

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

import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.poker.tournament.history.storage.api.TournamentHistoryPersistenceService;
import org.apache.log4j.Logger;

public class HistoryPersister {

    private static final Logger log = Logger.getLogger(HistoryPersister.class);

    private String historicId;

    private TournamentHistoryPersistenceService storageService;

    private SystemTime dateFetcher;

    public HistoryPersister(String historicId, TournamentHistoryPersistenceService storageService, SystemTime dateFetcher) {
        this.historicId = historicId;
        this.dateFetcher = dateFetcher;
        if (storageService == null) {
            log.warn("No tournament history service available, logging to file.");
            this.storageService = new LoggingHistoryPersister();
        } else {
            this.storageService = storageService;
        }
    }

    public void playerOut(int playerId, int position) {
        storageService.playerOut(playerId, position, historicId, dateFetcher.date().toDate());
    }

    public void playerMoved(int playerId, int tableId) {
        storageService.playerMoved(playerId, tableId, historicId, dateFetcher.date().toDate());
    }

    public void statusChanged(String status) {
        storageService.statusChanged(status, historicId, dateFetcher.date().toDate());
    }

    public String createHistoricId() {
        return storageService.createHistoricTournament();
    }

    public void setHistoricId(String historicId) {
        this.historicId = historicId;
    }
}

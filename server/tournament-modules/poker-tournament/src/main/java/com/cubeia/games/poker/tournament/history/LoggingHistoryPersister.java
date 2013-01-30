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

package com.cubeia.games.poker.tournament.history;

import com.cubeia.poker.tournament.history.api.HistoricPlayer;
import com.cubeia.poker.tournament.history.api.HistoricTournament;
import com.cubeia.poker.tournament.history.storage.api.TournamentHistoryPersistenceService;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LoggingHistoryPersister implements TournamentHistoryPersistenceService {

    private static final Logger log = Logger.getLogger(LoggingHistoryPersister.class);

    @Override
    public void addTable(String historicId, String externalTableId) {
        log.info("Tournament[ " + historicId + "]. Table created " + externalTableId);
    }

    @Override
    public void blindsUpdated(String historicId, Integer ante, Integer smallBlind, Integer bigBlind, long now) {
        log.info("Tournament[ " + historicId + "]. Blinds updated to " + ante + " / " + smallBlind + " / " + bigBlind);
    }

    @Override
    public String createHistoricTournament(String name, int id, int templateId, boolean isSitAndGo) {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<HistoricTournament> findTournamentsToResurrect() {
        return Collections.emptyList();
    }

    @Override
    public HistoricTournament getHistoricTournament(String id) {
        return null;
    }

    @Override
    public void playerFailedOpeningSession(String historicId, int playerId, String message, long now) {
        log.info("Tournament[ " + historicId + "]. Player failed opening session " + playerId + " message: " + message);
    }

    @Override
    public void playerFailedUnregistering(String historicId, int playerId, String message, long now) {
        log.info("Tournament[ " + historicId + "]. Player failed un-registering " + playerId);
    }

    @Override
    public void playerMoved(int playerId, int tableId, String historicId, long now) {
        log.info("Tournament[ " + historicId + "]. Player " + playerId +  " was moved to table " + tableId);
    }

    @Override
    public void playerOpenedSession(String historicId, int playerId, String sessionId, long now) {
        log.info("Tournament[ " + historicId + "]. Player opened session " + playerId + " session: " + sessionId);
    }

    @Override
    public void playerOut(int playerId, int position, int payoutInCents, String historicId, long now) {
        log.info("Tournament[ " + historicId + "]. Player " + playerId +  " finished in place " + position + " and won: " + payoutInCents + " cents.");
    }

    @Override
    public void playerReRegistered(String historicId, int playerId, long now) {
        log.info("Tournament[ " + historicId + "]. Player re-registered " + playerId);
    }

    @Override
    public void playerRegistered(String historicId, HistoricPlayer player, long now) {
        log.info("Tournament[ " + historicId + "]. Player registered " + player);
    }

    @Override
    public void playerUnregistered(String historicId, int playerId, long now) {
        log.info("Tournament[ " + historicId + "]. Player un-registered " + playerId);
    }

    @Override
    public void setEndTime(String historicId, long date) {
        log.info("Tournament[ " + historicId + "]. End time " + date);
    }

    @Override
    public void setName(String historicId, String name) {
        log.info("Tournament[ " + historicId + "]. Name " + name);
    }

    @Override
    public void setScheduledStartTime(String historicId, Date startTime) {
        log.info("Tournament[ " + historicId + "]. Scheduled start time " + startTime);
    }

    @Override
    public void setTournamentSessionId(String sessionId, String historicId) {
        log.info("Tournament[ " + historicId + "]. SessionId: " + sessionId);
    }

    @Override
    public void setStartTime(String historicId, long date) {
        log.info("Tournament[ " + historicId + "]. Start time " + date);
    }

    @Override
    public void statusChanged(String status, String historicId, long now) {
        log.info("Tournament[ " + historicId + "]. Status updated to " + status);
    }
}

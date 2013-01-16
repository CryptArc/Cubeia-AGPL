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

package com.cubeia.poker.tournament.history.storage.impl;

import com.cubeia.poker.tournament.history.api.HistoricTournament;
import com.cubeia.poker.tournament.history.api.PlayerPosition;
import com.cubeia.poker.tournament.history.api.TournamentEvent;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import org.bson.types.ObjectId;

public class HistoricTournamentDaoNotUsed {

    private Datastore datastore;

    public HistoricTournamentDaoNotUsed(Datastore datastore) {
        this.datastore = datastore;
    }

    /**
     * Creates a new historic tournament in preparation for storing information about a new tournament.
     *
     * @return the id of the new historic tournament
     */
    public String createHistoricTournament() {
        HistoricTournament historicTournament = new HistoricTournament();
        Key<HistoricTournament> save = datastore.save(historicTournament);
        return historicTournament.getId();
    }

    public Key<HistoricTournament> createHistoricTournamentKey() {
        return datastore.save(new HistoricTournament());
    }

    public HistoricTournament getHistoricTournament(String historicId) {
        return createQuery(historicId).get();
    }

    public void setStartTime(String historicId, long date) {
        setProperty(historicId, "startTime", date);
    }

    public void setEndTime(String historicId, long date) {
        setProperty(historicId, "endTime", date);
    }

    public void setName(String historicId, String name) {
        setProperty(historicId, "name", name);
    }

    public void addTable(String historicId, String externalTableId) {
        UpdateOperations<HistoricTournament> update = datastore.createUpdateOperations(HistoricTournament.class).add("tables", externalTableId);
        datastore.update(createQuery(historicId), update);
    }

    public void addRegisteredPlayer(String historicId, int playerId) {
        addObjectToCollection(historicId, playerId, "registeredPlayers");
    }

    public void removeRegisteredPlayer(String historicId, int playerId) {
        removeObjectFromCollection(historicId, playerId, "registeredPlayers");
    }

    private void setProperty(String historicId, String property, Object value) {
        UpdateOperations<HistoricTournament> update = datastore.createUpdateOperations(HistoricTournament.class).set(property, value);
        datastore.update(createQuery(historicId), update);
    }
    
    private Query<HistoricTournament> createQuery(String historicId) {
        return datastore.createQuery(HistoricTournament.class).field("id").equal(new ObjectId(historicId));
    }    

    public void addEvent(String historicId, TournamentEvent event) {
        addObjectToCollection(historicId, event, "events");
    }

    public void addPlayerPosition(String historicId, PlayerPosition playerPosition) {
        addObjectToCollection(historicId, playerPosition, "positions");
    }

    private void addObjectToCollection(String historicId, Object object, String collection) {
        UpdateOperations<HistoricTournament> update = datastore.createUpdateOperations(HistoricTournament.class).add(collection, object);
        datastore.update(createQuery(historicId), update);
    }

    private void removeObjectFromCollection(String historicId, Object object, String collection) {
        UpdateOperations<HistoricTournament> update = datastore.createUpdateOperations(HistoricTournament.class).removeAll(collection, object);
        datastore.update(createQuery(historicId), update);
    }

    /**
     * Finds tournaments that need to be resurrected. A tournament needs to be resurrected if:
     *
     *  - It has been created but has not started.
     *  - It has at least one registered player.
     *
     * Note that once a tournament has started it needs to be resolved rather than resurrected.
     *
     * @return a list of tournaments that need to be resurrected
     */
    public Query<HistoricTournament> findTournamentsToResurrect() {
        return datastore.find(HistoricTournament.class).field("startTime").equal(0).field("registeredPlayers").exists();
    }

    public void store(Object object) {
        datastore.save(object);
    }

    public HistoricTournament getHistoricTournamentByKey(Key<HistoricTournament> id) {
        return datastore.getByKey(HistoricTournament.class, id);
    }
}

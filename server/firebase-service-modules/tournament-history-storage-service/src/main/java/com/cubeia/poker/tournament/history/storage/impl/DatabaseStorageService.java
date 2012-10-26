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

package com.cubeia.poker.tournament.history.storage.impl;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.games.poker.common.mongo.DatabaseStorageConfiguration;
import com.cubeia.games.poker.common.mongo.MongoStorage;
import com.cubeia.poker.tournament.history.api.HistoricTournament;
import com.cubeia.poker.tournament.history.api.PlayerPosition;
import com.cubeia.poker.tournament.history.api.TournamentEvent;
import com.cubeia.poker.tournament.history.storage.api.TournamentHistoryPersistenceService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

public class DatabaseStorageService implements TournamentHistoryPersistenceService, Service {

    private static final Logger log = Logger.getLogger(DatabaseStorageService.class);

    private MongoStorage mongoStorage;

    private DatabaseStorageConfiguration configuration;

    private static final String TOURNAMENT_COLLECTION = "tournaments";

    private Gson gson = createGson();

    @Override
    public String createHistoricTournament() {
        HistoricTournament tournament = new HistoricTournament();
        DBObject dbObject = persist(tournament);
        Object id = dbObject.get("_id");
        return id.toString();
    }

    @Override
    public HistoricTournament getHistoricTournament(String id) {
        DBObject dbObject = mongoStorage.getById(new ObjectId(id), TOURNAMENT_COLLECTION);
        HistoricTournament historicTournament = createGson().fromJson(dbObject.toString(), HistoricTournament.class);
        return historicTournament;
    }

    @Override
    public void playerOut(int playerId, int position, String historicId, long now) {
        addEvent(historicId, new TournamentEvent(now, "player " + playerId + " out", "" + position));
        addPlayerPosition(historicId, playerId, position);
    }

    @Override
    public void playerMoved(int playerId, int tableId, String historicId, long now) {
        addEvent(historicId, new TournamentEvent(now, "player " + playerId + " moved", "" + tableId));
    }

    @Override
    public void statusChanged(String status, String historicId, long now) {
        addEvent(historicId, new TournamentEvent(now, "status changed", status));
    }

    @Override
    public void blindsUpdated(String historicId, int ante, int smallBlind, int bigBlind, long now) {
        addEvent(historicId, new TournamentEvent(now, "blinds updated", ante + "/" + smallBlind + "/" + bigBlind));
    }

    @Override
    public void setStartTime(String historicId, long date) {
        setProperty(historicId, "startTime", date);
    }

    @Override
    public void setEndTime(String historicId, long date) {
        setProperty(historicId, "endTime", date);
    }

    @Override
    public void setName(String historicId, String name) {
        setProperty(historicId, "name", name);
    }

    private void setProperty(String historicId, String propertyName, Object value) {
        BasicDBObject tournamentId = new BasicDBObject().append("_id", new ObjectId(historicId));
        DBObject update = new BasicDBObject().append(propertyName, value);
        log.debug("Storing property " + propertyName + " to " + value + " for " + historicId);
        mongoStorage.update(tournamentId, update, TOURNAMENT_COLLECTION);
    }

    private void addEvent(String historicId, TournamentEvent event) {
        pushObject(historicId, event, "events");
    }

    private void addPlayerPosition(String historicId, int playerId, int position) {
        pushObject(historicId, new PlayerPosition(playerId, position), "positions");
    }

    private void pushObject(String historicId, Object object, String array) {
        BasicDBObject tournamentId = new BasicDBObject().append("_id", new ObjectId(historicId));
        DBObject dbObject = (DBObject) JSON.parse(gson.toJson(object));
        BasicDBObject updateCommand = new BasicDBObject().append("$push", new BasicDBObject(array, dbObject));
        mongoStorage.update(tournamentId, updateCommand, TOURNAMENT_COLLECTION);
    }

    private DBObject persist(HistoricTournament tournament) {
        DBObject dbObject = (DBObject) JSON.parse(gson.toJson(tournament));
        mongoStorage.persist(dbObject, TOURNAMENT_COLLECTION);
        return dbObject;
    }

    private Gson createGson() {
        GsonBuilder b = new GsonBuilder();
        b.setPrettyPrinting();
        return b.create();
    }

    protected DatabaseStorageConfiguration getConfiguration(ServiceContext context) {
        return new DatabaseStorageConfiguration().load(context.getServerConfigDirectory().getAbsolutePath());
    }

    protected MongoStorage getMongoStorage() {
        return new MongoStorage(configuration);
    }

    @Override
    public void init(ServiceContext context) throws SystemException {
        configuration = getConfiguration(context);
        mongoStorage = getMongoStorage();
    }

    @Override
    public void start() {
        mongoStorage.connect();
    }

    @Override
    public void stop() {
        mongoStorage.disconnect();
    }

    @Override
    public void destroy() {

    }

}

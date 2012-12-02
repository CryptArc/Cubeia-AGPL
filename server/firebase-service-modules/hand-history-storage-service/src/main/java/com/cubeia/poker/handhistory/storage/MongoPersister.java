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

package com.cubeia.poker.handhistory.storage;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.server.Startable;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.games.poker.common.mongo.DatabaseStorageConfiguration;
import com.cubeia.poker.handhistory.api.HandHistoryPersistenceService;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.poker.handhistory.impl.JsonHandHistoryLogger;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class MongoPersister implements HandHistoryPersistenceService, Startable {

    private static final String HANDS_COLLECTION = "hands";

    private Mongo db;
    private String host;
    private int port;
    private String databaseName;

    private final Logger log = Logger.getLogger(getClass());
    private JsonHandHistoryLogger jsonLogger = new JsonHandHistoryLogger();

    // TODO Fix configuration...
    public MongoPersister(ServiceContext con) {
        DatabaseStorageConfiguration configuration = null; // = getConfiguration().load(con);
        host = configuration.getHost();
        port = configuration.getPort();
        databaseName = configuration.getDatabaseName();
    }

    @Override
    public void start() {
        try {
            log.info("Opening connection to MongoDB.");
            db = connectToMongo(host, port);
        } catch (UnknownHostException e) {
            log.warn("Could not connect to mongo on host " + host + " and port " + port);
        }
    }

    @Override
    public void stop() {
        if (db != null) {
            log.info("Closing connection to MongoDB.");
            db.close();
        }
    }

    @Override
    public void persist(HistoricHand hand) {
        // log.debug("Persisting hand " + hand.getId() + " to mongo");
        if (db != null) {
            try {
                persistToMongo(hand);
            } catch (Exception e) {
                log.warn("Failed persisting hand history to mondodb. Please start a mongodb server on host " + host + " and port " + port, e);
                jsonLogger.persist(hand);
            }
        } else {
            jsonLogger.persist(hand);
        }
    }


    // --- PROTECTED METHODS --- //

    protected DatabaseStorageConfiguration getConfiguration() {
        return new DatabaseStorageConfiguration();
    }


    // --- PRIVATE METHODS --- //

    private void persistToMongo(HistoricHand hand) {
        DBObject dbObject = (DBObject) JSON.parse(jsonLogger.convertToJson(hand));
        db().getCollection(HANDS_COLLECTION).insert(dbObject);
        log.info("Done persisting hand to mongo");
    }

    private DB db() {
        try {
            if (db == null) {
                db = connectToMongo(host, port);
            }
            return db.getDB(databaseName);
        } catch (Exception e) {
            log.warn("Could not connect to mongo on host " + host + " port " + port, e);
            return null;
        }
    }

    private Mongo connectToMongo(String host, int port) throws UnknownHostException {
        return new Mongo(host.trim(), port);
    }
}

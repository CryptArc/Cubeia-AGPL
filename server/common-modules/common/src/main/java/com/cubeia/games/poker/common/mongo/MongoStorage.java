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

package com.cubeia.games.poker.common.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;

public class MongoStorage {

    private static final Logger log = Logger.getLogger(MongoStorage.class);

    private Mongo db;

    private String host;

    private int port;

    private String databaseName;

    public MongoStorage(DatabaseStorageConfiguration configuration) {
        host = configuration.getHost();
        port = configuration.getPort();
        databaseName = configuration.getDatabaseName();
    }

    public void persist(DBObject dbObject, String collection) {
        db().getCollection(collection).insert(dbObject);
    }

    public void update(DBObject objectToUpdate, DBObject update, String collection) {
        db().getCollection(collection).update(objectToUpdate, update);
    }

    public DBObject getById(ObjectId id, String collection) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", id);
        return db().getCollection(collection).findOne(query);
    }

    private DB db() {
        try {
            if (db == null) {
                db = connectToMongo();
            }
            return db.getDB(databaseName);
        } catch (Exception e) {
            log.warn("Could not connect to mongo on host " + host + " port " + port, e);
            return null;
        }
    }

    private Mongo connectToMongo() throws UnknownHostException {
        return new Mongo(host.trim(), port);
    }


    public void connect() {
        try {
            connectToMongo();
        } catch (UnknownHostException e) {
            log.warn("Could not connect to mongo on host " + host + " and port " + port);
        }
    }

    public void disconnect() {
        if (db != null) {
            log.info("Closing mongo.");
            db.close();
        }
    }
}

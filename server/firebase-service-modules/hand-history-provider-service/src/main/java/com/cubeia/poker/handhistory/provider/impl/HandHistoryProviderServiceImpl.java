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

package com.cubeia.poker.handhistory.provider.impl;

import com.cubeia.firebase.api.action.service.ClientServiceAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequest;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.handhistoryservice.io.protocol.ProtocolObjectFactory;
import com.cubeia.games.poker.common.mongo.DatabaseStorageConfiguration;
import com.cubeia.games.poker.common.mongo.MongoStorage;
import com.cubeia.poker.handhistory.provider.api.HandHistoryProviderService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

public class HandHistoryProviderServiceImpl implements HandHistoryProviderService, Service, RoutableService {

    private static final Logger log = Logger.getLogger(HandHistoryProviderServiceImpl.class);
    public static final String HANDS_COLLECTION = "hands";

    private ServiceRouter router;
    private MongoStorage mongoStorage;
    private DatabaseStorageConfiguration configuration;

    @Override
    public String getHandHistory(int tableId, int playerId, long time) {
        BasicDBObject query = new BasicDBObject();
        query.put("table.tableId", tableId);
        query.put("seats", new BasicDBObject("$elemMatch", new BasicDBObject("playerId", playerId)));
        query.put("startTime", new BasicDBObject("$gte", time));

        DBCursor cursor = mongoStorage.findByQuery(query, HANDS_COLLECTION);
        String result = cursor.toArray().toString();
        cursor.close();
        return result;
    }

    @Override
    public void setRouter(ServiceRouter router) {
        this.router = router;
    }

    @Override
    public void onAction(ServiceAction e) {
        int tableId = -1;
        int playerId = e.getPlayerId();
        long time;
        byte[] data = e.getData();
        log.debug("Hand history requested for tableId: " + tableId);
        StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
        HandHistoryProviderRequest request = (HandHistoryProviderRequest)serializer.unpack(ByteBuffer.wrap(data));
        time = Long.parseLong(request.time);
        tableId = request.tableId;
        log.debug("Request data - TableId: " + tableId + " PlayerId: " + playerId + " Time: " + time);
        ServiceAction action = new ClientServiceAction(playerId, -1, getHandHistory(tableId, playerId, time).getBytes());
        router.dispatchToPlayer(e.getPlayerId(), action);
    }

    @Override
    public void init(ServiceContext context) throws SystemException {
        log.debug("HandHistoryProviderService STARTED! ");
        configuration = getConfiguration(context);
        mongoStorage = getMongoStorage();
    }

    protected DatabaseStorageConfiguration getConfiguration(ServiceContext context) {
        return new DatabaseStorageConfiguration().load(context.getServerConfigDirectory().getAbsolutePath());
    }

    protected MongoStorage getMongoStorage() {
        return new MongoStorage(configuration);
    }

    @Override
    public void destroy() { }

    @Override
    public void start() {
        mongoStorage.connect();
    }

    @Override
    public void stop() {
        mongoStorage.disconnect();
    }
}
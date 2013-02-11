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
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.games.poker.handhistoryservice.io.protocol.*;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.common.mongo.DatabaseStorageConfiguration;
import com.cubeia.games.poker.common.mongo.MongoStorage;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.poker.handhistory.provider.api.HandHistoryProviderService;
import com.google.code.morphia.query.Query;
import com.google.gson.*;
import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.List;

public class HandHistoryProviderServiceImpl implements HandHistoryProviderService, Service, RoutableService {

    private static final Logger log = Logger.getLogger(HandHistoryProviderServiceImpl.class);
    public static final int MAX_HANDS = 500;
    public static final int MAX_HAND_IDS = 500;

    private ServiceRouter router;
    private MongoStorage mongoStorage;
    private DatabaseStorageConfiguration configuration;

    private enum PacketType
    {
        hand_ids,
        hand,
        hands,
        undefined
    }

    @Override
    public String getHandIds(int tableId, int playerId, int count, long time) {
        log.debug("GetHandIds request data - TableId: " + tableId + " PlayerId: " + playerId + " Count: " + count + " Time: " + time);
        String result = "[]";
        if (count > 0)
        {
            if (count > MAX_HAND_IDS)
            {
                count = MAX_HAND_IDS;
            }
//            db.hands.find({"table.tableId" : 12, "seats" : {$elemMatch : {"playerId" : 2}}}, {"id" : 1, "_id" : 0}).sort({"startTime" : -1}).limit( 6 );
//            BasicDBObject query = new BasicDBObject();
//            query.put("table.tableId", tableId);
//            query.put("seats", new BasicDBObject("$elemMatch", new BasicDBObject("playerId", playerId)));
//            BasicDBObject projectedFields = new BasicDBObject();
//            projectedFields.put("id", 1);
//            projectedFields.put("_id", 0);
//            DBCursor cursor = mongoStorage.findByQuery(query, HistoricHand.class, projectedFields);
//            result = cursor.sort(new BasicDBObject("startTime", -1)).limit(count).toArray().toString();
//            cursor.close();
            Query query = mongoStorage.createQuery(HistoricHand.class);
            query.field("table.tableId").equal(tableId);
            query.filter("seats elem", new BasicDBObject("playerId", playerId));
            query.retrievedFields(true, "id");
            result = convertToJson(query.order("-startTime").limit(count).asKeyList());
        }
        else
        {
//            BasicDBObject query = new BasicDBObject();
//            query.put("table.tableId", tableId);
//            query.put("seats", new BasicDBObject("$elemMatch", new BasicDBObject("playerId", playerId)));
//            query.put("startTime", new BasicDBObject("$gte", time));
//            BasicDBObject projectedFields = new BasicDBObject();
//            projectedFields.put("id", 1);
//            projectedFields.put("_id", 0);
//            DBCursor cursor = mongoStorage.findByQuery(query, HistoricHand.class, projectedFields).limit(MAX_HAND_IDS);
//            result = cursor.toArray().toString();
//            cursor.close();
            Query query = mongoStorage.createQuery(HistoricHand.class);
            query.field("table.tableId").equal(tableId);
            query.field("startTime").greaterThanOrEq(time);
            query.filter("seats elem", new BasicDBObject("playerId", playerId));
            query.retrievedFields(true, "id").retrievedFields(false, "_id");
            result = convertToJson(query.order("-startTime").limit(MAX_HAND_IDS).asKeyList());
        }
        return result;
    }

    @Override
    public String getHand(String handId, int playerId) {
        log.debug("GetHand request data - HandId: " + handId + " PlayerId: " + playerId);
//        BasicDBObject query = new BasicDBObject();
//        query.put("id", handId);
//        query.put("seats", new BasicDBObject("$elemMatch", new BasicDBObject("playerId", playerId)));
//        DBCursor cursor = mongoStorage.findByQuery(query, HistoricHand.class);
//        String result = cursor.toArray().toString();
//        cursor.close();
//        return result;
        Query query = mongoStorage.createQuery(HistoricHand.class);
        query.field("id").equal(handId);
        query.filter("seats elem", new BasicDBObject("playerId", playerId));
        return convertToJson(query.asList());
    }

    @Override
    public String getHands(int tableId, int playerId, int count, long time) {
        log.debug("GetHands request data - TableId: " + tableId + " PlayerId: " + playerId + " Count: " + count + " Time: " + time);
        String result = "[]";
        if (count > 0)
        {
            if (count > MAX_HANDS)
            {
                count = MAX_HANDS;
            }
//            BasicDBObject query = new BasicDBObject();
//            query.put("table.tableId", tableId);
//            query.put("seats", new BasicDBObject("$elemMatch", new BasicDBObject("playerId", playerId)));
//            DBCursor cursor = mongoStorage.findByQuery(query, HistoricHand.class);
//            result = cursor.sort(new BasicDBObject("startTime", -1)).limit(count).toArray().toString();
//            cursor.close();
            Query query = mongoStorage.createQuery(HistoricHand.class);
            query.field("table.tableId").equal(tableId);
            query.filter("seats elem", new BasicDBObject("playerId", playerId));
            result = convertToJson(query.order("-startTime").limit(count).asList());
        }
        else
        {
//            BasicDBObject query = new BasicDBObject();
//            query.put("table.tableId", tableId);
//            query.put("seats", new BasicDBObject("$elemMatch", new BasicDBObject("playerId", playerId)));
//            query.put("startTime", new BasicDBObject("$gte", time));
//            DBCursor cursor = mongoStorage.findByQuery(query, HistoricHand.class).limit(MAX_HANDS);
//            result = cursor.toArray().toString();
//            cursor.close();
            Query query = mongoStorage.createQuery(HistoricHand.class);
            query.field("table.tableId").equal(tableId);
            query.field("startTime").greaterThanOrEq(time);
            query.filter("seats elem", new BasicDBObject("playerId", playerId));
            result = convertToJson(query.order("-startTime").limit(MAX_HANDS).asList());
        }
        return result;
    }

    @Override
    public void setRouter(ServiceRouter router) {
        this.router = router;
    }

    @Override
    public void onAction(ServiceAction e) {
        log.debug("Hand history requested.");
        StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
        ProtocolObject protocolObject = serializer.unpack(ByteBuffer.wrap(e.getData()));

        PacketType responseType = PacketType.undefined;
        String value = "";

        if (protocolObject.getClass() == HandHistoryProviderRequestHand.class)
        {
            HandHistoryProviderRequestHand request = (HandHistoryProviderRequestHand)protocolObject;
            value =  getHand(request.handId, e.getPlayerId());
            responseType = PacketType.hand;
        } else if (protocolObject.getClass() == HandHistoryProviderRequestHands.class)
        {
            HandHistoryProviderRequestHands request = (HandHistoryProviderRequestHands)protocolObject;
            value =  getHands(request.tableId, e.getPlayerId(), request.count, getTime(request.time));
            responseType = PacketType.hands;
        } else if (protocolObject.getClass() == HandHistoryProviderRequestHandIds.class)
        {
            HandHistoryProviderRequestHandIds request = (HandHistoryProviderRequestHandIds)protocolObject;
            value =  getHandIds(request.tableId, e.getPlayerId(), request.count, getTime(request.time));
            responseType = PacketType.hand_ids;
        }

        String protocolValue = "{ \"packetType\" : \"" + responseType + "\" , \"value\" : " + value + " }";
        ServiceAction action = new ClientServiceAction(e.getPlayerId(), -1, protocolValue.getBytes());
        router.dispatchToPlayer(e.getPlayerId(), action);
    }

    private long getTime(String value)
    {
        long time = 0L;
        if (!(value == null || value.isEmpty()))
        {
            try
            {
                time = Long.parseLong(value);
            }
            catch (Throwable t) { }
        }
        return time;
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

    private String convertToJson(List hands) {
        Gson gson = createGson();
        return gson.toJson(hands);
    }

    private Gson createGson() {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapter(HandHistoryEvent.class, new HandHistorySerializer());
        //b.setPrettyPrinting();
        return b.create();
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

    private static class HandHistorySerializer implements JsonSerializer<HandHistoryEvent> {

        @Override
        public JsonElement serialize(HandHistoryEvent src, Type typeOfSrc, JsonSerializationContext context) {
            Class<? extends HandHistoryEvent> cl = src.getClass();
            return context.serialize(src, cl);
        }
    }
}
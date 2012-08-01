package com.cubeia.games.poker.admin.service.history;

import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;

@Service
public class HandHistoryServiceImpl implements HandHistoryService {

    private static final Logger log = Logger.getLogger(HandHistoryServiceImpl.class);

    private Mongo mongo;

    private String host = "localhost";

    private int port = 27017;

    public HandHistoryServiceImpl() {
        try {
            mongo = new Mongo(host, port);
        } catch (UnknownHostException e) {
            log.fatal("Could not connect to mongo on host " + host + " and port " + port);
        }
    }

    @Override
    public String findHandHistoryByPlayerId(int playerId) {
        DBCursor cursor = mongo.getDB("hands").getCollection("hands").find();
        String result = JSON.serialize(cursor);
        return result;
    }
}

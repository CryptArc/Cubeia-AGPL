package com.cubeia.games.poker.admin.service.history;

import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;

@Service
public class HandHistoryServiceImpl implements HandHistoryService {

    private static final Logger log = Logger.getLogger(HandHistoryServiceImpl.class);

    private Mongo mongo;

    private String host;

    private int port;

    @Autowired
    public HandHistoryServiceImpl(@Value("#{adminProperties.mongoHost}") String host, @Value("#{adminProperties.mongoPort}") int port) {
        this.host = host;
        this.port = port;
    }

    @PostConstruct
    public void init() {
        try {
            log.info("Connecting to Mongo on host " + host + " and port " + port);
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

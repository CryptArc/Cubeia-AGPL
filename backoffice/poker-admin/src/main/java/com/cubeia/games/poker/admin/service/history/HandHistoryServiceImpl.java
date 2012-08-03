package com.cubeia.games.poker.admin.service.history;

import com.cubeia.poker.handhistory.api.HistoricHand;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HandHistoryServiceImpl implements HandHistoryService {

    private static final Logger log = Logger.getLogger(HandHistoryServiceImpl.class);

//    private Mongo mongo;
//
//    private String host;
//
//    private int port;
    @Autowired
    MongoOperations operations;

    @Override
    public List<HistoricHand> findHandHistoryByPlayerId(int playerId) {
        System.out.println("Finding hands in mongo.");
        return operations.find(new BasicQuery("{seats.id : " + playerId + "}"), HistoricHand.class, "hands");
    }
}

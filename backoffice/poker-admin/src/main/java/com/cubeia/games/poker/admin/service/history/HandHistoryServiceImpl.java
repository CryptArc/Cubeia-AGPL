package com.cubeia.games.poker.admin.service.history;

import com.cubeia.poker.handhistory.api.HistoricHand;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class HandHistoryServiceImpl implements HandHistoryService {

    private static final Logger log = Logger.getLogger(HandHistoryServiceImpl.class);

    @Autowired
    MongoTemplate template;

    @Override
    public List<HistoricHand> findHandHistory(int playerId, Date fromDate, Date toDate) {
        log.info("Finding hand histories by query: playerId = " + playerId + " from: " + fromDate + " to: " + toDate);
        Query query = query(where("seats.id").is(playerId));
        if (fromDate != null) query.addCriteria(where("startTime").gt(fromDate.getTime()));
        if (toDate != null) query.addCriteria(where("startTime").lt(toDate.getTime()));
        return template.find(query, HistoricHand.class, "hands");
    }
}

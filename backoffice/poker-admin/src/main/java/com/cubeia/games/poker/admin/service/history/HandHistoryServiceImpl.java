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

package com.cubeia.games.poker.admin.service.history;

import com.cubeia.poker.handhistory.api.HistoricHand;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    public List<HistoricHand> findHandHistory(Integer playerId, Date fromDate, Date toDate) {
        log.info("Finding hand histories by query: playerId = " + playerId + " from: " + fromDate + " to: " + toDate);
        Query query = new Query();
        if (playerId != null) query.addCriteria(where("seats.playerId").is(playerId));
        if (fromDate != null) query.addCriteria(where("startTime").gt(fromDate.getTime()));
        if (toDate != null) query.addCriteria(where("startTime").lt(toDate.getTime()));
        return template.find(query, HistoricHand.class, "hands");
    }

    @Override
    public HistoricHand findById(String handId) {
        Query query = query(where("handId.handId").is(handId));
        return template.findOne(query, HistoricHand.class, "hands");
    }

}

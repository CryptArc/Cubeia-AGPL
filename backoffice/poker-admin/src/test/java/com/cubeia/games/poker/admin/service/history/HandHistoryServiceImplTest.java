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

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import com.sun.corba.se.impl.orbutil.HexOutputStream;
import org.bson.types.BasicBSONList;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.UnknownHostException;

import static junit.framework.Assert.assertTrue;

public class HandHistoryServiceImplTest {

//    private HandHistoryServiceImpl service;
//
//    private static MongodProcess mongod;
//
//    private static final int PORT = 12345;
//    private static final String HOST = "localhost";
//
//    @BeforeClass
//    public static void initDb() throws Exception {
//        MongodConfig config = new MongodConfig(Version.V2_0_1, PORT, Network.localhostIsIPv6());
//        MongodExecutable prepared = MongoDBRuntime.getDefaultInstance().prepare(config);
//        mongod = prepared.start();
//    }
//
//    @AfterClass
//    public static void shutdownDb() {
//        if (mongod != null) mongod.stop();
//    }
//
//    @Before
//    public void setup() {
////        service = new HandHistoryServiceImpl(HOST, PORT);
////        service.init();
//    }
//
//    @Test
//    public void testFindHandHistoryByPlayerId() throws Exception {
//        addHandHistoryForPlayer();
//        String handHistory = service.findHandHistoryByPlayerId(1);
//        assertTrue(handHistory.length() > 0);
//    }
//
//    private void addHandHistoryForPlayer() throws UnknownHostException {
//        DB db = new Mongo(HOST, PORT).getDB("hands");
//        DBObject object = (DBObject) JSON.parse(getDummyData());
//        db.getCollection("hands").save(object);
//    }
//
//    private String getDummyData() {
//        return "{ \"handId\" : { \"tableId\": 13, \"tableIntegrationId\" : \"MOCK::13\", \"handId\" : \"1343748958838\" },\"startTime\" : 1343748958838, \"endTime\" : 1343749016362, \"results\" : { \"totalRake\" : 40, \"results\": { \"1\" : { \"playerId\" : 1, \"netWin\" : -2000, \"totalWin\" : 0, \"rake\" :20, \"totalBet\" : 2000 }, \"2\" : { \"playerId\" : 2, \"netWin\" : 1960,\"totalWin\" : 3960, \"rake\" : 20, \"totalBet\" : 2000 } } }, \"events\" : [{\"action\" : \"SMALL_BLIND\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 100},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" :1343748958869},{\"action\" : \"BIG_BLIND\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748958876},{\"playerId\" : 1,\"cards\" : [{\"suit\" : \"CLUBS\",\"rank\" : \"THREE\"},{\"suit\" : \"SPADES\",\"rank\" : \"TWO\"}],\"isExposed\" : false,\"type\" : \"PlayerCardsDealt\",\"time\" : 1343748958878},{\"playerId\" : 2,\"cards\" : [{\"suit\" : \"HEARTS\",\"rank\" : \"QUEEN\"},{\"suit\" : \"SPADES\",\"rank\" : \"JACK\"}],\"isExposed\" : false,\"type\" : \"PlayerCardsDealt\",\"time\" : 1343748958880},{\"action\" : \"RAISE\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 400},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343748962248},{\"action\" : \"CALL\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748965310},{\"pots\" : [{\"potId\" : 0,\"players\" : [1,2],\"potSize\" : 800}],\"type\" : \"PotUpdate\",\"time\" : 1343748965312},{\"cards\" : [{\"suit\" : \"DIAMONDS\",\"rank\" : \"JACK\"},{\"suit\" : \"HEARTS\",\"rank\" : \"JACK\"},{\"suit\" : \"DIAMONDS\",\"rank\" : \"SEVEN\"}],\"type\" : \"TableCardsDealt\",\"time\" : 1343748965316},{\"action\" : \"CHECK\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 0},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748972500},{\"action\" : \"BET\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343748976754},{\"action\" : \"CALL\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748980358},{\"pots\" : [{\"potId\" : 0,\"players\" : [1,2],\"potSize\" : 1200}],\"type\" : \"PotUpdate\",\"time\" : 1343748980360},{\"cards\" : [{\"suit\" : \"SPADES\",\"rank\" : \"SEVEN\"}],\"type\" : \"TableCardsDealt\",\"time\" : 1343748980364},{\"action\" : \"CHECK\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 0},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748986620},{\"action\" : \"BET\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343748990732},{\"action\" : \"CALL\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748994921},{\"pots\" : [{\"potId\" : 0,\"players\" : [1,2],\"potSize\" : 1600}],\"type\" : \"PotUpdate\",\"time\" : 1343748994923},{\"cards\" : [{\"suit\" : \"CLUBS\",\"rank\" : \"EIGHT\"}],\"type\" : \"TableCardsDealt\",\"time\" : 1343748994925},{\"action\" : \"CHECK\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 0},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343749001468},{\"action\" : \"BET\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 400},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343749004244},{\"action\" : \"RAISE\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 800},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343749009196},{\"action\" : \"RAISE\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 1200},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343749012325},{\"action\" : \"CALL\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 400},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343749016343},{\"pots\" : [{\"potId\" : 0,\"players\" : [1,2],\"potSize\" : 4000}],\"type\" : \"PotUpdate\",\"time\" : 1343749016345},{\"playerId\" : 1,\"cards\" : [{\"suit\" : \"CLUBS\",\"rank\" : \"THREE\"},{\"suit\" : \"SPADES\",\"rank\" : \"TWO\"}],\"type\" : \"PlayerCardsExposed\",\"time\" : 1343749016347},{\"playerId\" : 2,\"cards\" : [{\"suit\" : \"SPADES\",\"rank\" : \"JACK\"},{\"suit\" : \"HEARTS\",\"rank\" : \"QUEEN\"}],\"type\" : \"PlayerCardsExposed\",\"time\" : 1343749016347}], \"seats\" : [{\"id\" : 1,\"initialBalance\" : 11372,\"seatId\" : 0,\"name\" : \"1\"},{\"id\" : 2,\"initialBalance\" : 8600,\"seatId\" : 1,\"name\" : \"2\"}] }";
//    }
}

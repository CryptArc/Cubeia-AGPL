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

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.games.poker.common.mongo.DatabaseStorageConfiguration;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.Table;
import com.cubeia.poker.handhistory.impl.JsonHandHistoryLogger;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.runtime.Network;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmbeddedMongoHandHistoryProviderServiceTest {

    private static final Logger log = Logger.getLogger(EmbeddedMongoHandHistoryProviderServiceTest.class);

    private static int PORT = 12345;

    private static String HOST = "localhost";

    private static MongodProcess mongoProcess;

    private HandHistoryProviderServiceImpl service;

    private JsonHandHistoryLogger jsonLogger = new JsonHandHistoryLogger();

    @Mock
    private DatabaseStorageConfiguration configuration;

    @Before
    public void setup() throws SystemException {
        initMocks(this);
        when(configuration.load(anyString())).thenReturn(configuration);
        when(configuration.getHost()).thenReturn(HOST);
        when(configuration.getPort()).thenReturn(PORT);
        when(configuration.getDatabaseName()).thenReturn("poker");

        service = new HandHistoryProviderServiceImpl() {
            @Override
            protected DatabaseStorageConfiguration getConfiguration(ServiceContext context) {
                return configuration;
            }
        };
        service.init(null);
        service.start();
    }

    @BeforeClass
    public static void initDb() throws Exception {
        MongodConfig config = new MongodConfig(Version.V2_1_1, PORT, Network.localhostIsIPv6());
        MongodExecutable prepared = MongoDBRuntime.getDefaultInstance().prepare(config);
        mongoProcess = prepared.start();
    }

    @AfterClass
    public static void shutdownDb() {
        if (mongoProcess != null) mongoProcess.stop();
    }

    @Test
    public void testFindHandHistory() throws Exception {
        createHandHistory(1, 1);
        String handHistory = service.getHandHistory(1, 1, 0);
        log.debug("Hand history: " + handHistory);
        assertThat(handHistory.length(), greaterThan(3));
    }

    private void createHandHistory(int tableId, int playerId) throws UnknownHostException {
        // Create a hand history.
        HistoricHand hand = new HistoricHand();
        Table table = new Table();
        table.setTableId(tableId);
        hand.setTable(table);
        Player player = new Player(playerId, 1, 400, "p1");
        hand.getSeats().add(player);

        // Store it.
        Mongo mongo = new Mongo(HOST, PORT);
        DB db = mongo.getDB("poker");
        DBObject dbObject = (DBObject) JSON.parse(jsonLogger.convertToJson(hand));
        DBCollection collection = db.getCollection("hands");
        collection.insert(dbObject);
    }
}

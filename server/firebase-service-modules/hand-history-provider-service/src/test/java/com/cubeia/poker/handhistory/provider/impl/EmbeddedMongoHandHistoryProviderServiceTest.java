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
import com.cubeia.games.poker.common.mongo.MongoStorage;
import com.cubeia.poker.handhistory.api.*;
import com.cubeia.poker.handhistory.impl.JsonHandHistoryLogger;
import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.runtime.Network;
import org.apache.log4j.Logger;
import org.junit.*;
import org.mockito.Mock;

import java.net.UnknownHostException;
import java.util.ArrayList;

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

    MongoStorage storage = null;

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
            @Override
            protected MongoStorage getMongoStorage() {
                storage = new MongoStorage(configuration);
                return storage;
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
    public void testGetHandIds() throws Exception {

        createHandHistory(1,"hand1", 1,2);
        createHandHistory(1,"hand2", 1,2);
        createHandHistory(1,"hand3",2,3);
        createHandHistory(2,"hand4",1,2);

        String handIds = service.getHandIds(1, 1, 10, 0);

        log.debug(handIds);

        Assert.assertTrue(handIds.contains("hand1"));
        Assert.assertTrue(handIds.contains("hand2"));
        Assert.assertFalse(handIds.contains("hand3"));
        Assert.assertFalse(handIds.contains("hand4"));
    }

    @Test
    public void testGetHand() throws Exception {

        createHandHistory(1,"hand1", 1,2);
        createHandHistory(1,"hand2", 1,2);
        createHandHistory(1,"hand3",2,3);
        createHandHistory(2,"hand4",1,2);

        String hand = service.getHand("hand1",1);
        log.debug(hand);
        Assert.assertTrue(hand.contains("hand1"));
        Assert.assertTrue(hand.contains("\"privateCards\":[]"));
        Assert.assertFalse(hand.contains("\"cards\":[]"));  //expose cards should contain something

    }

    @Test
    public void testGetHandsByCount() throws Exception {
        createHandHistory(1,"hand1",100L, 1,2);
        createHandHistory(1,"hand2",200L, 1,2);
        createHandHistory(1,"hand3",300L,2,3);
        createHandHistory(2,"hand4",400L,1,2);

        String hand = service.getHands(1,2,10,System.currentTimeMillis());
        log.debug(hand);
        Assert.assertTrue(hand.contains("hand1"));
        Assert.assertTrue(hand.contains("hand2"));

    }

    @Test
    public void testGetHandSummaries() throws Exception {
        createHandHistory(1,"hand1",100L, 1,2);
        createHandHistory(1,"hand2",200L, 1,2);
        createHandHistory(1,"hand3",300L,2,3);
        createHandHistory(2,"hand4",400L,1,2);

        String hand = service.getHandSummaries(1,2,10,System.currentTimeMillis());
        log.debug(hand);
        Assert.assertTrue(hand.contains("hand1"));
        Assert.assertTrue(hand.contains("hand2"));
        Assert.assertTrue(hand.contains("\"events\":[]"));
        Assert.assertTrue(hand.contains("\"seats\":[]"));
        Assert.assertTrue(hand.contains("\"results\":[]"));
    }

    @Test
    public void testGetHandsNoResults() throws Exception {
        createHandHistory(1,"hand1",100L, 1,2);
        createHandHistory(1,"hand2",200L, 1,2);

        String hands = service.getHands(1,3,10,System.currentTimeMillis());
        log.debug(hands);
        Assert.assertFalse(hands.contains("hand1"));
        Assert.assertFalse(hands.contains("hand2"));

    }
    @Test
    public void testGetHandsByTime() throws Exception {
        createHandHistory(1,"hand1",100L, 1,2);
        createHandHistory(1,"hand2",200L, 1,2);
        createHandHistory(1,"hand3",300L,2,3);
        createHandHistory(2,"hand4",400L,1,2);

        String hand = service.getHands(1,2,0,200L);
        log.debug(hand);
        Assert.assertFalse(hand.contains("hand1")); //hand one is not within time range
        Assert.assertTrue(hand.contains("hand2"));

    }

    private void createHandHistory(int tableId, String handId, int... playerIds) throws UnknownHostException {
        this.createHandHistory(tableId,handId,System.currentTimeMillis(),playerIds);
    }
    private void createHandHistory(int tableId, String handId,long startTime, int... playerIds) throws UnknownHostException {
        // Create a hand history.
        HistoricHand hand = new HistoricHand();
        hand.setStartTime(startTime);
        hand.setId(handId);
        Table table = new Table();
        table.setTableId(tableId);

        hand.setTable(table);
        hand.setEvents(new ArrayList<HandHistoryEvent>());

        for(int playerId : playerIds) {
            Player player = new Player(playerId, 1, 400, "p" +playerId);
            hand.getSeats().add(player);
            PlayerCardsDealt pcd = new PlayerCardsDealt();
            pcd.setPrivateCards(new ArrayList<GameCard>());
            pcd.getPrivateCards().add(new GameCard(GameCard.Suit.CLUBS, GameCard.Rank.ACE));
            pcd.setPlayerId(playerId);
            hand.getEvents().add(pcd);
            PlayerCardsExposed pce = new PlayerCardsExposed(playerId);
            pce.setCards(new ArrayList<GameCard>());
            pce.getCards().add(new GameCard(GameCard.Suit.CLUBS, GameCard.Rank.ACE));
            hand.getEvents().add(pce);
        }
        table.setSeats(hand.getSeats().size());
        storage.persist(hand);

    }
}

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

package com.cubeia.poker.handhistory.storage;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.games.poker.common.mongo.DatabaseStorageConfiguration;
import com.cubeia.poker.tournament.history.api.HistoricTournament;
import com.cubeia.poker.tournament.history.storage.impl.DatabaseStorageService;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.runtime.Network;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DatabaseStorageServiceTest {

    private static final Logger log = Logger.getLogger(DatabaseStorageServiceTest.class);

    private static int PORT = 12345;

    private static String HOST = "localhost";

    private static MongodProcess mongod;

    private DatabaseStorageService service;

    @Mock
    private DatabaseStorageConfiguration configuration;

    // @Before
    public void setup() throws SystemException {
        initMocks(this);
        when(configuration.load(anyString())).thenReturn(configuration);
        when(configuration.getHost()).thenReturn(HOST);
        when(configuration.getPort()).thenReturn(PORT);
        when(configuration.getDatabaseName()).thenReturn("poker");

        service = new DatabaseStorageService() {
            @Override
            protected DatabaseStorageConfiguration getConfiguration(ServiceContext context) {
                return configuration;
            }
        };
        service.init(null);
        service.start();
    }

    // @BeforeClass
    public static void initDb() throws Exception {
        MongodConfig config = new MongodConfig(Version.V2_1_1, PORT, Network.localhostIsIPv6());
        MongodExecutable prepared = MongoDBRuntime.getDefaultInstance().prepare(config);
        mongod = prepared.start();
    }

    // @AfterClass
    public static void shutdownDb() {
        if (mongod != null) mongod.stop();
    }

    // @Test
    public void testCreateHistoricTournament() throws Exception {
        String id = service.createHistoricTournament();
        log.info("Id = " + id);
        assertThat(id, notNullValue());
    }

    // @Test
    public void testFindHistoricTournament() throws Exception {
        String id = service.createHistoricTournament();

        HistoricTournament tournament = service.getHistoricTournament(id);
        assertThat(tournament, notNullValue());
    }

    // @Test
    public void testUpdateTournament() throws Exception {
        String id = service.createHistoricTournament();

        service.statusChanged("ANNOUNCED", id, new Date().getTime());
        service.statusChanged("REGISTERING", id, new Date().getTime());

        Mongo mongo = new Mongo(HOST, PORT);
        DB db = mongo.getDB("poker");
        DBCollection collection = db.getCollection("tournaments");
        log.info("COUNT: " + collection.count());

        DBCursor cursorDoc = collection.find();
        while (cursorDoc.hasNext()) {
            DBObject object = cursorDoc.next();
            log.debug("OBJECT: " + object);
        }

        HistoricTournament tournament = service.getHistoricTournament(id);
        assertThat(tournament.getEvents().size(), is(2));
    }

    //@Test
    public void testSetStartDate() throws Exception {
        String id = service.createHistoricTournament();
        service.setStartTime(id, new DateTime().getMillis());

        HistoricTournament tournament = service.getHistoricTournament(id);
        log.debug("Start date: " + new Date(tournament.getStartTime()));
        assertThat(tournament.getStartTime(), not(0L));
    }

}
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

package com.cubeia.games.poker.activator;

import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import static com.cubeia.poker.variant.PokerVariant.TEXAS_HOLDEM;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.activator.DefaultActivator;
import com.cubeia.firebase.api.game.activator.DefaultActivatorConfig;
import com.cubeia.firebase.api.game.activator.MttAwareActivator;
import com.cubeia.firebase.api.game.lobby.LobbyTable;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.games.poker.FirebaseState;
import com.cubeia.games.poker.tournament.activator.TournamentTableSettings;
import com.cubeia.poker.PokerGuiceModule;
import com.cubeia.poker.PokerSettings;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.timing.Timings;
import com.cubeia.poker.variant.PokerVariant;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Override the default game activator in order to provide my own 
 * specific implementations. 
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerActivator extends DefaultActivator implements MttAwareActivator, PokerActivatorMBean {

    private static final String JMX_BIND_NAME = "com.cubeia.poker:type=PokerActivator";
    
    private transient Logger log = Logger.getLogger(this.getClass());

    private RNGProvider rngProvider;

    private int multiplier = 1;
    
    /**
     * Holds all participants that should be used for creating tables.
     * I hold the implementation instead of the interface so that I can keep
     * bingo-specific data in the participants, i.e. FQN and special attributes.
     */
    private List<PokerParticipant> participants = new ArrayList<PokerParticipant>();

	private Injector injector;
	
    /**
     * Create the activator for Poker.
     * Set the Creation Participant as participant.
     *
     */
    public PokerActivator() {
        super();
        log.info("Created Game Activator for Poker");
    }

    @SuppressWarnings("serial")
    @Override
    public void init(ActivatorContext context) throws SystemException {
        super.init(context);
        
        // TODO: get RNG from service in provider
        final Random rng = new Random();
        rngProvider = new RNGProvider() {
            // TODO: This provider should get the rng from a service and store it in a transient field
            //   to avoid serializing it.
            
            @Override
            public Random getRNG() {
                return rng;
            }
        };
        
        initJmx();
        injector = Guice.createInjector(
        		new ActivatorGuiceModule(context),
        		new PokerGuiceModule());
        
        initParticipants(rngProvider);
    }
    
    @Override
    public void destroy() {
        super.destroy();
        destroyJmx();
    }
    
    /** 
     * Create a number of participants, i.e. lobby branches 
     */
    private void initParticipants(RNGProvider rngProvider) {	
    	participants.add(new PokerParticipant(10, "ITALIAN/cashgame/REAL_MONEY", 10, Timings.DEFAULT, TEXAS_HOLDEM, rngProvider));
    	participants.add(new PokerParticipant(4, "ITALIAN/cashgame/REAL_MONEY/4", 10, Timings.SLOW, TELESINA, rngProvider));
    	participants.add(new PokerParticipant(6, "ITALIAN/cashgame/REAL_MONEY/6", 10, Timings.SLOW, TELESINA, rngProvider));
    	
    	for (PokerParticipant part : participants) {
    		part.setInjector(injector);
    	}
    }

    /**
     * I will create a small set of different
     * seat-numbers which will go under different lobby paths.
     */
    @Override
    protected void initTables() {
        DefaultActivatorConfig configuration = getConfiguration();
        configuration.setIncrementSize(2);
        
        for (PokerParticipant part : participants) {
            // Get all tables for given FQN
            LobbyTable[] tables = tableRegistry.listTables(part.getLobbyPath());
            if(tables.length == 0) {
                incrementTables(configuration, part);
            }

        }
    }
    
    public void createTable(String domain, int seats, int level, PokerVariant variant) {
    	this.tableRegistry.createTable(seats, new PokerParticipant(seats, domain, level, Timings.DEFAULT, variant, rngProvider));
    }

    /** 
     * Check if we need to create tables or if empty tables should be removed. 
     * 
     * @see com.cubeia.firebase.api.game.activator.DefaultActivator#checkTables()
     */
    @Override
    protected void checkTables() {
        for (PokerParticipant part : participants) {
            LobbyTable[] tables = tableRegistry.listTables(part.getLobbyPath());
            List<LobbyTable> empty = findEmpty(tables);
            DefaultActivatorConfig config = getConfiguration();

            if(empty.size() < config.getMinAvailTables()) {
                incrementTables(config, part);
            } else {
                // checkTimeoutTables(tables.length, empty, config);
            }
        }
    }

    /**
     * Create a new batch of fresh tables.
     * The actual count is set trought the configuration.
     * 
     * @param config
     */
    private void incrementTables(DefaultActivatorConfig config, PokerParticipant participant) {
    	
    	log.debug("creating tables for participant: " + participant);
    	
        tableRegistry.createTables(config.getIncrementSize() * multiplier, participant.getSeats(), participant);
    }


    public void mttTableCreated(Table table, int mttId, Object commandAttachment, LobbyAttributeAccessor acc) {
        log.debug("Created poker tournament table: "+table.getId());

        TimingProfile timing = TimingFactory.getRegistry().getDefaultTimingProfile();
        if (commandAttachment instanceof TournamentTableSettings) {
            TournamentTableSettings settings = (TournamentTableSettings) commandAttachment;
            timing = settings.getTimingProfile();
        }

        log.debug("Created tournament table["+table.getId()+"] with timing profile: "+timing);

        PokerState pokerState = injector.getInstance(PokerState.class);
        pokerState.setId(table.getId());
        
        // TODO: must check with variant of poker this is
        PokerSettings settings = new PokerSettings(-1, timing, PokerVariant.TEXAS_HOLDEM, table.getPlayerSet().getSeatingMap().getNumberOfSeats());
        
        pokerState.init(rngProvider, settings);
        pokerState.setTournamentTable(true);
        pokerState.setTournamentId(mttId);
        pokerState.setAdapterState(new FirebaseState());
        table.getGameState().setState(pokerState);
        
    }

    public void mttTableCreated(Table table, int mttId, LobbyAttributeAccessor acc) {
        mttTableCreated(table, mttId, null, acc);
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
    
    public void destroyTable(int id) {
        tableRegistry.destroyTable(id, true);
    }
    
    

    /*------------------------------------------------

    JMX INITIALIZATION & DESTRUCTION

 ------------------------------------------------*/

    private void initJmx() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName monitorName = new ObjectName(JMX_BIND_NAME);
            mbs.registerMBean(this, monitorName);
        } catch(Exception e) {
            log.error("failed to bind poker activator to JMX", e);
        }
    }


    private void destroyJmx() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName monitorName = new ObjectName(JMX_BIND_NAME);
            if(mbs.isRegistered(monitorName)) {
                mbs.unregisterMBean(monitorName);
            }
        } catch(Exception e) {
            log.error("failed to unbind poker activator to JMX", e);
        }
    }
}

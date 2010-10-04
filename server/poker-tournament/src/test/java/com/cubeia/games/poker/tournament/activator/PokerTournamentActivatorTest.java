package com.cubeia.games.poker.tournament.activator;

import com.cubeia.firebase.api.mtt.activator.ActivatorContext;
import com.cubeia.games.poker.tournament.MockMttActivatorContext;

import junit.framework.TestCase;

public class PokerTournamentActivatorTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    
    public void testCreate() throws Exception {
        PokerTournamentActivatorImpl activator = new PokerTournamentActivatorImpl();
        ActivatorContext context = new MockMttActivatorContext();
        activator.init(context);
        
        
    }
}

package com.cubeia.poker.gametypes;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.rounds.AnteRound;


public class TelesinaRoundsTest {
    
    @Mock private PokerState state;
    
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    

    @Test
    @Ignore
    public void testVisitAnteRound() {
        fail("Not yet implemented");
        
        Telesina ts = new Telesina(state);
        
        AnteRound anteRound = mock(AnteRound.class);
        
        ts.visit(anteRound);
        
        
        
//        log.debug("visit ante round");
//        
//        if (anteRound.isCanceled()) {
//            handleCanceledHand();
//        } else {
//            moveChipsToPot();
//            reportPotUpdate();
//            
//            dealPocketCards();
//            dealExposedCards();
//            
//            prepareBettingRound();
//        }
        
    }

    /*
    @Test
    public void testVisitBettingRound() {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitBlindsRound() {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitDealCommunityCardsRound() {
        fail("Not yet implemented");
    }
    */

}

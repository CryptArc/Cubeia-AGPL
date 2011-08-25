package com.cubeia.poker.gametypes;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.GameType;
import com.cubeia.poker.IPokerState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.AnteRound;
import com.cubeia.poker.rounds.blinds.BlindsInfo;


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

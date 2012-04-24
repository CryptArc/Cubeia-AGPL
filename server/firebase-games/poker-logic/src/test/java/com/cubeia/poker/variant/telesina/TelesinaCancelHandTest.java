package com.cubeia.poker.variant.telesina;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;

public class TelesinaCancelHandTest {
    
    @Test
    public void testCancelHand() {
        PokerState state = mock(PokerState.class);
        Integer player1Id = 1222;
        Integer player2Id = 2333;
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        when(player1.getId()).thenReturn(player1Id);
        when(player1.getBetStack()).thenReturn(0L);
        
        PokerPlayer player2 = mock(PokerPlayer.class);
        when(player2.getId()).thenReturn(player2Id);
        when(player2.getBetStack()).thenReturn(100L);
        
        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
        playerMap.put(player1Id, player1);
        playerMap.put(player2Id, player2);
        when(state.getCurrentHandPlayerMap()).thenReturn(playerMap);
        
        SortedMap<Integer, PokerPlayer>  seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(state.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        
        Telesina telesina = new Telesina(null, state, null, null,null);
        telesina.handleCanceledHand();
        
        verify(state).notifyHandFinished(Mockito.any(HandResult.class), Mockito.eq(HandEndStatus.CANCELED_TOO_FEW_PLAYERS));
        verify(state,never()).notifyTakeBackUncalledBets(player1.getId(), 0L);
        verify(state).notifyTakeBackUncalledBets(player2.getId(), 100L);
//        verify(state).notifyPlayerBalance(player1Id);
//        verify(state).notifyPlayerBalance(player2Id);
        
        verify(state).notifyRakeInfo();
    }
}

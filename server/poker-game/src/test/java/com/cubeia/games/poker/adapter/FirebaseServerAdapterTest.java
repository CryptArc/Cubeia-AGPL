package com.cubeia.games.poker.adapter;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import se.jadestone.dicearena.game.poker.network.protocol.PotTransfers;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;

public class FirebaseServerAdapterTest {

    @Test
    public void testUpdatePots() {
        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        
        fsa.protocolFactory = mock(ProtocolFactory.class);
        fsa.state = mock(PokerState.class);
        
        when(fsa.state.getCurrentHandPlayerMap()).thenReturn(Collections.<Integer, PokerPlayer>emptyMap());
        
        Table table = mock(Table.class);
        fsa.table = table;
        when(table.getId()).thenReturn(1337);
        
        GameNotifier notifier = mock(GameNotifier.class);
        when(table.getNotifier()).thenReturn(notifier);
        
        Pot pot1 = mock(Pot.class);
        when(pot1.getId()).thenReturn(23);
        Collection<Pot> pots = asList(pot1);
        
        PokerPlayer player1 = mock(PokerPlayer.class);
        PotTransition pt1 = mock(PotTransition.class);
        when(pt1.getPot()).thenReturn(pot1);
        when(pt1.getPlayer()).thenReturn(player1);
        Collection<PotTransition> potTransitions = asList(pt1);
        
        GameDataAction potAction = mock(GameDataAction.class);
        when(fsa.protocolFactory.createGameAction(Mockito.any(PotTransfers.class), Mockito.anyInt(), Mockito.eq(1337))).thenReturn(potAction );
        
        fsa.updatePots(pots, potTransitions);
        verify(notifier).notifyAllPlayers(potAction);
    }

    @Ignore
    @Test
    public void validateAndUpdateBalances() {
        // TODO: implement!!!
    }

}

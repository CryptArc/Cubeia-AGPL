package com.cubeia.games.poker;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import se.jadestone.dicearena.game.poker.network.protocol.DealPublicCards;
import se.jadestone.dicearena.game.poker.network.protocol.GameCard;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;
import se.jadestone.dicearena.game.poker.network.protocol.RequestAction;
import se.jadestone.dicearena.game.poker.network.protocol.StartHandHistory;
import se.jadestone.dicearena.game.poker.network.protocol.StopHandHistory;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.JoinRequestAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.cache.ActionCache;

public class PokerTableListenerTest {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSendGameState() throws IOException {
        int tableId = 234;
        int playerId = 1337;
        PokerTableListener ptl = new PokerTableListener();
        ptl.actionCache = mock(ActionCache.class);
        List<GameAction> cachedActions = Arrays.<GameAction>asList(new JoinRequestAction(playerId, 1, 0, "snubbe"));
        when(ptl.actionCache.getPrivateAndPublicActions(tableId, playerId)).thenReturn(cachedActions);
        
        Table table = mock(Table.class);
        when(table.getId()).thenReturn(tableId);
        GameNotifier notifier = mock(GameNotifier.class);
        when(table.getNotifier()).thenReturn(notifier);
        
        ptl.sendGameState(table, playerId);

        ArgumentCaptor<Collection> actionsCaptor = ArgumentCaptor.forClass(Collection.class);
        
        verify(notifier).notifyPlayer(Mockito.eq(playerId), actionsCaptor.capture());

        Collection<GameAction> sentActions = actionsCaptor.getValue();
        assertThat(sentActions.size(), is(3));
        Iterator<GameAction> actionIter = sentActions.iterator();
        assertThat(extractProtocolObject((GameDataAction) actionIter.next()), instanceOf(StartHandHistory.class));
        assertThat(actionIter.next(), instanceOf(JoinRequestAction.class));
        assertThat(extractProtocolObject((GameDataAction) actionIter.next()), instanceOf(StopHandHistory.class));
    }
    
    private ProtocolObject extractProtocolObject(GameDataAction gda) throws IOException {
        StyxSerializer styx = new StyxSerializer(new ProtocolObjectFactory());
        return styx.unpack(gda.getData());
    }
    
    @Test
    public void testFilterRequestActions() throws IOException {
        PokerTableListener ptl = new PokerTableListener();
        StyxSerializer styx = new StyxSerializer(new ProtocolObjectFactory());
        
        List<GameAction> actions = new ArrayList<GameAction>();
        JoinRequestAction act0 = new JoinRequestAction(111, 1, 0, "snubbe");
        actions.add(act0);
        GameDataAction gda0 = new GameDataAction(333, 1);
        gda0.setData(styx.pack(new RequestAction()));
        actions.add(gda0);
        GameDataAction gda1 = new GameDataAction(222, 1);
        gda1.setData(styx.pack(new DealPublicCards(new ArrayList<GameCard>())));
        actions.add(gda1);

        assertThat(actions.size(), is(3));
        
        List<GameAction> filteredActions = ptl.filterRequestActions(actions);
        assertThat(filteredActions.size(), is(2));
        assertThat(filteredActions, is(Arrays.<GameAction>asList(act0, gda1)));
    }

}

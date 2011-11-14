package com.cubeia.games.poker;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import mock.UnmongofiableSet;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import com.cubeia.firebase.api.action.AbstractGameAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TablePlayerSet;
import com.cubeia.firebase.api.util.UnmodifiableSet;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;

public class TableCrashHandlerTest {

    @Mock private PokerState state;
    @Mock private ActionCache actionCache;
    @Mock private Table table;
    @Mock private LobbyTableAttributeAccessor attributeAccessor;
    @Mock private BackendPlayerSessionHandler backendPlayerSessionHandler;
    private TableCrashHandler tableCrashHandler;
    private int tableId = 1343;

    @Before
    public void setup() {
        initMocks(this);
        tableCrashHandler = new TableCrashHandler(state, actionCache, backendPlayerSessionHandler);
        when(table.getAttributeAccessor()).thenReturn(attributeAccessor);
        when(table.getId()).thenReturn(tableId);
    }
    
    @Ignore
    @Test
    public void testHandleCrashOnTable() {
        AbstractGameAction action = new GameObjectAction(tableId);
        
        TablePlayerSet tablePlayerSet = mock(TablePlayerSet.class);
        when(table.getPlayerSet()).thenReturn(tablePlayerSet);
        final GenericPlayer gp1 = mock(GenericPlayer.class);
        final GenericPlayer gp2 = mock(GenericPlayer.class);
        int player1Id = 1003;
        int player2Id = 4001;
        when(gp1.getPlayerId()).thenReturn(player1Id);
        when(gp2.getPlayerId()).thenReturn(player2Id);
        
        UnmodifiableSet<GenericPlayer> playerSet = new UnmongofiableSet<GenericPlayer>(asList(gp1, gp2));
        when(tablePlayerSet.getPlayers()).thenReturn(playerSet);
        
        PokerPlayer pokerPlayer1 = mock(PokerPlayer.class);
        PokerPlayer pokerPlayer2 = mock(PokerPlayer.class);
        when(state.getPokerPlayer(player1Id)).thenReturn(pokerPlayer1);
        when(state.getPokerPlayer(player2Id)).thenReturn(pokerPlayer2);
        
        tableCrashHandler.handleCrashOnTable(action, table, new RuntimeException("shit happens"));
        
        verify(attributeAccessor).setIntAttribute("VISIBLE_IN_LOBBY", 0);
        verify(state).shutdown();
        verify(tablePlayerSet).removePlayer(player1Id);
        verify(tablePlayerSet).removePlayer(player2Id);
    }

    
}

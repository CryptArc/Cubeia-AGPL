package com.cubeia.games.poker;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;

import mock.UnmongofiableSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cubeia.firebase.api.action.AbstractGameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TablePlayerSet;
import com.cubeia.firebase.api.util.UnmodifiableSet;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.lobby.PokerLobbyAttributes;
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
    
    @Test
    public void testHandleCrashOnTable() {
        AbstractGameAction action = new GameObjectAction(tableId);
        
        TablePlayerSet tablePlayerSet = mock(TablePlayerSet.class);
        when(table.getPlayerSet()).thenReturn(tablePlayerSet);
        GameNotifier gameNotifier = mock(GameNotifier.class);
        when(table.getNotifier()).thenReturn(gameNotifier);
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
        
        verify(attributeAccessor).setIntAttribute(PokerLobbyAttributes.VISIBLE_IN_LOBBY.name(), 0);
        verify(state).shutdown();
        verify(gameNotifier).notifyPlayer(Mockito.eq(player1Id), Mockito.any(GameDataAction.class));
        verify(gameNotifier).notifyPlayer(Mockito.eq(player2Id), Mockito.any(GameDataAction.class));
        verify(tablePlayerSet).removePlayer(player1Id);
        verify(tablePlayerSet).removePlayer(player2Id);
        verify(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer1);
        verify(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer2);
        verify(attributeAccessor).setAttribute(PokerLobbyAttributes.TABLE_READY_FOR_CLOSE.name(), new AttributeValue(1));
    }
    
    @Test
    public void testClosePlayerSessionsWontStopOnException() {
        PokerPlayer pokerPlayer1 = mock(PokerPlayer.class);
        PokerPlayer pokerPlayer2 = mock(PokerPlayer.class);
        doThrow(new RuntimeException("crash")).when(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer1);
        
        tableCrashHandler.closePlayerSessions(table, Arrays.asList(pokerPlayer1, pokerPlayer2));
        
        verify(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer2);
    }
    
}

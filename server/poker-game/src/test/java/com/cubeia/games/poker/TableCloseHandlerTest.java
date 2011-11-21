package com.cubeia.games.poker;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Arrays;

import mock.UnmongofiableSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import se.jadestone.dicearena.game.poker.network.protocol.Enums.ErrorCode;
import se.jadestone.dicearena.game.poker.network.protocol.ErrorPacket;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;

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
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.adapter.FirebaseServerAdapter;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.lobby.PokerLobbyAttributes;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;

public class TableCloseHandlerTest {

    private StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
    
    @Mock private PokerState state;
    @Mock private FirebaseState fbState;
    @Mock private ActionCache actionCache;
    @Mock private Table table;
    @Mock private LobbyTableAttributeAccessor attributeAccessor;
    @Mock private BackendPlayerSessionHandler backendPlayerSessionHandler;
    @Mock private FirebaseServerAdapter serverAdapter;
    private TableCloseHandler tableCrashHandler;
    private int tableId = 1343;

    @Before
    public void setup() {
        initMocks(this);
        tableCrashHandler = new TableCloseHandler(state, actionCache, backendPlayerSessionHandler, serverAdapter);
        when(table.getAttributeAccessor()).thenReturn(attributeAccessor);
        when(table.getId()).thenReturn(tableId);
        when(state.getAdapterState()).thenReturn(fbState);
    }
    
    @Test
    public void testCloseAborted() throws IOException {
        setupCloseTableScenario();
        tableCrashHandler.closeTable(table, false);
        verify(state, times(0)).shutdown();
    }
    
    @Test
    public void testCloseForced() throws IOException {
        setupCloseTableScenario();
        tableCrashHandler.closeTable(table, true);
        verify(state, times(1)).shutdown();
        ArgumentCaptor<GameDataAction> actionCaptor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(table.getNotifier(), times(2)).notifyPlayer(Mockito.anyInt(), actionCaptor.capture());
        GameDataAction errorMessageAction = actionCaptor.getValue();
        ErrorPacket errorPacket = (ErrorPacket) serializer.unpack(errorMessageAction.getData());
        assertThat(errorPacket.code, is(ErrorCode.TABLE_CLOSING));
        assertThat(errorPacket.referenceId, is(""));
    }
    
    @Test
    public void testClose() throws Exception {
		TablePlayerSet tablePlayerSet = mock(TablePlayerSet.class);
        when(table.getPlayerSet()).thenReturn(tablePlayerSet);
        GameNotifier gameNotifier = mock(GameNotifier.class);
        when(table.getNotifier()).thenReturn(gameNotifier);
        UnmodifiableSet<GenericPlayer> playerSet = new UnmongofiableSet<GenericPlayer>();
        when(tablePlayerSet.getPlayers()).thenReturn(playerSet);
        when(tablePlayerSet.getPlayerCount()).thenReturn(0);
        long handId = 4435L;
        when(serverAdapter.getIntegrationHandId()).thenReturn(handId);
        tableCrashHandler.closeTable(table, false);
        verify(state, times(1)).shutdown();
    }

	protected void setupCloseTableScenario() {
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
        when(tablePlayerSet.getPlayerCount()).thenReturn(2);
        PokerPlayer pokerPlayer1 = mock(PokerPlayer.class);
        PokerPlayer pokerPlayer2 = mock(PokerPlayer.class);
        when(state.getPokerPlayer(player1Id)).thenReturn(pokerPlayer1);
        when(state.getPokerPlayer(player2Id)).thenReturn(pokerPlayer2);
        long handId = 4435L;
        when(serverAdapter.getIntegrationHandId()).thenReturn(handId);
	}
    
    @Test
    public void testHandleCrashOnTable() throws IOException {
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
        long handId = 4435L;
        when(serverAdapter.getIntegrationHandId()).thenReturn(handId);
        
        tableCrashHandler.handleCrashOnTable(action, table, new RuntimeException("shit happens"));
        
        verify(attributeAccessor).setIntAttribute(PokerLobbyAttributes.VISIBLE_IN_LOBBY.name(), 0);
        verify(state).shutdown();
        
        verify(tablePlayerSet).removePlayer(player1Id);
        verify(tablePlayerSet).removePlayer(player2Id);
        verify(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer1, 0);
        verify(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer2, 0);
        verify(attributeAccessor).setAttribute(PokerLobbyAttributes.TABLE_READY_FOR_CLOSE.name(), new AttributeValue(1));
        verify(gameNotifier).notifyPlayer(Mockito.eq(player1Id), Mockito.any(GameDataAction.class));
        
        ArgumentCaptor<GameDataAction> actionCaptor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(gameNotifier).notifyPlayer(Mockito.eq(player2Id), actionCaptor.capture());
        GameDataAction errorMessageAction = actionCaptor.getValue();
        ErrorPacket errorPacket = (ErrorPacket) serializer.unpack(errorMessageAction.getData());
        assertThat(errorPacket.code, is(ErrorCode.UNSPECIFIED_ERROR));
        assertThat(errorPacket.referenceId, is("" + handId));
    }
    
    @Test
    public void testClosePlayerSessionsWontStopOnException() {
        PokerPlayer pokerPlayer1 = mock(PokerPlayer.class);
        PokerPlayer pokerPlayer2 = mock(PokerPlayer.class);
        doThrow(new RuntimeException("crash")).when(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer1, -1);
        
        tableCrashHandler.closePlayerSessions(table, Arrays.asList(pokerPlayer1, pokerPlayer2));
        
        verify(backendPlayerSessionHandler).endPlayerSessionInBackend(table, pokerPlayer2, 0);
    }
    
}

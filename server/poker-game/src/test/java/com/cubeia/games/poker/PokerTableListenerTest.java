package com.cubeia.games.poker;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableMetaData;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;

public class PokerTableListenerTest {
    
    @Test
    public void addPlayer() throws IOException {
        int tableId = 234;
        int playerId = 1337;
        PokerTableListener ptl = new PokerTableListener();
      
        ptl.state = mock(PokerState.class);
        ptl.gameStateSender = mock(GameStateSender.class);
        ptl.walletService = mock(WalletServiceContract.class);
        ptl.cashGameBackend = mock(CashGamesBackendContract.class);
        FirebaseCallbackFactory callbackFactory = mock(FirebaseCallbackFactory.class);
        when(ptl.cashGameBackend.getCallbackFactory()).thenReturn(callbackFactory);
        
        Table table = mock(Table.class);
        when(table.getId()).thenReturn(tableId);
        TableMetaData tableMetaData = mock(TableMetaData.class);
        when(table.getMetaData()).thenReturn(tableMetaData);
        GameNotifier gameNotifier = mock(GameNotifier.class);
        when(table.getNotifier()).thenReturn(gameNotifier);
        GenericPlayer player = new GenericPlayer(playerId, "plajah");
        int balance = 40000;
        when(ptl.state.getBalance(playerId)).thenReturn(balance);

        PokerPlayer pokerPlayer = ptl.addPlayer(table, player, false);
        
        assertThat(pokerPlayer.getId(), is(playerId));
        assertThat(((PokerPlayerImpl) pokerPlayer).getPlayerSessionId(), nullValue());
        verify(ptl.gameStateSender).sendGameState(table, playerId);
        verify(ptl.state).addPlayer(pokerPlayer);
        verify(ptl.cashGameBackend).openSession(Mockito.any(OpenSessionRequest.class), Mockito.any(OpenSessionCallback.class));
        verify(callbackFactory).createOpenSessionCallback(table);
        
        verify(gameNotifier, Mockito.never()).notifyAllPlayers(Mockito.any(GameAction.class));
        
        assertThat(pokerPlayer.isSittingOut(), is(true));
        assertThat(pokerPlayer.getSitOutStatus(), is(SitOutStatus.NOT_ENTERED_YET));
    }

}

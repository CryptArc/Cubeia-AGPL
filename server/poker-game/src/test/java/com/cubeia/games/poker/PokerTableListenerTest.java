package com.cubeia.games.poker;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import se.jadestone.dicearena.game.poker.network.protocol.PlayerBalance;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;

import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableMetaData;
import com.cubeia.firebase.io.StyxSerializer;
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
        ptl.backendService = mock(CashGamesBackendContract.class);
        
        Table table = mock(Table.class);
        when(table.getId()).thenReturn(tableId);
        TableMetaData tableMetaData = mock(TableMetaData.class);
        when(table.getMetaData()).thenReturn(tableMetaData);
        GameNotifier gameNotifier = mock(GameNotifier.class);
        when(table.getNotifier()).thenReturn(gameNotifier);
        GenericPlayer player = new GenericPlayer(playerId, "plajah");
        int balance = 40000;
        when(ptl.state.getBalance(playerId)).thenReturn(balance);

        Long sessionId = 5355104L;
//        when(ptl.walletService.startSession(PokerGame.CURRENCY_CODE, PokerGame.LICENSEE_ID, playerId, 
//            tableId, PokerGame.POKER_GAME_ID, player.getName())).thenReturn(sessionId);
        
        PokerPlayer pokerPlayer = ptl.addPlayer(table, player, false);
        
        assertThat(pokerPlayer.getId(), is(playerId));
        assertThat(((PokerPlayerImpl) pokerPlayer).getSessionId(), nullValue());
        verify(ptl.gameStateSender).sendGameState(table, playerId);
        verify(ptl.state).addPlayer(pokerPlayer);
//        verify(ptl.walletService).startSession(PokerGame.CURRENCY_CODE, PokerGame.LICENSEE_ID, playerId, 
//            tableId, PokerGame.POKER_GAME_ID, player.getName());
        verify(ptl.backendService).openSession(Mockito.anyInt(), Mockito.any(OpenSessionRequest.class));
        
//        ArgumentCaptor<GameDataAction> balanceActionCaptor = ArgumentCaptor.forClass(GameDataAction.class);
            
        verify(gameNotifier, Mockito.never()).notifyAllPlayers(Mockito.any(GameAction.class));
//        GameDataAction gda = balanceActionCaptor.getValue();
        
        StyxSerializer styx = new StyxSerializer(new ProtocolObjectFactory());
//        PlayerBalance balanceAction = (PlayerBalance) styx.unpack(gda.getData());
//        assertThat(balanceAction.player, is(playerId));
//        assertThat(balanceAction.balance, is(balance));
        assertThat(pokerPlayer.isSittingOut(), is(true));
        assertThat(pokerPlayer.getSitOutStatus(), is(SitOutStatus.NOT_ENTERED_YET));
    }

}

package com.cubeia.games.poker.adapter;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.firebase.api.game.player.PlayerStatus;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableScheduler;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.poker.PokerState;


public class DisconnectHandlerTest {

@Mock TimeoutCache timeoutCache;
	
	@Mock Table table;
	
	@Mock TableScheduler scheduler;
	
	@Mock PokerState state;
	
	@Mock FirebaseServerAdapter adapter;
	
	DisconnectHandler handler = new DisconnectHandler();
	
	/**
	 * Player id waiting to act: 1
	 * Table id: 66
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		handler.state = state;
		handler.adapter = adapter;
		
		when(table.getScheduler()).thenReturn(scheduler);
		when(table.getId()).thenReturn(66);
		
		when(state.isWaitingForPlayerToAct(1)).thenReturn(true);
	}
	
	@Test
	public void testWaitRejoin() {
		handler.checkDisconnectTime(table, 1, PlayerStatus.WAITING_REJOIN);
		//Mockito.verify(timeoutCache).removeTimeout(table.getId(), 1, table.getScheduler());
		Mockito.verify(adapter).notifyDisconnected(1);
	}
	
	@Test
	public void testWaitRejoinOtherPlayer() {
		handler.checkDisconnectTime(table, 2, PlayerStatus.WAITING_REJOIN);
		Mockito.verifyZeroInteractions(timeoutCache);
	}
	
	@Test
	public void testReconnect() {
		handler.checkDisconnectTime(table, 1, PlayerStatus.CONNECTED);
		
	}
}

package com.cubeia.games.poker.adapter;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import se.jadestone.dicearena.game.poker.network.protocol.BestHand;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoResponse;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerBalance;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfers;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandType;
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
		when(fsa.protocolFactory.createGameAction(Mockito.any(PotTransfers.class), Mockito.anyInt(), Mockito.eq(1337))).thenReturn(potAction);
//		RakeInfoContainer rakeInfoContainer = new RakeInfoContainer();
		
        fsa.notifyPotUpdates(pots, potTransitions);
		
        // TODO: check that rakeInfoContainer was sent to client
//		verify(notifier).notifyAllPlayers(potAction);
	}

	@Test
	public void testNotifyBuyInInfo() throws IOException {

		FirebaseServerAdapter fsa = new FirebaseServerAdapter();
		fsa.table = mock(Table.class);
		GameNotifier tableNotifier = mock(GameNotifier.class);
		when(fsa.table.getNotifier()).thenReturn(tableNotifier);

		PokerPlayer pokerPlayer = mock(PokerPlayer.class);
		int playerId = 1337;
		when(pokerPlayer.getId()).thenReturn(playerId);
		
		// fsa.protocolFactory = mock(ProtocolFactory.class);
		fsa.state = mock(PokerState.class);
		when(fsa.state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);
		

		int minBuyIn = 100;
		when(fsa.state.getMinBuyIn()).thenReturn(minBuyIn);

		int maxBuyIn = 45000;
		when(fsa.state.getMaxBuyIn()).thenReturn(maxBuyIn);

		int playerBalanceOnTable = 100;
		when(pokerPlayer.getBalance()).thenReturn((long) playerBalanceOnTable);

		fsa.notifyBuyInInfo(pokerPlayer.getId(), true);

		ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
		verify(tableNotifier).notifyPlayer(Mockito.eq(playerId), captor.capture());
		GameDataAction gda = captor.getValue();

		BuyInInfoResponse buyInInfoRespPacket = (BuyInInfoResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());
		assertThat(buyInInfoRespPacket.balanceInWallet, is(500000));
		assertThat(buyInInfoRespPacket.balanceOnTable, is(playerBalanceOnTable));
		assertThat(buyInInfoRespPacket.maxAmount, is(maxBuyIn - playerBalanceOnTable));
		assertThat(buyInInfoRespPacket.minAmount, is(minBuyIn));
		assertThat(buyInInfoRespPacket.mandatoryBuyin, is(true));

	}
	
	@Test
	public void testNotifyBestHand() throws IOException
	{
		FirebaseServerAdapter fsa = new FirebaseServerAdapter();
		fsa.table = mock(Table.class);
		GameNotifier tableNotifier = mock(GameNotifier.class);
		when(fsa.table.getNotifier()).thenReturn(tableNotifier);
		
		int playerId0 = 1337;
		PokerPlayer pokerPlayer0 = mock(PokerPlayer.class);
		when(pokerPlayer0.getId()).thenReturn(playerId0);
		
		int playerId1 = 666;
		PokerPlayer pokerPlayer1 = mock(PokerPlayer.class);
		when(pokerPlayer1.getId()).thenReturn(playerId1);
		
		int playerId2 = 6987;
		PokerPlayer pokerPlayer2 = mock(PokerPlayer.class);
		when(pokerPlayer2.getId()).thenReturn(playerId2);
		
		HandType handType = HandType.HIGH_CARD;
		
		Card pocketCard1 = new Card(0,"AS");
		
        Card pocketCard2 = new Card(1,"5C");
        
        // test if hand is not exposed
		fsa.notifyBestHand(playerId0, handType, asList(pocketCard1, pocketCard2), false);
		verify(tableNotifier, times(1)).notifyPlayer(Mockito.eq(playerId0), Mockito.any(GameDataAction.class));
		verify(tableNotifier, never()).notifyPlayer(Mockito.eq(playerId1), Mockito.any(GameDataAction.class));
		verify(tableNotifier, never()).notifyPlayer(Mockito.eq(playerId2), Mockito.any(GameDataAction.class));
		
		verify(tableNotifier, never()).notifyAllPlayers(Mockito.any(GameDataAction.class));
			
		// test if hand is exposed
		fsa.notifyBestHand(playerId0, handType, asList(pocketCard1, pocketCard2), true);
		verify(tableNotifier, times(1)).notifyPlayer(Mockito.eq(playerId0), Mockito.any(GameDataAction.class));
		verify(tableNotifier, never()).notifyPlayer(Mockito.eq(playerId1), Mockito.any(GameDataAction.class));
		verify(tableNotifier, never()).notifyPlayer(Mockito.eq(playerId2), Mockito.any(GameDataAction.class));
		
		verify(tableNotifier, times(1)).notifyAllPlayers(Mockito.any(GameDataAction.class));
		
	}
	
	@Test
	public void testNotifyPlayerBalance() throws IOException
	{
		FirebaseServerAdapter fsa = new FirebaseServerAdapter();
		fsa.table = mock(Table.class);
		GameNotifier tableNotifier = mock(GameNotifier.class);
		when(fsa.table.getNotifier()).thenReturn(tableNotifier);
		
		int playerId0 = 1337;
		PokerPlayer pokerPlayer0 = mock(PokerPlayer.class);
		when(pokerPlayer0.getId()).thenReturn(playerId0);
		when(pokerPlayer0.getBalance()).thenReturn(1000L);
		when(pokerPlayer0.getPendingBalance()).thenReturn(1001L);
		
		int playerId1 = 666;
		PokerPlayer pokerPlayer1 = mock(PokerPlayer.class);
		when(pokerPlayer1.getId()).thenReturn(playerId1);
		
		int playerId2 = 112358;
		PokerPlayer pokerPlayer2 = mock(PokerPlayer.class);
		when(pokerPlayer2.getId()).thenReturn(playerId2);
		
		fsa.state = mock(PokerState.class);
		when(fsa.state.getPokerPlayer(playerId0)).thenReturn(pokerPlayer0);
		when(fsa.state.getPokerPlayer(playerId1)).thenReturn(pokerPlayer1);
		when(fsa.state.getPokerPlayer(playerId2)).thenReturn(pokerPlayer2);
		
		fsa.notifyPlayerBalance(pokerPlayer0);
		
		// check that the public message is ok
		ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
		verify(tableNotifier).notifyAllPlayersExceptOne(captor.capture(),Mockito.eq(playerId0));
		GameDataAction gda = captor.getValue();
		PlayerBalance playerBalanceAction = (PlayerBalance) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());
		
		assertThat(playerBalanceAction.balance, is(1000));
		assertThat(playerBalanceAction.pendingBalance, is(0));
		assertThat(playerBalanceAction.player, is(playerId0));
		
		// check that the private message is ok
		captor = ArgumentCaptor.forClass(GameDataAction.class);
		verify(tableNotifier).notifyPlayer(Mockito.eq(playerId0),captor.capture());
		gda = captor.getValue();
		playerBalanceAction = (PlayerBalance) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());
		
		assertThat(playerBalanceAction.balance, is(1000));
		assertThat(playerBalanceAction.pendingBalance, is(1001));
		assertThat(playerBalanceAction.player, is(playerId0));
		
		
		
		//verify(tableNotifier).notifyAllPlayersExceptOne(arg0, arg1)
	}

	@Ignore
	@Test
	public void validateAndUpdateBalances() {
		// TODO: implement!!!
	}

}

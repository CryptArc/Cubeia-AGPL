/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.adapter;

import com.cubeia.backend.cashgame.LongTransactionId;
import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.TransactionUpdate;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableScheduler;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.PokerConfigServiceMock;
import com.cubeia.games.poker.common.Money;
import com.cubeia.games.poker.handler.ActionTransformer;
import com.cubeia.games.poker.handler.Trigger;
import com.cubeia.games.poker.handler.TriggerType;
import com.cubeia.games.poker.io.protocol.*;
import com.cubeia.games.poker.io.protocol.Enums.BuyInInfoResultCode;
import com.cubeia.games.poker.io.protocol.Enums.PlayerTableStatus;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.games.poker.state.FirebaseState;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.timing.TimingProfile;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.DECLINE_ENTRY_BET;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class FirebaseServerAdapterTest {

    @Test
    public void testUpdatePots() {
        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.handHistory = mock(HandHistoryReporter.class);
        fsa.actionTransformer = new ActionTransformer();

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

        fsa.notifyPotUpdates(pots, potTransitions);

    }

    @Test
    public void testNotifyBuyInInfo() throws IOException, GetBalanceFailedException {
        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.actionTransformer = new ActionTransformer();
        fsa.buyInCalculator = new BuyInCalculator();
        fsa.table = mock(Table.class);
        fsa.backend = mock(CashGamesBackendContract.class);
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

        long playerBalanceOnTable = 100;
        long playerBalanceOnTableOutsideHand = 100;
        long playerRequestedBalance = 50;
        long playerTotalBalanceOnTable = playerBalanceOnTable + playerBalanceOnTableOutsideHand + playerRequestedBalance;

        when(pokerPlayer.getBalance()).thenReturn((long) playerBalanceOnTable);
        when(pokerPlayer.getPendingBalanceSum()).thenReturn(playerBalanceOnTableOutsideHand + playerRequestedBalance);
        long mainAccountBalance = 500000L;
        when(fsa.backend.getMainAccountBalance(playerId)).thenReturn(new Money(mainAccountBalance, "EUR", 2));

        fsa.notifyBuyInInfo(pokerPlayer.getId(), true);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyPlayer(Mockito.eq(playerId), captor.capture());
        GameDataAction gda = captor.getValue();

        BuyInInfoResponse buyInInfoRespPacket = (BuyInInfoResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());
        assertThat(buyInInfoRespPacket.balanceInWallet, is((int) mainAccountBalance));

        assertThat(buyInInfoRespPacket.balanceOnTable, is((int) playerTotalBalanceOnTable));
        assertThat(buyInInfoRespPacket.minAmount, is(0));
        assertThat(buyInInfoRespPacket.maxAmount, is((int) (maxBuyIn - playerTotalBalanceOnTable)));
        assertThat(buyInInfoRespPacket.mandatoryBuyin, is(true));
        assertThat(buyInInfoRespPacket.resultCode, is(BuyInInfoResultCode.OK));

    }

    @Test
    public void testNotifyBuyInInfoBalanceToHigh() throws IOException, GetBalanceFailedException {

        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.table = mock(Table.class);
        fsa.backend = mock(CashGamesBackendContract.class);
        fsa.buyInCalculator = new BuyInCalculator();
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

        int maxBuyIn = 150;
        when(fsa.state.getMaxBuyIn()).thenReturn(maxBuyIn);

        long playerBalanceOnTable = 100;
        long playerBalanceOnTableOutsideHand = 100;
        long playerRequestedBalance = 100;
        long playerTotalBalanceOnTable = playerBalanceOnTable + playerBalanceOnTableOutsideHand + playerRequestedBalance;

        when(pokerPlayer.getBalance()).thenReturn((long) playerBalanceOnTable);
        when(pokerPlayer.getPendingBalanceSum()).thenReturn(playerBalanceOnTableOutsideHand + playerRequestedBalance);
        long mainAccountBalance = 500000L;
        when(fsa.backend.getMainAccountBalance(playerId)).thenReturn(new Money(mainAccountBalance, "EUR", 2));

        fsa.notifyBuyInInfo(pokerPlayer.getId(), true);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyPlayer(Mockito.eq(playerId), captor.capture());
        GameDataAction gda = captor.getValue();

        BuyInInfoResponse buyInInfoRespPacket = (BuyInInfoResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());
        assertThat(buyInInfoRespPacket.balanceInWallet, is((int) mainAccountBalance));

        assertThat(buyInInfoRespPacket.balanceOnTable, is((int) playerTotalBalanceOnTable));
        assertThat(buyInInfoRespPacket.maxAmount, is(0));
        assertThat(buyInInfoRespPacket.minAmount, is(0));
        assertThat(buyInInfoRespPacket.mandatoryBuyin, is(true));
        assertThat(buyInInfoRespPacket.resultCode, is(BuyInInfoResultCode.MAX_LIMIT_REACHED));
    }

    @Test
    public void testPerformPendingBuyIns() {

        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.configService = new PokerConfigServiceMock();
        fsa.table = mock(Table.class);
        fsa.backend = mock(CashGamesBackendContract.class);
        fsa.buyInCalculator = mock(BuyInCalculator.class);
        fsa.state = mock(PokerState.class);

        FirebaseState adapterState = mock(FirebaseState.class);
        when(fsa.state.getAdapterState()).thenReturn(adapterState);

        FirebaseCallbackFactory callbackFactory = mock(FirebaseCallbackFactory.class);
        when(fsa.backend.getCallbackFactory()).thenReturn(callbackFactory);
        ReserveCallback callback = mock(ReserveCallback.class);
        when(callbackFactory.createReserveCallback(fsa.table)).thenReturn(callback);

        PokerPlayer player1 = mock(PokerPlayerImpl.class);
        PokerPlayer player2 = mock(PokerPlayerImpl.class);
        PokerPlayer player3 = mock(PokerPlayerImpl.class);
        Collection<PokerPlayer> players = Arrays.asList(player1, player2, player3);
        when(player1.isBuyInRequestActive()).thenReturn(false);
        when(player1.getRequestedBuyInAmount()).thenReturn(5000L);
        when(player2.isBuyInRequestActive()).thenReturn(false);
        when(player2.getRequestedBuyInAmount()).thenReturn(25000L);
        when(player3.isBuyInRequestActive()).thenReturn(true);

        when(fsa.buyInCalculator.calculateAmountToReserve(Mockito.anyInt(), Mockito.anyInt(), Mockito.eq(5000))).thenReturn(2500);
        when(fsa.buyInCalculator.calculateAmountToReserve(Mockito.anyInt(), Mockito.anyInt(), Mockito.eq(25000))).thenReturn(0);

        fsa.performPendingBuyIns(players);

        verify(fsa.state, times(2)).getMaxBuyIn();
        verify(fsa.backend).reserve(Mockito.any(ReserveRequest.class), Mockito.eq(callback));
        verify(player1).buyInRequestActive();
        verify(player1).setRequestedBuyInAmount(2500L);
        verify(player2).clearRequestedBuyInAmountAndRequest();
    }

    @Test
    public void testNotifyBuyInInfoErrorGettingWalletBalance() throws IOException, GetBalanceFailedException {
        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.table = mock(Table.class);
        fsa.backend = mock(CashGamesBackendContract.class);
        fsa.buyInCalculator = new BuyInCalculator();
        GameNotifier tableNotifier = mock(GameNotifier.class);
        when(fsa.table.getNotifier()).thenReturn(tableNotifier);

        PokerPlayer pokerPlayer = mock(PokerPlayer.class);
        int playerId = 1337;
        when(pokerPlayer.getId()).thenReturn(playerId);

        fsa.state = mock(PokerState.class);
        when(fsa.state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);

        int minBuyIn = 100;
        when(fsa.state.getMinBuyIn()).thenReturn(minBuyIn);

        int maxBuyIn = 150;
        when(fsa.state.getMaxBuyIn()).thenReturn(maxBuyIn);

        int playerBalanceOnTable = 100;
        int playerPendingBalanceOnTable = 100;

        when(pokerPlayer.getBalance()).thenReturn((long) playerBalanceOnTable);
        when(pokerPlayer.getBalanceNotInHand()).thenReturn((long) playerPendingBalanceOnTable);
        when(fsa.backend.getMainAccountBalance(playerId)).thenThrow(new GetBalanceFailedException("error"));

        fsa.notifyBuyInInfo(pokerPlayer.getId(), true);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyPlayer(Mockito.eq(playerId), captor.capture());
        GameDataAction gda = captor.getValue();

        BuyInInfoResponse buyInInfoRespPacket = (BuyInInfoResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());
        assertThat(buyInInfoRespPacket.balanceInWallet, is(-1));
        assertThat(buyInInfoRespPacket.maxAmount, is(0));
        assertThat(buyInInfoRespPacket.minAmount, is(0));
        assertThat(buyInInfoRespPacket.resultCode, is(BuyInInfoResultCode.UNSPECIFIED_ERROR));
    }

    @Test
    public void testNotifyBuyInInfoMaxBuyinIsLessThanMinBuyIn() throws IOException, GetBalanceFailedException {

        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.table = mock(Table.class);
        fsa.backend = mock(CashGamesBackendContract.class);
        fsa.buyInCalculator = new BuyInCalculator();
        GameNotifier tableNotifier = mock(GameNotifier.class);
        when(fsa.table.getNotifier()).thenReturn(tableNotifier);

        PokerPlayer pokerPlayer = mock(PokerPlayer.class);
        int playerId = 1337;
        when(pokerPlayer.getId()).thenReturn(playerId);

        // fsa.protocolFactory = mock(ProtocolFactory.class);
        fsa.state = mock(PokerState.class);
        when(fsa.state.getPokerPlayer(playerId)).thenReturn(pokerPlayer);


        int minBuyIn = 200;
        when(fsa.state.getMinBuyIn()).thenReturn(minBuyIn);

        int maxBuyIn = 300;
        when(fsa.state.getMaxBuyIn()).thenReturn(maxBuyIn);

        int playerBalanceOnTable = 250;
        int playerPendingBalanceOnTable = 0;
        int playerTotalBalanceOnTable = playerBalanceOnTable + playerPendingBalanceOnTable;

        when(pokerPlayer.getBalance()).thenReturn((long) playerBalanceOnTable);
        when(pokerPlayer.getBalanceNotInHand()).thenReturn((long) playerPendingBalanceOnTable);
        long mainAccountBalance = 500000L;
        when(fsa.backend.getMainAccountBalance(playerId)).thenReturn(new Money(mainAccountBalance, "EUR", 2));

        fsa.notifyBuyInInfo(pokerPlayer.getId(), true);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyPlayer(Mockito.eq(playerId), captor.capture());
        GameDataAction gda = captor.getValue();

        BuyInInfoResponse buyInInfoRespPacket = (BuyInInfoResponse) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());
        assertThat(buyInInfoRespPacket.balanceInWallet, is((int) mainAccountBalance));
        assertThat(buyInInfoRespPacket.balanceOnTable, is(playerTotalBalanceOnTable));
        assertThat(buyInInfoRespPacket.maxAmount, is(50));
        assertThat(buyInInfoRespPacket.minAmount, is(0));
        assertThat(buyInInfoRespPacket.mandatoryBuyin, is(true));
        assertThat(buyInInfoRespPacket.resultCode, is(BuyInInfoResultCode.OK));

    }

    @Test(expected = IllegalStateException.class)
    public void testValidateAndUpdateBalancesThrowsErrorIfUserNotFound() {

        FirebaseServerAdapter serverAdapter = new FirebaseServerAdapter();
        serverAdapter.state = mock(PokerState.class);

        BatchHandResponse batchHandResult = new BatchHandResponse();
        BalanceUpdate balanceUpdate = mock(BalanceUpdate.class);
        batchHandResult.addResultEntry(new TransactionUpdate(new LongTransactionId(-1), balanceUpdate));

        serverAdapter.validateAndUpdateBalances(batchHandResult);

    }

    @Test(expected = IllegalStateException.class)
    public void testValidateAndUpdateBalancesThrowsErrorIfBalanceDiff() {

        FirebaseServerAdapter serverAdapter = new FirebaseServerAdapter();
        serverAdapter.state = mock(PokerState.class);
        SortedMap<Integer, PokerPlayer> currentPlayers = new TreeMap<Integer, PokerPlayer>();

        PlayerSessionId playerSessionId = mock(PlayerSessionId.class);

        Integer playerId = 1337;
        PokerPlayerImpl player = mock(PokerPlayerImpl.class);
        when(player.getId()).thenReturn(playerId);
        Long playerBalance = 666L;
        when(player.getBalance()).thenReturn(playerBalance);
        when(player.getPlayerSessionId()).thenReturn(playerSessionId);

        // add player
        currentPlayers.put(playerId, player);

        when(serverAdapter.state.getCurrentHandPlayerMap()).thenReturn(currentPlayers);


        BatchHandResponse batchHandResult = new BatchHandResponse();

        Money backendBalance = new Money(100L, "EUR", 2);
        long balanceVersionNumber = 1L;
        BalanceUpdate balanceUpdate = new BalanceUpdate(playerSessionId, backendBalance, balanceVersionNumber);

        batchHandResult.addResultEntry(new TransactionUpdate(new LongTransactionId(-1), balanceUpdate));
        serverAdapter.validateAndUpdateBalances(batchHandResult);

    }


    @Test
    public void testNotifyPlayerStatusInHandSittingIn() throws IOException {
        FirebaseServerAdapter serverAdapter = new FirebaseServerAdapter();
        serverAdapter.table = mock(Table.class);
        serverAdapter.backend = mock(CashGamesBackendContract.class);
        serverAdapter.state = mock(PokerState.class);
        GameNotifier tableNotifier = mock(GameNotifier.class);
        when(serverAdapter.table.getNotifier()).thenReturn(tableNotifier);

        int playerId = 1337;

        boolean inCurrentHand = true;
        PokerPlayerStatus status = PokerPlayerStatus.SITIN;

        when(serverAdapter.state.isPlayerInHand(playerId)).thenReturn(inCurrentHand);
        serverAdapter.notifyPlayerStatusChanged(playerId, status, true);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyAllPlayers(captor.capture());
        GameDataAction gda = captor.getValue();
        PlayerPokerStatus playerPokerStatusPacket = (PlayerPokerStatus) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());

        // assertThat(playerPokerStatusPacket.inCurrentHand, is(true));
        assertThat(playerPokerStatusPacket.status, is(PlayerTableStatus.SITIN));
        assertThat(playerPokerStatusPacket.player, is(playerId));
        assertThat(playerPokerStatusPacket.inCurrentHand, is(true));

    }

    @Test
    public void testNotifyPlayerStatusInHandSittingOut() throws IOException {
        FirebaseServerAdapter serverAdapter = new FirebaseServerAdapter();
        serverAdapter.table = mock(Table.class);
        serverAdapter.backend = mock(CashGamesBackendContract.class);
        serverAdapter.state = mock(PokerState.class);
        GameNotifier tableNotifier = mock(GameNotifier.class);
        when(serverAdapter.table.getNotifier()).thenReturn(tableNotifier);

        int playerId = 1337;

        boolean inCurrentHand = true;
        PokerPlayerStatus status = PokerPlayerStatus.SITOUT;

        when(serverAdapter.state.isPlayerInHand(playerId)).thenReturn(inCurrentHand);
        serverAdapter.notifyPlayerStatusChanged(playerId, status, false);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyAllPlayers(captor.capture());
        GameDataAction gda = captor.getValue();
        PlayerPokerStatus playerPokerStatusPacket = (PlayerPokerStatus) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());

        // assertThat(playerPokerStatusPacket.inCurrentHand, is(true));
        assertThat(playerPokerStatusPacket.status, is(PlayerTableStatus.SITOUT));
        assertThat(playerPokerStatusPacket.player, is(playerId));
        assertThat(playerPokerStatusPacket.inCurrentHand, is(false));

    }

    @Test
    public void testNotifyPlayerStatusOutOfHandSittingIn() throws IOException {
        FirebaseServerAdapter serverAdapter = new FirebaseServerAdapter();
        serverAdapter.table = mock(Table.class);
        serverAdapter.backend = mock(CashGamesBackendContract.class);
        serverAdapter.state = mock(PokerState.class);
        GameNotifier tableNotifier = mock(GameNotifier.class);
        when(serverAdapter.table.getNotifier()).thenReturn(tableNotifier);

        int playerId = 1337;

        boolean inCurrentHand = false;
        PokerPlayerStatus status = PokerPlayerStatus.SITIN;

        when(serverAdapter.state.isPlayerInHand(playerId)).thenReturn(inCurrentHand);
        serverAdapter.notifyPlayerStatusChanged(playerId, status, inCurrentHand);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyAllPlayers(captor.capture());
        GameDataAction gda = captor.getValue();
        PlayerPokerStatus playerPokerStatusPacket = (PlayerPokerStatus) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());

        // assertThat(playerPokerStatusPacket.inCurrentHand, is(false));
        assertThat(playerPokerStatusPacket.status, is(PlayerTableStatus.SITIN));
        assertThat(playerPokerStatusPacket.player, is(playerId));

    }

    @Test
    public void testNotifyPlayerStatusOutOfHandSittingOut() throws IOException {
        FirebaseServerAdapter serverAdapter = new FirebaseServerAdapter();
        serverAdapter.table = mock(Table.class);
        serverAdapter.backend = mock(CashGamesBackendContract.class);
        serverAdapter.state = mock(PokerState.class);
        GameNotifier tableNotifier = mock(GameNotifier.class);
        when(serverAdapter.table.getNotifier()).thenReturn(tableNotifier);

        int playerId = 1337;

        boolean inCurrentHand = false;
        PokerPlayerStatus status = PokerPlayerStatus.SITOUT;

        when(serverAdapter.state.isPlayerInHand(playerId)).thenReturn(inCurrentHand);
        serverAdapter.notifyPlayerStatusChanged(playerId, status, inCurrentHand);

        ArgumentCaptor<GameDataAction> captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyAllPlayers(captor.capture());
        GameDataAction gda = captor.getValue();
        PlayerPokerStatus playerPokerStatusPacket = (PlayerPokerStatus) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());

        // assertThat(playerPokerStatusPacket.inCurrentHand, is(false));
        assertThat(playerPokerStatusPacket.status, is(PlayerTableStatus.SITOUT));
        assertThat(playerPokerStatusPacket.player, is(playerId));

    }

    @Test
    public void testNotifyBestHand() throws IOException {
        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.actionTransformer = new ActionTransformer();
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

        Card pocketCard1 = new Card(0, "AS");

        Card pocketCard2 = new Card(1, "5C");

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
    public void testNotifyPlayerBalance() throws IOException {
        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.actionTransformer = new ActionTransformer();
        fsa.table = mock(Table.class);
        GameNotifier tableNotifier = mock(GameNotifier.class);
        when(fsa.table.getNotifier()).thenReturn(tableNotifier);

        int playerId0 = 1337;
        PokerPlayer pokerPlayer0 = mock(PokerPlayer.class);
        when(pokerPlayer0.getId()).thenReturn(playerId0);
        when(pokerPlayer0.getBalance()).thenReturn(1000L);
//		when(pokerPlayer0.getBalanceNotInHand()).thenReturn(1001L);
        when(pokerPlayer0.getPendingBalanceSum()).thenReturn(1111L);

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
        verify(tableNotifier).notifyAllPlayersExceptOne(captor.capture(), Mockito.eq(playerId0));
        GameDataAction gda = captor.getValue();
        PlayerBalance playerBalanceAction = (PlayerBalance) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());

        assertThat(playerBalanceAction.balance, is(1000));
        assertThat(playerBalanceAction.pendingBalance, is(0));
        assertThat(playerBalanceAction.player, is(playerId0));

        // check that the private message is ok
        captor = ArgumentCaptor.forClass(GameDataAction.class);
        verify(tableNotifier).notifyPlayer(Mockito.eq(playerId0), captor.capture());
        gda = captor.getValue();
        playerBalanceAction = (PlayerBalance) new StyxSerializer(new ProtocolObjectFactory()).unpack(gda.getData());

        assertThat(playerBalanceAction.balance, is(1000));
        assertThat(playerBalanceAction.pendingBalance, is(1111));
        assertThat(playerBalanceAction.player, is(playerId0));
    }

    @Test
    public void testRequestAction() {
        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.timeoutCache = mock(TimeoutCache.class);
        fsa.protocolFactory = mock(ProtocolFactory.class);
        fsa.actionTransformer = mock(ActionTransformer.class);
        fsa.actionSequenceGenerator = mock(ActionSequenceGenerator.class);

        int sequence = 909;
        when(fsa.actionSequenceGenerator.next()).thenReturn(sequence);

        GameDataAction action = mock(GameDataAction.class);
        int playerId = 1337;
        int tableId = 55;

        fsa.state = mock(PokerState.class);
        FirebaseState firebaseState = mock(FirebaseState.class);
        when(fsa.state.getAdapterState()).thenReturn(firebaseState);
        TimingProfile timingProfile = mock(TimingProfile.class);
        when(fsa.state.getTimingProfile()).thenReturn(timingProfile);
        long latencyGracePeriod = 300L;
        when(timingProfile.getTime(Periods.LATENCY_GRACE_PERIOD)).thenReturn(latencyGracePeriod);

        fsa.table = mock(Table.class);
        when(fsa.table.getId()).thenReturn(tableId);
        GameNotifier notifier = mock(GameNotifier.class);
        when(fsa.table.getNotifier()).thenReturn(notifier);
        TableScheduler tableScheduler = mock(TableScheduler.class);
        when(fsa.table.getScheduler()).thenReturn(tableScheduler);
        UUID uuid = UUID.randomUUID();
        when(tableScheduler.scheduleAction(Mockito.any(GameAction.class), Mockito.anyLong())).thenReturn(uuid);

        ActionRequest actionRequest = createActionRequest(playerId);

        RequestAction actionRequestPacket = new RequestAction();
        actionRequestPacket.player = playerId;
        when(fsa.actionTransformer.transform(actionRequest, sequence)).thenReturn(actionRequestPacket);
        when(fsa.protocolFactory.createGameAction(actionRequestPacket, playerId, tableId)).thenReturn(action);

        fsa.requestAction(actionRequest);

        verify(fsa.timeoutCache).addTimeout(tableId, playerId, uuid);
        verify(notifier).notifyAllPlayers(action);
        verify(firebaseState).setCurrentRequestSequence(sequence);
        ArgumentCaptor<GameObjectAction> scheduledActionCaptor = ArgumentCaptor.forClass(GameObjectAction.class);
        verify(tableScheduler).scheduleAction(scheduledActionCaptor.capture(), Mockito.eq(latencyGracePeriod + actionRequest.getTimeToAct()));
        GameObjectAction scheduledAction = scheduledActionCaptor.getValue();
        Trigger trigger = (Trigger) scheduledAction.getAttachment();
        assertThat(trigger.getType(), is(TriggerType.PLAYER_TIMEOUT));
        assertThat(trigger.getSeq(), is(sequence));
    }

    @Test
    public void testRequestMultipleActionsGetsTheSameId() {
        FirebaseServerAdapter fsa = new FirebaseServerAdapter();
        fsa.protocolFactory = mock(ProtocolFactory.class);
        fsa.actionTransformer = mock(ActionTransformer.class);
        fsa.actionSequenceGenerator = mock(ActionSequenceGenerator.class);
        fsa.timeoutCache = mock(TimeoutCache.class);

        int sequence = 909;
        when(fsa.actionSequenceGenerator.next()).thenReturn(sequence);

        GameDataAction action1 = mock(GameDataAction.class);
        GameDataAction action2 = mock(GameDataAction.class);
        int player1Id = 1337;
        int player2Id = 3337;
        int tableId = 55;

        fsa.state = mock(PokerState.class);
        FirebaseState firebaseState = mock(FirebaseState.class);
        when(fsa.state.getAdapterState()).thenReturn(firebaseState);
        TimingProfile timingProfile = mock(TimingProfile.class);
        when(fsa.state.getTimingProfile()).thenReturn(timingProfile);
        when(timingProfile.getTime(Periods.LATENCY_GRACE_PERIOD)).thenReturn(23434L);

        fsa.table = mock(Table.class);
        when(fsa.table.getId()).thenReturn(tableId);
        GameNotifier notifier = mock(GameNotifier.class);
        when(fsa.table.getNotifier()).thenReturn(notifier);
        TableScheduler tableScheduler = mock(TableScheduler.class);
        when(fsa.table.getScheduler()).thenReturn(tableScheduler);
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        when(tableScheduler.scheduleAction(Mockito.any(GameAction.class), Mockito.anyLong())).thenReturn(uuid1, uuid2);

        ActionRequest actionRequest1 = createActionRequest(player1Id);
        ActionRequest actionRequest2 = createActionRequest(player2Id);

        RequestAction actionRequestPacket1 = new RequestAction();
        actionRequestPacket1.player = player1Id;
        RequestAction actionRequestPacket2 = new RequestAction();
        actionRequestPacket2.player = player2Id;

        when(fsa.actionTransformer.transform(actionRequest1, sequence)).thenReturn(actionRequestPacket1);
        when(fsa.protocolFactory.createGameAction(actionRequestPacket1, player1Id, tableId)).thenReturn(action1);
        when(fsa.actionTransformer.transform(actionRequest2, sequence)).thenReturn(actionRequestPacket2);
        when(fsa.protocolFactory.createGameAction(actionRequestPacket2, player2Id, tableId)).thenReturn(action2);

        fsa.requestMultipleActions(Arrays.asList(actionRequest1, actionRequest2));

        verify(fsa.timeoutCache).addTimeout(tableId, player1Id, uuid1);
        verify(fsa.timeoutCache).addTimeout(tableId, player2Id, uuid2);
        verify(notifier).notifyAllPlayers(action1);
        verify(notifier).notifyAllPlayers(action2);
        verify(firebaseState).setCurrentRequestSequence(sequence);

        ArgumentCaptor<GameObjectAction> scheduledActionCaptor = ArgumentCaptor.forClass(GameObjectAction.class);
        verify(tableScheduler, times(2)).scheduleAction(scheduledActionCaptor.capture(), Mockito.anyLong());
        GameObjectAction scheduledAction = scheduledActionCaptor.getAllValues().get(0);
        Trigger trigger = (Trigger) scheduledAction.getAttachment();
        assertThat(trigger.getPid(), is(player1Id));
        assertThat(trigger.getType(), is(TriggerType.PLAYER_TIMEOUT));
        assertThat(trigger.getSeq(), is(sequence));

        scheduledAction = scheduledActionCaptor.getAllValues().get(1);
        trigger = (Trigger) scheduledAction.getAttachment();
        assertThat(trigger.getPid(), is(player2Id));
        assertThat(trigger.getType(), is(TriggerType.PLAYER_TIMEOUT));
        assertThat(trigger.getSeq(), is(sequence));
    }

    private ActionRequest createActionRequest(int playerId) {
        ActionRequest actionRequest = new ActionRequest();
        actionRequest.setPlayerId(playerId);
        actionRequest.setTimeToAct(5000);
        actionRequest.enable(new PossibleAction(ANTE, 20, 20));
        actionRequest.enable(new PossibleAction(DECLINE_ENTRY_BET));
        return actionRequest;
    }


    @Ignore
    @Test
    public void validateAndUpdateBalances() {
        // TODO: implement!!!
    }

}

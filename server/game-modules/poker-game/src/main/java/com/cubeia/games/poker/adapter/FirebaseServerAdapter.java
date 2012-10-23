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

import static com.cubeia.firebase.api.game.player.PlayerStatus.DISCONNECTED;
import static com.cubeia.firebase.api.game.player.PlayerStatus.LEAVING;
import static com.cubeia.games.poker.handler.BackendCallHandler.EXT_PROP_KEY_TABLE_ID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.LongTransactionId;
import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.TransactionUpdate;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableType;
import com.cubeia.firebase.api.util.UnmodifiableSet;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.firebase.service.random.api.RandomService;
import com.cubeia.game.poker.config.api.PokerConfigurationService;
import com.cubeia.games.poker.adapter.BuyInCalculator.MinAndMaxBuyInResult;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.common.Money;
import com.cubeia.games.poker.entity.HandIdentifier;
import com.cubeia.games.poker.handler.ActionTransformer;
import com.cubeia.games.poker.handler.Trigger;
import com.cubeia.games.poker.handler.TriggerType;
import com.cubeia.games.poker.io.protocol.BestHand;
import com.cubeia.games.poker.io.protocol.BuyInInfoResponse;
import com.cubeia.games.poker.io.protocol.DealPrivateCards;
import com.cubeia.games.poker.io.protocol.DealPublicCards;
import com.cubeia.games.poker.io.protocol.DealerButton;
import com.cubeia.games.poker.io.protocol.DeckInfo;
import com.cubeia.games.poker.io.protocol.Enums;
import com.cubeia.games.poker.io.protocol.Enums.BuyInInfoResultCode;
import com.cubeia.games.poker.io.protocol.ExposePrivateCards;
import com.cubeia.games.poker.io.protocol.ExternalSessionInfoPacket;
import com.cubeia.games.poker.io.protocol.FuturePlayerAction;
import com.cubeia.games.poker.io.protocol.HandCanceled;
import com.cubeia.games.poker.io.protocol.HandEnd;
import com.cubeia.games.poker.io.protocol.InformFutureAllowedActions;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.io.protocol.PlayerDisconnectedPacket;
import com.cubeia.games.poker.io.protocol.PlayerHandStartStatus;
import com.cubeia.games.poker.io.protocol.PlayerPokerStatus;
import com.cubeia.games.poker.io.protocol.Pot;
import com.cubeia.games.poker.io.protocol.PotTransfer;
import com.cubeia.games.poker.io.protocol.PotTransfers;
import com.cubeia.games.poker.io.protocol.RakeInfo;
import com.cubeia.games.poker.io.protocol.RequestAction;
import com.cubeia.games.poker.io.protocol.StartNewHand;
import com.cubeia.games.poker.io.protocol.TakeBackUncalledBet;
import com.cubeia.games.poker.jmx.PokerStats;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.games.poker.state.FirebaseState;
import com.cubeia.games.poker.tournament.PokerTournamentRoundReport;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsLevel;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.SystemShutdownException;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.tournament.RoundReport;
import com.cubeia.poker.util.SitoutCalculator;
import com.cubeia.poker.util.ThreadLocalProfiler;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Firebase implementation of the poker logic's server adapter.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class FirebaseServerAdapter implements ServerAdapter {

    private static Logger log = LoggerFactory.getLogger(FirebaseServerAdapter.class);

    @Inject
    @VisibleForTesting
    ActionCache cache;

    @Inject
    @VisibleForTesting
    GameContext gameContext;

    @Service
    @VisibleForTesting
    CashGamesBackendContract backend;

    @Inject
    @VisibleForTesting
    Table table;

    @Inject
    @VisibleForTesting
    PokerState state;

    @Inject
    @VisibleForTesting
    ActionTransformer actionTransformer;

    @Inject
    @VisibleForTesting
    ActionSequenceGenerator actionSequenceGenerator;

    @Inject
    @VisibleForTesting
    TimeoutCache timeoutCache;

    @Inject
    @VisibleForTesting
    LobbyUpdater lobbyUpdater;

    @Inject
    @VisibleForTesting
    PlayerUnseater playerUnseater;

    @Inject
    @VisibleForTesting
    BuyInCalculator buyInCalculator;

    @VisibleForTesting
    ProtocolFactory protocolFactory = new ProtocolFactory();
    
    @Service
    @VisibleForTesting
    PokerConfigurationService configService;

    @Inject
    private HandResultBatchFactory handResultBatchFactory;
    
    @Inject
    @VisibleForTesting
    HandHistoryReporter handHistory;
    
    @Service
    @VisibleForTesting
    RandomService randomService;

    /*------------------------------------------------

         ADAPTER METHODS

         These methods are the adapter interface
         implementations

      ------------------------------------------------*/
    
    public java.util.Random getSystemRNG() {
    	return randomService.getSystemDefaultRandom();
    };

    @Override
    public void notifyNewHand() throws SystemShutdownException {

        if (backend.isSystemShuttingDown()) {
            /*
             * This will be caught by the processors.
             */
            throw new SystemShutdownException();
        }
        
        String handId = backend.generateHandId();
        HandIdentifier playedHand = new HandIdentifier();
        playedHand.setIntegrationId(handId);
        getFirebaseState().setCurrentHandIdentifier(playedHand);

        StartNewHand packet = new StartNewHand();
        packet.handId = handId;
        GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
        sendPublicPacket(action, -1);

        log.debug("Starting new hand with ID '" + handId + "'. FBPlayers: " + table.getPlayerSet().getPlayerCount() + ", PokerPlayers: " + state.getSeatedPlayers().size());
    
        handHistory.notifyNewHand();
    }

    /**
     * Notify about market references.
     * If any reference is null then it is replaced by a minus sign.
     *
     * @param playerId
     * @param externalTableReference        the tables reference
     * @param externalTableSessionReference the players table reference
     */
    @Override
    public void notifyExternalSessionReferenceInfo(int playerId, String externalTableReference, String externalTableSessionReference) {
        ExternalSessionInfoPacket packet = new ExternalSessionInfoPacket(externalTableReference, externalTableSessionReference);
        GameDataAction action = protocolFactory.createGameAction(packet, playerId, table.getId());
        log.debug("--> Send ExternalSessionInfoPacket[" + packet + "] to player: {}", playerId);
        sendPrivatePacket(playerId, action);
    }

    @Override
    public void notifyDealerButton(int seat) {
        DealerButton packet = new DealerButton();
        packet.seat = (byte) seat;
        GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
        log.debug("--> Send DealerButton[" + packet + "] to everyone");
        sendPublicPacket(action, -1);
    }

    @Override
    public void notifyNewRound() {
    	handHistory.notifyNewRound();
    }

    @Override
    public void requestAction(ActionRequest request) {
        checkNotNull(request);

        int sequenceNumber = actionSequenceGenerator.next();
        createAndSendActionRequest(request, sequenceNumber);
        setRequestSequence(sequenceNumber);

        // Schedule timeout inc latency grace period
        long latency = state.getTimingProfile().getTime(Periods.LATENCY_GRACE_PERIOD);
        schedulePlayerTimeout(request.getTimeToAct() + latency, request.getPlayerId(), sequenceNumber);
    }

    @Override
    public void requestMultipleActions(Collection<ActionRequest> requests) {
        checkNotNull(requests);
        checkArgument(!requests.isEmpty(), "request collection can't be empty");

        int sequenceNumber = actionSequenceGenerator.next();

        for (ActionRequest actionRequest : requests) {
            createAndSendActionRequest(actionRequest, sequenceNumber);
            long latency = state.getTimingProfile().getTime(Periods.LATENCY_GRACE_PERIOD);
            schedulePlayerTimeout(actionRequest.getTimeToAct() + latency, actionRequest.getPlayerId(), sequenceNumber);
        }

        setRequestSequence(sequenceNumber);
    }

    private void createAndSendActionRequest(ActionRequest request, int sequenceNumber) {
        RequestAction packet = actionTransformer.transform(request, sequenceNumber);
        GameDataAction action = protocolFactory.createGameAction(packet, request.getPlayerId(), table.getId());
        log.debug("--> Send RequestAction[" + packet + "] to everyone");
        sendPublicPacket(action, -1);
    }

    @Override
    public void scheduleTimeout(long millis) {
        GameObjectAction action = new GameObjectAction(table.getId());
        TriggerType type = TriggerType.TIMEOUT;
        Trigger timeout = new Trigger(type);
        timeout.setSeq(-1);
        action.setAttachment(timeout);
        table.getScheduler().scheduleAction(action, millis);
        setRequestSequence(-1);
    }

    @Override
    public void notifyActionPerformed(PokerAction pokerAction, PokerPlayer pokerPlayer) {
        PerformAction packet = actionTransformer.transform(pokerAction, pokerPlayer);
        GameDataAction action = protocolFactory.createGameAction(packet, pokerAction.getPlayerId(), table.getId());
        log.debug("--> Send PerformAction[" + packet + "] to everyone");
        sendPublicPacket(action, -1);
        handHistory.notifyActionPerformed(pokerAction, pokerPlayer);
    }

    @Override
    public void notifyFutureAllowedActions(PokerPlayer player, List<PokerActionType> optionList) {

        InformFutureAllowedActions packet = new InformFutureAllowedActions();

        List<FuturePlayerAction> options = new ArrayList<FuturePlayerAction>();

        for (PokerActionType actionType : optionList) {
            options.add(new FuturePlayerAction(actionTransformer.fromPokerActionTypeToProtocolActionType(actionType)));
        }

        packet.actions = options;

        GameDataAction action = protocolFactory.createGameAction(packet, player.getId(), table.getId());
        sendPrivatePacket(player.getId(), action);

    }


    @Override
    public void notifyCommunityCards(List<Card> cards) {
        DealPublicCards packet = actionTransformer.createPublicCardsPacket(cards);
        GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
        log.debug("--> Send DealPublicCards[" + packet + "] to everyone");
        sendPublicPacket(action, -1);
        handHistory.notifyCommunityCards(cards);
    }

    @Override
    public void notifyPrivateCards(int playerId, List<Card> cards) {
        // Send the cards to the owner with proper rank & suit information
        DealPrivateCards packet = actionTransformer.createPrivateCardsPacket(playerId, cards, false);
        GameDataAction action = protocolFactory.createGameAction(packet, playerId, table.getId());
        log.debug("--> Send DealPrivateCards[" + packet + "] to player[" + playerId + "]");
        sendPrivatePacket(playerId, action);

        // Send the cards as hidden to the other players
        DealPrivateCards hiddenCardsPacket = actionTransformer.createPrivateCardsPacket(playerId, cards, true);
        GameDataAction ntfyAction = protocolFactory.createGameAction(hiddenCardsPacket, playerId, table.getId());
        log.debug("--> Send DealPrivateCards(hidden)[" + hiddenCardsPacket + "] to everyone");
        sendPublicPacket(ntfyAction, playerId);
        
        handHistory.notifyPrivateCards(playerId, cards);
    }

    @Override
    public void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand, boolean publicHand) {
        if (cardsInHand == null) {
            log.error("cardsInHand is null, this will cause a NullPointerException if we try to notify the best hand. Ignoring. " +
                      "HandType: " + handType + " playerId " + playerId);
            return;
        }
        BestHand bestHandPacket = actionTransformer.createBestHandPacket(playerId, handType, cardsInHand);
        GameDataAction bestHandAction = protocolFactory.createGameAction(bestHandPacket, playerId, table.getId());
        log.debug("--> Send BestHandPacket[" + bestHandPacket + "] to player[" + playerId + "]");

        if (publicHand) {
            sendPublicPacket(bestHandAction, -1);
        } else {
            sendPrivatePacket(playerId, bestHandAction);
        }
    }

    @Override
    public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
        // Send the cards as public to the other players
        DealPrivateCards hiddenCardsPacket = actionTransformer.createPrivateCardsPacket(playerId, cards, false);
        GameDataAction ntfyAction = protocolFactory.createGameAction(hiddenCardsPacket, playerId, table.getId());
        log.debug("--> Send DealPrivateCards(exposed)[" + hiddenCardsPacket + "] to everyone");
        sendPublicPacket(ntfyAction, -1);
        handHistory.notifyPrivateExposedCards(playerId, cards);
    }

    @Override
    public void exposePrivateCards(ExposeCardsHolder holder) {
        ExposePrivateCards packet = actionTransformer.createExposeCardsPacket(holder);
        GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
        log.debug("--> Send ExposePrivateCards[" + packet + "] to everyone");
        sendPublicPacket(action, -1);
        handHistory.exposePrivateCards(holder);
    }

    @Override
    public void performPendingBuyIns(Collection<PokerPlayer> players) {
        for (PokerPlayer player : players) {
            if (!player.isBuyInRequestActive() && player.getRequestedBuyInAmount() > 0) {
                PokerPlayerImpl pokerPlayer = (PokerPlayerImpl) player;

                int playerBalanceIncludingPending = (int) (pokerPlayer.getBalance() + pokerPlayer.getBalanceNotInHand());
                int amountToBuyIn = buyInCalculator.calculateAmountToReserve(
                        state.getMaxBuyIn(), playerBalanceIncludingPending, (int) player.getRequestedBuyInAmount());

                if (amountToBuyIn > 0) {
                    log.debug("sending reserve request to backend: player id = {}, amount = {}, amount requested by player = {}",
                            new Object[]{player.getId(), amountToBuyIn, player.getRequestedBuyInAmount()});

                    ReserveCallback callback = backend.getCallbackFactory().createReserveCallback(table);
                    Money amountToBuyInMoney = configService.createSystemMoney(amountToBuyIn);
                    ReserveRequest reserveRequest = new ReserveRequest(
                            pokerPlayer.getPlayerSessionId(), getFirebaseState().getHandCount(), amountToBuyInMoney); 
                    player.setRequestedBuyInAmount(amountToBuyIn);
                    backend.reserve(reserveRequest, callback); 
                    player.buyInRequestActive();
                } else {
                    log.debug("wont reserve money, max reached: player id = {}, amount wanted = {}", player.getId(), player.getRequestedBuyInAmount());
                    player.clearRequestedBuyInAmountAndRequest();
                }
            }
        }
    }

    @Override
    public void notifyBuyInInfo(int playerId, boolean mandatoryBuyin) {
        try {
            PokerPlayer player = state.getPokerPlayer(playerId);
            BuyInInfoResponse resp = new BuyInInfoResponse();

            int playerBalance = player == null ? 0 : (int) (player.getBalance() + player.getPendingBalanceSum());
            resp.balanceOnTable = playerBalance;
            resp.mandatoryBuyin = mandatoryBuyin;

            try {
                resp.balanceInWallet = (int) backend.getMainAccountBalance(playerId).getAmount();
            } catch (GetBalanceFailedException e) {
                log.error("error getting balance", e);
                resp.resultCode = BuyInInfoResultCode.UNSPECIFIED_ERROR;
                resp.balanceInWallet = -1;
            }

            if (resp.resultCode != BuyInInfoResultCode.UNSPECIFIED_ERROR) {
                MinAndMaxBuyInResult buyInRange = buyInCalculator.calculateBuyInLimits(
                        state.getMinBuyIn(), state.getMaxBuyIn(), state.getAnteLevel(), playerBalance);
                resp.minAmount = buyInRange.getMinBuyIn();
                resp.maxAmount = buyInRange.getMaxBuyIn();
                resp.resultCode = buyInRange.isBuyInPossible() ? BuyInInfoResultCode.OK : BuyInInfoResultCode.MAX_LIMIT_REACHED;
            }

            log.debug("Sending buyin information to player[" + playerId + "]: " + resp);

            GameDataAction gda = new GameDataAction(playerId, table.getId());
            StyxSerializer styx = new StyxSerializer(null);
            try {
                gda.setData(styx.pack(resp));
            } catch (IOException e) {
                e.printStackTrace();
            }

            table.getNotifier().notifyPlayer(playerId, gda);
        } catch (Exception e) {
            log.error("Failed to create buy in info response for player[" + playerId + "], mandatory[" + mandatoryBuyin + "]", e);
        }
    }

    @Override
    public void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus, boolean tournamentTable) {
        ThreadLocalProfiler.add("FirebaseServerAdapter.notifyHandEnd.start");

        if (handEndStatus.equals(HandEndStatus.NORMAL) && handResult != null) {
            sendHandEndPacket(handResult);
            if (!tournamentTable) {
                performBackEndTransactions(handResult, handEndStatus);
            }
            updateHandEndStatistics();
        } else {
            log.info("The hand was cancelled on table: " + table.getId() + " - " + table.getMetaData().getName());
            cleanupPlayers(new SitoutCalculator());
            HandCanceled handCanceledPacket = new HandCanceled();
            GameDataAction action = protocolFactory.createGameAction(handCanceledPacket, -1, table.getId());
            log.debug("--> Send HandCanceled[" + handCanceledPacket + "] to everyone");
            sendPublicPacket(action, -1);
        }

        clearActionCache();
        ThreadLocalProfiler.add("FirebaseServerAdapter.notifyHandEnd.stop");
    }
   
    private Map<Integer, String> getTransactionIds(BatchHandResponse batchHandResult) {
    	Map<Integer, String> transIds = new HashMap<Integer, String>();
    	for (TransactionUpdate u : batchHandResult.getResultingBalances()) {
			long transactionId = ((LongTransactionId)u.getTransactionId()).getTransactionId();
			int userId = u.getBalance().getPlayerSessionId().getPlayerId();
			transIds.put(userId, String.valueOf(transactionId));
		}
    	return transIds;
	}

    private void updateHandEndStatistics() {
        PokerStats.getInstance().reportHandEnd();
        getFirebaseState().incrementHandCount();
    }

    private void sendHandEndPacket(HandResult handResult) {
        Collection<RatedPlayerHand> hands = handResult.getPlayerHands();
        List<PotTransfer> transfers = new ArrayList<PotTransfer>();
        PotTransfers potTransfers = new PotTransfers(false, transfers, null);

        for (PotTransition pt : handResult.getPotTransitions()) {
            log.debug("--> sending winner pot transfer to client: {}", pt);
            transfers.add(actionTransformer.createPotTransferPacket(pt));
        }
        HandEnd packet = actionTransformer.createHandEndPacket(hands, potTransfers, handResult.getPlayerRevealOrder());
        GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
        log.debug("--> Send HandEnd[" + packet + "] to everyone");
        log.debug("--> handResult.getPlayerRevealOrder: {}", handResult.getPlayerRevealOrder());
        sendPublicPacket(action, -1);
    }

    private void performBackEndTransactions(HandResult handResult, HandEndStatus handEndStatus) {
        String handId = getIntegrationHandId();
        TableId externalTableId = getIntegrationTableId();
        BatchHandResponse batchHandResult = batchHand(handResult, handId, externalTableId);
        validateAndUpdateBalances(batchHandResult);
        Map<Integer, String> transactionIds = getTransactionIds(batchHandResult);
        handHistory.notifyHandEnd(handResult, handEndStatus, transactionIds);
    }

    private BatchHandResponse batchHand(HandResult handResult, String handId, TableId externalTableId) {
        BatchHandRequest batchHandRequest = handResultBatchFactory.createAndValidateBatchHandRequest(handResult, handId, externalTableId);
        batchHandRequest.setStartTime(state.getStartTime());
        batchHandRequest.setEndTime(System.currentTimeMillis());
        return doBatchHandResult(batchHandRequest);
    }

    private BatchHandResponse doBatchHandResult(BatchHandRequest batchHandRequest) {
        BatchHandResponse batchHandResult;
        try {
            batchHandResult = backend.batchHand(batchHandRequest);
        } catch (BatchHandFailedException e) {
            throw new RuntimeException(e);
        }
        return batchHandResult;
    }

    public String getIntegrationHandId() {
        HandIdentifier id = getFirebaseState().getCurrentHandIdentifier();
        return (id == null ? null : id.getIntegrationId());
    }

    private TableId getIntegrationTableId() {
        return (TableId) state.getExternalTableProperties().get(EXT_PROP_KEY_TABLE_ID);
    }

    @VisibleForTesting
    protected void validateAndUpdateBalances(BatchHandResponse batchHandResult) {
        for (TransactionUpdate tup : batchHandResult.getResultingBalances()) {
            PokerPlayerImpl pokerPlayer = null;
            BalanceUpdate bup = tup.getBalance();
            for (PokerPlayer pp : state.getCurrentHandPlayerMap().values()) {
                if (((PokerPlayerImpl) pp).getPlayerSessionId().equals(bup.getPlayerSessionId())) {
                    pokerPlayer = (PokerPlayerImpl) pp;
                }
            }

            if (pokerPlayer == null) {
                throw new IllegalStateException("error updating balance: unable to find player with session = " + bup.getPlayerSessionId());
            } else {
                long gameBalance = pokerPlayer.getBalance() + pokerPlayer.getBalanceNotInHand();
                long backendBalance = bup.getBalance().getAmount();

                if (gameBalance != backendBalance) {
                    //log.error("backend balance: {} not equal to game balance: {}, will reset to backend value", backendBalance, gameBalance);
                    throw new IllegalStateException("backend balance: " + backendBalance + " not equal to game balance: " + gameBalance + ", will reset to backend value");
                }
            }
        }
    }

    private void clearActionCache() {
        if (cache != null) {
            cache.clear(table.getId());
        }
    }

    @Override
    public void notifyPlayerBalance(PokerPlayer player) {
        if (player == null) return;

        long playersTotalContributionToPot = state.getPlayersTotalContributionToPot(player);

        // first send private packet to the player
        GameDataAction publicAction = actionTransformer.createPlayerBalanceAction(
                (int) player.getBalance(), 0, (int) playersTotalContributionToPot, player.getId(), table.getId());
        sendPublicPacket(publicAction, player.getId());

        //	    // then send public packet to all the other players but exclude the pending balance
        GameDataAction privateAction = actionTransformer.createPlayerBalanceAction(
                (int) player.getBalance(), (int) player.getPendingBalanceSum(), (int) playersTotalContributionToPot, player.getId(), table.getId());
        log.debug("Send private PBA: " + privateAction);
        sendPrivatePacket(player.getId(), privateAction);
    }

    /**
     * Sends a poker tournament round report to the tournament as set in the table meta-data.
     *
     * @param report, poker-logic protocol object, not null.
     */
    public void reportTournamentRound(RoundReport report) {
        PokerStats.getInstance().reportHandEnd();

        // Map the report to a server specific round report
        BlindsLevel currentLevel = new BlindsLevel(report.getSmallBlindAmount(), report.getBigBlindAmount(), report.getAnteAmount());
        PokerTournamentRoundReport pokerReport = new PokerTournamentRoundReport(report.getBalanceMap(), currentLevel);
        MttRoundReportAction action = new MttRoundReportAction(table.getMetaData().getMttId(), table.getId());
        action.setAttachment(pokerReport);
        table.getTournamentNotifier().sendToTournament(action);
        clearActionCache();
    }


    public void notifyPotUpdates(Collection<com.cubeia.poker.pot.Pot> pots, Collection<PotTransition> potTransitions) {
    	boolean fromPlayerToPot = !potTransitions.isEmpty() && potTransitions.iterator().next().isFromPlayerToPot();
        List<Pot> clientPots = new ArrayList<Pot>();
        List<PotTransfer> transfers = new ArrayList<PotTransfer>();

        // notify return uncalled chips
        for (PotTransition potTransition : potTransitions) {
            if (potTransition.isFromBetStackToPlayer()) {
                log.debug("--> sending takeBackUncalledChips to client: {}", potTransition);
                notifyTakeBackUncalledBet(potTransition.getPlayer().getId(), (int) potTransition.getAmount());
            }
        }

        for (com.cubeia.poker.pot.Pot pot : pots) {
            clientPots.add(actionTransformer.createPotUpdatePacket(pot.getId(), pot.getPotSize()));
        }

        for (PotTransition potTransition : potTransitions) {
            if (!potTransition.isFromBetStackToPlayer()) {
                log.debug("--> sending pot update to client: {}", potTransition);
                transfers.add(actionTransformer.createPotTransferPacket(potTransition));
            }
        }

        // notify betstacks to pots
        PotTransfers potTransfers = new PotTransfers(fromPlayerToPot, transfers, clientPots);
        GameDataAction action = protocolFactory.createGameAction(potTransfers, 0, table.getId());
        sendPublicPacket(action, -1);

        handHistory.notifyPotUpdates(pots, potTransitions);
    }


    @Override
    public void notifyRakeInfo(RakeInfoContainer rakeInfoContainer) {
        log.debug("--> sending rake info to client: {}", rakeInfoContainer);
        RakeInfo rakeInfo = new RakeInfo((int) rakeInfoContainer.getTotalPot(), (int) rakeInfoContainer.getTotalRake());
        GameDataAction action = protocolFactory.createGameAction(rakeInfo, 0, table.getId());
        sendPublicPacket(action, -1);
    }

    @Override
    public void notifyTakeBackUncalledBet(int playerId, int amount) {
        log.debug("--> Taking back uncalled bet: {}", playerId, amount);
        ProtocolObject takeBackUncalledBet = new TakeBackUncalledBet(playerId, amount);
        GameDataAction action = protocolFactory.createGameAction(takeBackUncalledBet, playerId, table.getId());
        sendPublicPacket(action, -1);
    }

    @Override
    public void notifyHandStartPlayerStatus(int playerId, PokerPlayerStatus status) {
        log.debug("Notify hand start player status: " + playerId + " -> " + status);
        PlayerHandStartStatus packet = new PlayerHandStartStatus();
        packet.player = playerId;
        switch (status) {
            case SITIN:
                packet.status = Enums.PlayerTableStatus.SITIN;
                break;
            case SITOUT:
                packet.status = Enums.PlayerTableStatus.SITOUT;
                break;
        }
        GameDataAction action = protocolFactory.createGameAction(packet, playerId, table.getId());
        sendPublicPacket(action, -1);
    }

    @Override
    public void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status, boolean inCurrentHand) {
        log.debug("Notify player status changed: " + playerId + " -> " + status);
        PlayerPokerStatus packet = new PlayerPokerStatus();
        packet.player = playerId;
        switch (status) {
            case SITIN:
                packet.status = Enums.PlayerTableStatus.SITIN;
                break;
            case SITOUT:
                packet.status = Enums.PlayerTableStatus.SITOUT;
                break;
        }
        packet.inCurrentHand = inCurrentHand;
        GameDataAction action = protocolFactory.createGameAction(packet, playerId, table.getId());
        sendPublicPacket(action, -1);
    }

    /**
     * Schedule a player timeout trigger command.
     */
    public void schedulePlayerTimeout(long millis, int pid, int seq) {
        GameObjectAction action = new GameObjectAction(table.getId());
        TriggerType type = TriggerType.PLAYER_TIMEOUT;
        Trigger timeout = new Trigger(type, pid);
        timeout.setSeq(seq);
        action.setAttachment(timeout);
        UUID actionId = table.getScheduler().scheduleAction(action, millis);
        timeoutCache.addTimeout(table.getId(), pid, actionId);
    }

    /**
     * Remove all players in state LEAVING or DISCONNECTED
     */
    public void cleanupPlayers(SitoutCalculator sitoutCalculator) {
        if (table.getMetaData().getType().equals(TableType.NORMAL)) {
            // Check for disconnected and leaving players
            UnmodifiableSet<GenericPlayer> players = table.getPlayerSet().getPlayers();
            for (GenericPlayer p : players) {
                if (p.getStatus() == DISCONNECTED || p.getStatus() == LEAVING) {
                    log.debug("Player clean up - unseat leaving or disconnected player[" + p.getPlayerId() + "] from table[" + table.getId() + "]");
                    unseatPlayer(p.getPlayerId(), false);
                }
            }

            // Check sitting out players for time outs
            Collection<PokerPlayer> timeoutPlayers = sitoutCalculator.checkTimeoutPlayers(
                    state.getSeatedPlayers(), state.getSettings().getSitoutTimeLimitMilliseconds());
            for (PokerPlayer p : timeoutPlayers) {
                log.debug("Player clean up - unseat timed out sit-out player[" + p.getId() + "] from table[" + table.getId() + "]");
                unseatPlayer(p.getId(), true);
            }
        }

        lobbyUpdater.updateLobby((FirebaseState) state.getAdapterState(), table);
    }

    public void unseatPlayer(int playerId, boolean setAsWatcher) {
        PokerPlayer pokerPlayer = state.getPokerPlayer(playerId);
        boolean participatingInCurrentHand = state.getPlayerInCurrentHand(playerId) != null && state.isPlaying();

        if (!pokerPlayer.isBuyInRequestActive() && !participatingInCurrentHand) {
            playerUnseater.unseatPlayer(table, playerId, setAsWatcher);
        }
    }

    /**
     * This action will be cached and used for sending current state to
     * joining players.
     * <p/>
     * If skipPlayerId is -1 then no player will be skipped.
     *
     * @param action
     * @param skipPlayerId
     */
    private void sendPublicPacket(GameAction action, int skipPlayerId) {
        if (skipPlayerId < 0) {
            table.getNotifier().notifyAllPlayers(action);
        } else {
            table.getNotifier().notifyAllPlayersExceptOne(action, skipPlayerId);
        }
        // Add to state cache
        if (cache != null) {
            cache.addPublicActionWithExclusion(table.getId(), action, skipPlayerId);
        }
    }

    /**
     * Send private packet to player and cache it as private. The cached action
     * will be sent to the player when rejoining.
     *
     * @param playerId player id
     * @param action   action
     */
    private void sendPrivatePacket(int playerId, GameAction action) {
        table.getNotifier().notifyPlayer(playerId, action);

        if (cache != null) {
            cache.addPrivateAction(table.getId(), playerId, action);
        }
    }


    private FirebaseState getFirebaseState() {
        return (FirebaseState) state.getAdapterState();
    }

    private void setRequestSequence(int seq) {
        getFirebaseState().setCurrentRequestSequence(seq);
    }


    @Override
    public void notifyDeckInfo(int size, com.cubeia.poker.hand.Rank rankLow) {
        DeckInfo deckInfoPacket = new DeckInfo(size, actionTransformer.convertRankToProtocolEnum(rankLow));
        GameDataAction action = protocolFactory.createGameAction(deckInfoPacket, 0, table.getId());
        sendPublicPacket(action, -1);
        handHistory.notifyDeckInfo(size, rankLow);
    }

    @Override
    public void notifyDisconnected(int playerId) {
        timeoutCache.removeTimeout(table.getId(), playerId, table.getScheduler());

        long timeout = state.getTimingProfile().getTime(Periods.DISCONNECT_EXTRA_TIME);
        long latencyTimeout = timeout + state.getTimingProfile().getTime(Periods.LATENCY_GRACE_PERIOD);
        PlayerDisconnectedPacket packet = new PlayerDisconnectedPacket();
        packet.playerId = playerId;
        packet.timebank = (int) timeout;

        log.debug("Notify disconnect: {}", packet);
        GameDataAction action = protocolFactory.createGameAction(packet, playerId, table.getId());
        sendPublicPacket(action, -1);

        log.debug("Schedule new timeout for player in {} ms", latencyTimeout);
        schedulePlayerTimeout(latencyTimeout, playerId, getFirebaseState().getCurrentRequestSequence());
    }

}

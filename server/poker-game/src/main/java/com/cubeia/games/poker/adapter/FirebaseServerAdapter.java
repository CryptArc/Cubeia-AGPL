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

import static com.cubeia.games.poker.handler.BackendCallHandler.EXT_PROP_KEY_TABLE_ID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.BestHand;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoResponse;
import se.jadestone.dicearena.game.poker.network.protocol.DealPrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.DealPublicCards;
import se.jadestone.dicearena.game.poker.network.protocol.DealerButton;
import se.jadestone.dicearena.game.poker.network.protocol.DeckInfo;
import se.jadestone.dicearena.game.poker.network.protocol.Enums;
import se.jadestone.dicearena.game.poker.network.protocol.Enums.BuyInInfoResultCode;
import se.jadestone.dicearena.game.poker.network.protocol.ExposePrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.HandCanceled;
import se.jadestone.dicearena.game.poker.network.protocol.HandEnd;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerPokerStatus;
import se.jadestone.dicearena.game.poker.network.protocol.Pot;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfer;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfers;
import se.jadestone.dicearena.game.poker.network.protocol.RakeInfo;
import se.jadestone.dicearena.game.poker.network.protocol.RequestAction;
import se.jadestone.dicearena.game.poker.network.protocol.StartNewHand;

import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.LeaveAction;
import com.cubeia.firebase.api.action.WatchResponseAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.player.PlayerStatus;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableType;
import com.cubeia.firebase.api.util.UnmodifiableSet;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.firebase.io.protocol.Enums.WatchResponseStatus;
import com.cubeia.games.poker.FirebaseState;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.entity.HandIdentifier;
import com.cubeia.games.poker.handler.Trigger;
import com.cubeia.games.poker.handler.TriggerType;
import com.cubeia.games.poker.jmx.PokerStats;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.games.poker.tournament.PokerTournamentRoundReport;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.rake.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.sitout.SitoutCalculator;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.tournament.RoundReport;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Firebase implementation of the poker logic's server adapter.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class FirebaseServerAdapter implements ServerAdapter {

	private static Logger log = LoggerFactory.getLogger(FirebaseServerAdapter.class);

	@Inject @VisibleForTesting
	ActionCache cache;

	@Inject @VisibleForTesting
	GameContext gameContext;

	@Service @VisibleForTesting
	CashGamesBackendContract backend;

	@Inject @VisibleForTesting
	Table table;

	@Inject @VisibleForTesting 
	PokerState state;
	
	@Inject @VisibleForTesting
	ActionTransformer actionTransformer;
	
	@Inject @VisibleForTesting
	ActionSequenceGenerator actionSequenceGenerator;

    @Inject @VisibleForTesting
    TimeoutCache timeoutCache;
	
	@VisibleForTesting
	ProtocolFactory protocolFactory = new ProtocolFactory();

	@Inject
	private HandResultBatchFactory handResultBatchFactory;

	/*------------------------------------------------

		ADAPTER METHODS

		These methods are the adapter interface
		implementations

	 ------------------------------------------------*/

	@Override
	public void notifyNewHand() {
		String handId = backend.generateHandId();
		HandIdentifier playedHand = new HandIdentifier();
		playedHand.setIntegrationId(handId);
		getFirebaseState().setCurrentHandIdentifier(playedHand);
		
		StartNewHand packet = new StartNewHand();
		packet.handId = handId;
		packet.dealerSeatId = state.getBlindsInfo().getDealerButtonSeatId();
		GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
		sendPublicPacket(action, -1);

		log.debug("Starting new hand with ID '" + handId + "'. FBPlayers: "+table.getPlayerSet().getPlayerCount()+", PokerPlayers: "+state.getSeatedPlayers().size());
	}

	@Override
	public void notifyDealerButton(int seat) {
		DealerButton packet = new DealerButton();
		packet.seat = (byte)seat;
		GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
		log.debug("--> Send DealerButton["+packet+"] to everyone");
		sendPublicPacket(action, -1);
	}

	@Override
	public void notifyNewRound() { }

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
        log.debug("--> Send RequestAction["+packet+"] to everyone");
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
		log.debug("--> Send PerformAction["+packet+"] to everyone");
		sendPublicPacket(action, -1);
	}

	@Override
	public void notifyCommunityCards(List<Card> cards) {
		DealPublicCards packet = actionTransformer.createPublicCardsPacket(cards);
		GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
		log.debug("--> Send DealPublicCards["+packet+"] to everyone");
		sendPublicPacket(action, -1);
	}

	@Override
	public void notifyPrivateCards(int playerId, List<Card> cards) {
		// Send the cards to the owner with proper rank & suit information
		DealPrivateCards packet = actionTransformer.createPrivateCardsPacket(playerId, cards, false);
		GameDataAction action = protocolFactory.createGameAction(packet, playerId, table.getId());
		log.debug("--> Send DealPrivateCards["+packet+"] to player["+playerId+"]");
		sendPrivatePacket(playerId, action);

		// Send the cards as hidden to the other players
		DealPrivateCards hiddenCardsPacket = actionTransformer.createPrivateCardsPacket(playerId, cards, true);
		GameDataAction ntfyAction = protocolFactory.createGameAction(hiddenCardsPacket, playerId, table.getId());
		log.debug("--> Send DealPrivateCards(hidden)["+hiddenCardsPacket+"] to everyone");
		sendPublicPacket(ntfyAction, playerId);
	}

	@Override
	public void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand, boolean publicHand) {
		BestHand bestHandPacket = actionTransformer.createBestHandPacket(playerId, handType, cardsInHand);
		GameDataAction bestHandAction = protocolFactory.createGameAction(bestHandPacket, playerId, table.getId());
		log.debug("--> Send BestHandPacket["+bestHandPacket+"] to player["+playerId+"]");
		
		if (publicHand){
			sendPublicPacket(bestHandAction,-1);
		}else{
			sendPrivatePacket(playerId, bestHandAction);
		}
	}

	@Override
	public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
		// Send the cards as public to the other players
		DealPrivateCards hiddenCardsPacket = actionTransformer.createPrivateCardsPacket(playerId, cards, false);
		GameDataAction ntfyAction = protocolFactory.createGameAction(hiddenCardsPacket, playerId, table.getId());
		log.debug("--> Send DealPrivateCards(exposed)["+hiddenCardsPacket+"] to everyone");
		sendPublicPacket(ntfyAction, -1);
	}

	@Override
	public void exposePrivateCards(ExposeCardsHolder holder) {
		ExposePrivateCards packet = actionTransformer.createExposeCardsPacket(holder);
		GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());
		log.debug("--> Send ExposePrivateCards["+packet+"] to everyone");
		sendPublicPacket(action, -1);
	}

	@Override
	public void notifyBuyInInfo(int playerId, boolean mandatoryBuyin) {
		try {
			PokerPlayer player = state.getPokerPlayer(playerId);
	
			BuyInInfoResponse resp = new BuyInInfoResponse();
	
			int balanceOnTable = player == null ? 0 : (int) player.getBalance() + (int) player.getPendingBalance();
	
			int correctedMaxBuyIn = state.getMaxBuyIn() - balanceOnTable;
			int correctedMinBuyIn = balanceOnTable >= state.getMinBuyIn() ? 0 : state.getMinBuyIn();
			
			try {
			    resp.balanceInWallet = (int) backend.getMainAccountBalance(playerId);
			    resp.balanceOnTable = balanceOnTable;
			    
			    if (correctedMaxBuyIn <= 0){
			        resp.maxAmount = 0;
			        resp.minAmount = 0;
			        resp.resultCode = BuyInInfoResultCode.MAX_LIMIT_REACHED;
			    } else {
			        resp.maxAmount = correctedMaxBuyIn;
			        resp.minAmount = correctedMinBuyIn;
			        resp.resultCode = BuyInInfoResultCode.OK;
			    }
			    
			    resp.mandatoryBuyin = mandatoryBuyin;
			} catch (GetBalanceFailedException e) {
			    log.error("error getting balance", e);
			    resp.resultCode = BuyInInfoResultCode.UNSPECIFIED_ERROR;
	            resp.maxAmount = -1;
	            resp.minAmount = -1;
	            resp.balanceInWallet = -1;
	        }
			
			log.debug("Sending buyin information to player["+playerId+"]: "+resp);
			
			GameDataAction gda = new GameDataAction(playerId, table.getId());
	
			StyxSerializer styx = new StyxSerializer(null);
			try {
				gda.setData(styx.pack(resp));
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			table.getNotifier().notifyPlayer(playerId, gda);
		} catch (Exception e) {
			log.error("Failed to create buy in info response for player["+playerId+"], mandatory["+mandatoryBuyin+"]", e);
		}
	}

	@Override
	public void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus) {
		if (handEndStatus.equals(HandEndStatus.NORMAL) && handResult != null) {
			
			List<PotTransfer> transfers = new ArrayList<PotTransfer>();
			for (PotTransition pt : handResult.getPotTransitions()) {
			    log.debug("--> sending winner pot transfer to client: {}", pt);
				transfers.add(actionTransformer.createPotTransferPacket(pt));
			}

			long handId = getIntegrationHandId();
			TableId externalTableId = getIntegrationTableId();
			BatchHandRequest batchHandRequest = handResultBatchFactory.createAndValidateBatchHandRequest(handResult, handId, externalTableId);
            batchHandRequest.startTime = state.getStartTime();
            batchHandRequest.endTime = System.currentTimeMillis();

			BatchHandResponse batchHandResult = doBatchHandResult(batchHandRequest);

			validateAndUpdateBalances(batchHandResult);

			PotTransfers potTransfers = new PotTransfers(false, transfers, null);

			// TODO: The following logic should be moved to poker-logic
			// I.e. ranking hands etc do not belong in the game-layer
			Collection<RatedPlayerHand> hands = handResult.getPlayerHands();
			log.debug("--> handResult.getPlayerRevealOrder: {}", handResult.getPlayerRevealOrder());
			HandEnd packet = actionTransformer.createHandEndPacket(hands, potTransfers, handResult.getPlayerRevealOrder());
			GameDataAction action = protocolFactory.createGameAction(packet, 0, table.getId());

			log.debug("--> Send HandEnd["+packet+"] to everyone");
			sendPublicPacket(action, -1);

			PokerStats.getInstance().reportHandEnd();

			// increment hand count
			getFirebaseState().incrementHandCount();

			// Remove all idling players
			cleanupPlayers();
			updateLobby();

		} else {
			log.info("The hand was cancelled on table: " + table.getId() + " - " + table.getMetaData().getName());
			cleanupPlayers();
			HandCanceled handCanceledPacket = new HandCanceled();
			GameDataAction action = protocolFactory.createGameAction(handCanceledPacket, -1, table.getId());
			log.debug("--> Send HandCanceled["+handCanceledPacket+"] to everyone");
			sendPublicPacket(action, -1);
		}

		clearActionCache();
	}

    private BatchHandResponse doBatchHandResult(
			BatchHandRequest batchHandRequest) {
		BatchHandResponse batchHandResult;
		try {
			batchHandResult = backend.batchHand(batchHandRequest);
		} catch (BatchHandFailedException e) {
			throw new RuntimeException(e);
		}
		return batchHandResult;
	}
	
	public long getIntegrationHandId() {
		return Long.valueOf(getFirebaseState().getCurrentHandIdentifier().getIntegrationId());
	}
	
	private TableId getIntegrationTableId() {
		return (TableId) state.getExternalTableProperties().get(EXT_PROP_KEY_TABLE_ID);
	}

	@VisibleForTesting
	protected void validateAndUpdateBalances(BatchHandResponse batchHandResult) {
		for (BalanceUpdate bup : batchHandResult.resultingBalances) {
			PokerPlayerImpl pokerPlayer = null;
			for (PokerPlayer pp : state.getCurrentHandPlayerMap().values()) {
				if (((PokerPlayerImpl) pp).getPlayerSessionId().equals(bup.playerSessionId)) {
					pokerPlayer = (PokerPlayerImpl) pp;
				}
			}

			if (pokerPlayer == null) {
				//log.error("error updating balance: unable to find player with session = {}", bup.playerSessionId);
				throw new IllegalStateException("error updating balance: unable to find player with session = " + bup.playerSessionId);
			} else {
				long gameBalance = pokerPlayer.getBalance() + pokerPlayer.getPendingBalance();
				long backendBalance = bup.balance;

				if (gameBalance != backendBalance) {
					//log.error("backend balance: {} not equal to game balance: {}, will reset to backend value", backendBalance, gameBalance);
					throw new IllegalStateException("backend balance: "+backendBalance+" not equal to game balance: "+gameBalance+", will reset to backend value");
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
				(int) player.getBalance(), 0, (int)playersTotalContributionToPot, player.getId(), table.getId());
		sendPublicPacket(publicAction, player.getId());

		//	    // then send public packet to all the other players but exclude the pending balance
		GameDataAction privateAction = actionTransformer.createPlayerBalanceAction(
				(int) player.getBalance(), (int) player.getPendingBalance(), (int)playersTotalContributionToPot, player.getId(), table.getId());
		log.debug("Send private PBA: "+privateAction);
		sendPrivatePacket(player.getId(),privateAction);

	}

	/**
	 * Sends a poker tournament round report to the tournament as set in the table meta-data.
	 * 
	 * @param report, poker-logic protocol object, not null.
	 */
	public void reportTournamentRound(RoundReport report) {
		PokerStats.getInstance().reportHandEnd();

		// Map the report to a server specific round report
		PokerTournamentRoundReport pokerReport = new PokerTournamentRoundReport(report.getBalanceMap());
		MttRoundReportAction action = new MttRoundReportAction(table.getMetaData().getMttId(), table.getId());
		action.setAttachment(pokerReport);
		table.getTournamentNotifier().sendToTournament(action);
		clearActionCache();
	}


	public void notifyPotUpdates(Collection<com.cubeia.poker.pot.Pot> pots, Collection<PotTransition> potTransitions) {
		boolean fromPlayerToPot = !potTransitions.isEmpty()  &&  potTransitions.iterator().next().isFromPlayerToPot();
		List<Pot> clientPots = new ArrayList<Pot>();
		List<PotTransfer> transfers = new ArrayList<PotTransfer>();

		for (com.cubeia.poker.pot.Pot pot : pots) {
			clientPots.add(actionTransformer.createPotUpdatePacket(pot.getId(), pot.getPotSize()));
		}

		for (PotTransition potTransition : potTransitions) {
			log.debug("--> sending pot update to client: {}", potTransition);
			transfers.add(actionTransformer.createPotTransferPacket(potTransition));
		}

		PotTransfers potTransfers = new PotTransfers(fromPlayerToPot, transfers, clientPots);
		GameDataAction action = protocolFactory.createGameAction(potTransfers, 0, table.getId());
		sendPublicPacket(action, -1);
	}


	@Override
	public void notifyRakeInfo(RakeInfoContainer rakeInfoContainer) {
	    log.debug("--> sending rake info to client: {}", rakeInfoContainer);
		RakeInfo rakeInfo = new RakeInfo((int) rakeInfoContainer.getTotalPot(), (int) rakeInfoContainer.getTotalRake());
		GameDataAction action = protocolFactory.createGameAction(rakeInfo, 0, table.getId());
		sendPublicPacket(action, -1);
	}

	@Override
	public void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status) {
		log.debug("Notify player status changed: "+playerId+" -> "+status);
		PlayerPokerStatus packet = new PlayerPokerStatus();
		packet.player = playerId;
		packet.inCurrentHand = state.isPlayerInHand(playerId);
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

	/*------------------------------------------------

		PRIVATE METHODS

	 ------------------------------------------------*/

	/**
	 * Schedule a player timeout trigger command.
	 * @param seq 
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
	public void cleanupPlayers() {
		if (table.getMetaData().getType().equals(TableType.NORMAL)) {
			// Check for disconnected and leaving players
			UnmodifiableSet<GenericPlayer> players = table.getPlayerSet().getPlayers();
			for (GenericPlayer p : players) {
				if (p.getStatus() == PlayerStatus.DISCONNECTED || p.getStatus() == PlayerStatus.LEAVING) {
					log.debug("Player clean up - unseat leaving or disconnected player["+p.getPlayerId()+"] from table["+table.getId()+"]");
					unseatPlayer(p.getPlayerId(), false);
				}
			}

			// Check sitting out players for time outs
			Collection<PokerPlayer> timeoutPlayers = new SitoutCalculator().checkTimeoutPlayers(state);
			for (PokerPlayer p : timeoutPlayers) {
				log.debug("Player clean up - unseat timed out sit-out player["+p.getId()+"] from table["+table.getId()+"]");
				unseatPlayer(p.getId(), true);
			}
		}
	}

	public void unseatPlayer(int playerId, boolean setAsWatcher) {
		table.getPlayerSet().unseatPlayer(playerId);
		table.getListener().playerLeft(table, playerId);
		if (setAsWatcher) {
			LeaveAction leave = new LeaveAction(playerId, table.getId());
			WatchResponseAction watch = new WatchResponseAction(table.getId(), WatchResponseStatus.OK);
			table.getNotifier().sendToClient(playerId, leave);
			table.getNotifier().sendToClient(playerId, watch);
			table.getWatcherSet().addWatcher(playerId);
			table.getListener().watcherJoined(table, playerId);
		}
	}

	/**
	 * This action will be cached and used for sending current state to 
	 * joining players.
	 * 
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
	 * @param action action
	 */
	private void sendPrivatePacket(int playerId, GameAction action) {
		table.getNotifier().notifyPlayer(playerId, action);

		if (cache != null) {
			cache.addPrivateAction(table.getId(), playerId, action);
		}
	}


	private FirebaseState getFirebaseState() {
		return (FirebaseState)state.getAdapterState();
	}

	private void setRequestSequence(int seq) {
		getFirebaseState().setCurrentRequestSequence(seq);
	}


	private void updateLobby() {
		FirebaseState fbState = (FirebaseState)state.getAdapterState();
		LobbyTableAttributeAccessor lobbyTable = table.getAttributeAccessor();
		lobbyTable.setAttribute("handcount", new AttributeValue(fbState.getHandCount()));
	}

	@Override
	public void notifyDeckInfo(int size, com.cubeia.poker.hand.Rank rankLow) {
		DeckInfo deckInfoPacket = new DeckInfo(size, actionTransformer.convertRankToProtocolEnum(rankLow));
		GameDataAction action = protocolFactory.createGameAction(deckInfoPacket, 0, table.getId());
		sendPublicPacket(action, -1);
	}
	
}

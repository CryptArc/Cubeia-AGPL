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

package com.cubeia.poker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.states.NotStartedSTM;
import com.cubeia.poker.states.PlayingSTM;
import com.cubeia.poker.states.PokerGameSTM;
import com.cubeia.poker.states.WaitingToStartSTM;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.tournament.RoundReport;
import com.cubeia.poker.variant.PokerVariant;
import com.cubeia.poker.variant.telesina.Telesina;
import com.cubeia.poker.variant.telesina.TelesinaDeckFactory;
import com.cubeia.poker.variant.telesina.TelesinaRoundFactory;
import com.cubeia.poker.variant.texasholdem.TexasHoldem;

/**
 * This is the class that users of the poker api will interface with.
 * 
 * This class is responsible for handling all poker actions.
 * 
 * Also, the current state of the game can be queried from this class, to be able to send a snapshot
 * view of the game to new players.
 *
 * NOTE: The name of the class should really be Poker Game.
 * TODO: Are we breaking SRP with having these two responsibilities? How can this be fixed?
 *
 */
public class PokerState implements Serializable, IPokerState {

	private static final Logger log = Logger.getLogger(PokerState.class);

	private static final long serialVersionUID = -7208084698542289729L;

	// TODO: The internal states should preferably not be public. 
	public static final PokerGameSTM NOT_STARTED = new NotStartedSTM();

	public static final PokerGameSTM WAITING_TO_START = new WaitingToStartSTM();

	public static final PokerGameSTM PLAYING = new PlayingSTM();

	/* -------- Dependency Injection Members, initialization needed -------- */

//	@Inject
//	@TexasHoldemGame
	GameType gameType;
	

	/**
	 * The server adapter is the layer between the server and the game logic.
	 * You must set the adapter before using the game logic. The adapter is
	 * declared transient, so if you serialize the game state you will need to
	 * reset the server adapter.
	 */
	private transient ServerAdapter serverAdapter;

	private TimingProfile timing = TimingFactory.getRegistry().getDefaultTimingProfile();

	/**
	 * Identifier. May be used as seem fit.
	 */
	private int id = -1;

	/**
	 * Will be set to true if this is a tournament table.
	 */
	private boolean tournamentTable = false;

	/**
	 * Will be set if this is a tournament table.
	 */
	private int tournamentId = -1;

	/**
	 * Used by the server adapter layer to store state.
	 */
	private Object adapterState;
	
	/**
	 * Ante level (blinds etc) in cents.
	 */
	private int anteLevel = -1;

	/* ------------------------- Internal Members -------------------------- */

	/** Maps playerId to player */
	private Map<Integer, PokerPlayer> playerMap = new HashMap<Integer, PokerPlayer>();

	/** Maps playerId to player during the current hand */
	private Map<Integer, PokerPlayer> currentHandPlayerMap = new HashMap<Integer, PokerPlayer>();
	
	private SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();

	/** Seatings during the current round */
	private SortedMap<Integer, PokerPlayer> currentHandSeatingMap = new TreeMap<Integer, PokerPlayer>();
	
	private PokerGameSTM currentState = NOT_STARTED;

	private boolean handFinished = false;

	private HandResult handResult;	
	
	private PotHolder potHolder = new PotHolder();
	
	private List<Card> communityCards = new ArrayList<Card>();

	private PokerVariant variant;

    private int tableSize;

	public PokerState() {}

	public String toString() {
		return "PokerState - state[" + currentState + "] type[" + gameType + "]";
	}

	@Override
	public void init(PokerSettings settings) {
		anteLevel = settings.getAnteLevel();
		timing = settings.getTiming();
		variant = settings.getVariant();
		tableSize = settings.getTableSize();
		
		gameType = createGameTypeByVariant(variant);
		
		log.debug("poker state initialized with logic: " + gameType);
	}

	protected GameType createGameTypeByVariant(PokerVariant variant) {
		GameType gameType;
		
		switch (variant) {
		case TEXAS_HOLDEM:
			gameType = new TexasHoldem(this);
			break;
		case TELESINA:
			gameType = new Telesina(this, new TelesinaDeckFactory(), new TelesinaRoundFactory());
			break;
		default:
			throw new UnsupportedOperationException("unsupported poker variant: " + variant);
		}
		
		return gameType;
	}
	
	/**
	 * Adds a player.
	 * 
	 * TODO: Validation is required. Currently, a player can be seated in two seats. Possibly throw a checked exception.
	 * 
	 * @param player
	 */
	public void addPlayer(PokerPlayer player) {
		playerMap.put(player.getId(), player);
		seatingMap.put(player.getSeatId(), player);
		if (!isTournamentTable()) {
			startGame();
		}
	}

	/**
	 * Starts the game if all criterias are met
	 */
	private void startGame() {
		if (currentState.getClass() == NOT_STARTED.getClass() && playerMap.size() > 1) {
			serverAdapter.scheduleTimeout(timing.getTime(Periods.START_NEW_HAND));
			currentState = WAITING_TO_START;
		}
	}

	public void act(PokerAction action) {
		// Check sizes of caches and log warnings
		checkWarnings();
		currentState.act(action, this);
	}

	public List<Card> getCommunityCards() {
		return communityCards;
	}

	public boolean isFinished() {
		return handFinished;
	}

	public void timeout() {
		currentState.timeout(this);
	}

	public boolean isPlayerSeated(int playerId) {
		return playerMap.containsKey(playerId);
	}

	public Collection<PokerPlayer> getSeatedPlayers() {
		return playerMap.values();
	}
	
	@Override
	public Map<Integer, PokerPlayer> getCurrentHandPlayerMap() {
		return currentHandPlayerMap;
	}
	
	@Override
	public PokerPlayer getPlayerInCurrentHand(Integer playerId) {
		return getCurrentHandPlayerMap().get(playerId);
	}
	
	@Override
	public SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap() {
		return currentHandSeatingMap;
	}
	
	public int countSittingInPlayers() {
		int sitIn = 0;
		for (PokerPlayer player : playerMap.values()) {
			if ( player.getSitOutNextRound() ) {
				player.setSitOutStatus(SitOutStatus.SITTING_OUT);
			}
			if (!player.isSittingOut()) {
				sitIn++;
			}
		}
		return sitIn;
	}
	
	@Override
	public int countNonFoldedPlayers() {
		int nonFolded = 0;
		for (PokerPlayer p : getCurrentHandSeatingMap().values()) {
			if (!p.hasFolded() && !p.isSittingOut()) {
				nonFolded++;
			}
		}

		return nonFolded;
	}
	
	@Override
	public boolean isPlayerInHand(int playerId) {
		return getCurrentHandPlayerMap().get(playerId) == null ? false : getCurrentHandPlayerMap().get(playerId).isInHand();
	}

	public void startHand() {
		if (countSittingInPlayers() > 1) {
			currentState = PLAYING;
			notifyNewHand();
			resetValuesAtStartOfHand();
			
			currentHandSeatingMap = createCopy(seatingMap);
			currentHandPlayerMap = createCopy(playerMap);
			
			gameType.startHand();
		} else {
			throw new IllegalStateException("Not enough players to start hand. Was: " + countSittingInPlayers() + ", expected > 1. Players: "
					+ playerMap);
		}
	}

	private Map<Integer, PokerPlayer> createCopy(Map<Integer, PokerPlayer> map) {
		return new HashMap<Integer, PokerPlayer>(map);
	}

	private SortedMap<Integer, PokerPlayer> createCopy(SortedMap<Integer, PokerPlayer> sortedMap) {
		return new TreeMap<Integer, PokerPlayer>(sortedMap);
	}

	private void resetValuesAtStartOfHand() {
		gameType.prepareNewHand();
	}

	// TODO: This opens up for tinkering. Should we disallow this method?
	public GameType getGameType() {
		return gameType;
	}

	/**
	 * Should be called by the game when a hand has finished.
	 * 
	 * TODO: Should not be here. (The user of PokerGame has no interest in calling or seeing this method)
	 * 
	 * @param result
	 * @param status
	 */
	public void notifyHandFinished(HandResult result, HandEndStatus status) {
		handFinished = true;
		handResult = result;

		awardWinners(handResult.getResults());
		
		if (tournamentTable) {
			// Report round to tournament coordinator and wait for notification
			tournamentRoundReport();
		} else {
			// TODO: Handle pending add chips requests.
			log.debug("Notify Hand Finished schedule timeout");
			serverAdapter.notifyHandEnd(handResult, status);
			log.debug("Schedule hand over timeout in: "+timing.getTime(Periods.START_NEW_HAND));
			serverAdapter.scheduleTimeout(timing.getTime(Periods.START_NEW_HAND));
		}
		currentState = WAITING_TO_START;
	}

	private void awardWinners(Map<PokerPlayer, Result> results) {
		for (Entry<PokerPlayer, Result> entry : results.entrySet()) {
			PokerPlayer player = entry.getKey();
			player.addChips(entry.getValue().getWinningsIncludingOwnBets());
		}
	}
	
	private void tournamentRoundReport() {
		RoundReport report = new RoundReport();
		for (PokerPlayer player : playerMap.values()) {
			report.setSetBalance(player.getId(), player.getBalance());
		}
		log.debug("Sending tournament round report: "+report);
		serverAdapter.reportTournamentRound(report);
	}

	public PokerGameSTM getGameState() {
		return currentState;
	}

	/**
	 * TODO: Should not be here. (The user of PokerGame has no interest in calling or seeing this method)
	 * Also: This should encapsulated so it cannot be tinkered with.
	 */
	public void setState(PokerGameSTM state) {
		this.currentState = state;
	}

	public void removePlayer(PokerPlayer player) {
		removePlayer(player.getId());
	}

	public void removePlayer(int playerId) {
		PokerPlayer removed = playerMap.remove(playerId);
		if (removed != null) {
			seatingMap.remove(removed.getSeatId());
		}
	}
	
	public PokerPlayer getPokerPlayer(int playerId) {
		return playerMap.get(playerId);
	}
	
	// TODO: Should not be possible to call like this. The game type should only be possible to change between hands.
	public void setGameType(GameType gameType) {
		log.debug("setting gametype = " + gameType + " to state: " + this);
		this.gameType = gameType;
	}

	// TODO: Should not be public.
	public BlindsInfo getBlindsInfo() {
		return gameType.getBlindsInfo();
	}

	public TimingProfile getTimingProfile() {
		return timing;
	}

	@Override
	public PokerVariant getPokerVariant() {
		return variant;
	}
	
	public int getTableSize() {
        return tableSize;
    }
	
	public boolean isTournamentTable() {
		return tournamentTable;
	}

	// TODO: Refactor to inheritance.
	public void setTournamentTable(boolean tournamentTable) {
		this.tournamentTable = tournamentTable;
	}

	public int getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(int tournamentId) {
		this.tournamentId = tournamentId;
	}

	/**
	 * Called by the adapter layer when a player leaves or disconnects.
	 * 
	 * @param playerId
	 */
	public void playerIsSittingOut(int playerId) {
		PokerPlayer player = playerMap.get(playerId);
		if ( player != null ) {
			player.setSitOutNextRound(true);
		}
	}
	
	/**
	 * Called by the adapter layer when a player rejoins/reconnects.
	 * 
	 * @param playerId
	 */
	public void playerIsSittingIn(int playerId) {
		PokerPlayer player = playerMap.get(playerId);
		if ( player != null ) {
			player.sitIn();
			player.setSitOutNextRound(false);
			notifyPlayerSittingIn(playerId);
			startGame();
		}
	}
	
	/*------------------------------------------------
	 
		SERVER ADAPTER METHODS
		
		These methods propagate to the server adapter.
		The nature of the methods is that they 
		demand communication with the player(s).
		
		// TODO: None of these methods should be public here. Instead, inject the server adapter into classes
		         that need to call the server adapter.

	 ------------------------------------------------*/

	
	public void notifyNewHand() {
	    serverAdapter.notifyNewHand();
	}
	
	public void requestAction(ActionRequest r) {
		r.setTimeToAct(timing.getTime(Periods.ACTION_TIMEOUT));
		log.debug("Send player action request ["+r+"]");
		serverAdapter.requestAction(r);
	}

	public void notifyCommunityCards(List<Card> cards) {
		serverAdapter.notifyCommunityCards(cards);
	}

	public void notifyPrivateCards(int playerId, List<Card> cards) {
		serverAdapter.notifyPrivateCards(playerId, cards);
	}

	public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
	    serverAdapter.notifyPrivateExposedCards(playerId, cards);
	}
	
	public void exposePrivateCards(int playerId, List<Card> cards) {
		serverAdapter.exposePrivateCards(playerId, cards);
	}

	public void notifyPlayerBalance(int playerId) {
	    serverAdapter.notifyPlayerBalance(playerMap.get(playerId));
	}
	
	public void setHandFinished(boolean finished) {
		handFinished = finished;
	}
	
	/**
	 * Removes all disconnected players from the table
	 */
	public void cleanupPlayers() {
	    serverAdapter.cleanupPlayers();
	}

	public void updatePot() {
        serverAdapter.updatePots(potHolder.getPots());
    }	

	/**
	 * TODO: Should not be here. (The user of PokerGame has no interest in calling or seeing this method)
	 */
	public void notifyDealerButton(int dealerButtonSeatId) {
		serverAdapter.notifyDealerButton(dealerButtonSeatId);
	}

//	public void notifyPlayerAllIn(int playerId) {
//		serverAdapter.notifyPlayerStatusChanged(playerId, PokerPlayerStatus.ALLIN);
//	}
	
	public void notifyPlayerSittingOut(int playerId) {
		serverAdapter.notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITOUT);
	}
	
	public void notifyPlayerSittingIn(int playerId) {
		serverAdapter.notifyPlayerStatusChanged(playerId, PokerPlayerStatus.NORMAL);
	}
	
	public ServerAdapter getServerAdapter() {
		return serverAdapter;
	}
	
	public void setServerAdapter(ServerAdapter serverAdapter) {
		this.serverAdapter = serverAdapter;
	}
	
	// TODO: Refactor. The holder of this instance can create a new class which holds this instance together with other data.
	public Object getAdapterState() {
		return adapterState;
	}
	
	// TODO: Refactor. The holder of this instance can create a new class which holds this instance together with other data.	
	public void setAdapterState(Object adapterState) {
		this.adapterState = adapterState;
	}
	
	/*------------------------------------------------
	 
		END OF SERVER ADAPTER METHODS

 	------------------------------------------------*/
	

	// TODO: Refactor. The holder of this instance can create a new class which holds this instance together with other data.	
	public int getId() {
		return id;
	}

	// TODO: Refactor. The holder of this instance can create a new class which holds this instance together with other data.	
	public void setId(int id) {
		this.id = id;
	}

	public String getStateDescription() {
		return currentState.getClass().getName() + "_" + gameType.getStateDescription();
	}

	/**
	 * Adds chips to a player. If the player is in a hand, the chips will be
	 * added after the hand if finished.
	 * 
	 * @param playerId
	 * @param chips
	 * @return <code>true</code> if the chips were added immediately,
	 *         <code>false</code> if they will be added when the hand is
	 *         finished.
	 */
	public void addChips(int playerId, long chips) {
		if (!playerMap.containsKey(playerId)) {
			throw new IllegalArgumentException("Player " + playerId + " tried to add chips, but was not seated.");
		}
		
		if (isPlayerInHand(playerId)) {
			// Add pending chips request.
		} else {
			playerMap.get(playerId).addChips(chips);
		}
	}

	public int getBalance(int playerId) {
		return (int) playerMap.get(playerId).getBalance();
	}

	public PotHolder getPotHolder() {
		return potHolder;
	}
	
	public int getAnteLevel() {
		return anteLevel;
	}

	public void setAnteLevel(int anteLevel) {
		this.anteLevel = anteLevel;
	}
	
	private void checkWarnings() {
		if (playerMap.size() > 20) {
			log.warn("PLAYER MAP SIZE WARNING. Size="+playerMap.size()+", Values: "+playerMap);
		}
		if (seatingMap.size() > 20) {
			log.warn("SEATING MAP SIZE WARNING. Size="+seatingMap.size()+", Values: "+seatingMap);
		}
	}

	public void notifyPlayerBalanceReset(PokerPlayer player) {
		serverAdapter.notifyPlayerBalanceReset(player);
		
	}

	
	
}

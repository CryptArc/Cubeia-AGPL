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

import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.states.*;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.GameType;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * This is the class that users of the poker api will interface with.
 * <p/>
 * This class is responsible for handling all poker actions.
 * <p/>
 * Also, the current state of the game can be queried from this class, to be able to send a snapshot
 * view of the game to new players.
 * <p/>
 * NOTE: The name of the class should really be PokerGame.
 */
public class PokerState implements Serializable, IPokerState {

    private static final Logger log = LoggerFactory.getLogger(PokerState.class);

    private static final long serialVersionUID = -7208084698542289729L;

    /* -------- Dependency Injection Members, initialization needed -------- */

    /**
     * The server adapter is the layer between the server and the game logic.
     * You must set the adapter before using the game logic. The adapter is
     * declared transient, so if you serialize the game state you will need to
     * reset the server adapter.
     */
    private transient ServerAdapter serverAdapter;

    private ServerAdapterHolder serverAdapterHolder = new ServerAdapterHolder();

    /**
     * Used by the server adapter layer to store state. (Should be removed)
     */
    private Object adapterState;

    /* ------------------------- Internal Members -------------------------- */

    private StateHolder stateHolder = new StateHolder();

    private StateChanger stateChanger = stateHolder;

    /**
     * Map of external table properties. External properties are optional stuff that might be needed
     * when integrating to external systems. Session/table/tournament id's for example.
     */
    private Map<String, Serializable> externalTableProperties = new HashMap<String, Serializable>();

    @VisibleForTesting
    protected PokerContext pokerContext;

    private GameType gameType;

    public PokerState() {
    }

    public String toString() {
        return pokerContext.toString();
    }

    @Override
    public void init(GameType gameType, PokerSettings settings) {
        pokerContext = new PokerContext(settings);
        this.gameType = gameType;
        gameType.setPokerContextAndServerAdapter(pokerContext, serverAdapterHolder);
        stateHolder.changeState(new NotStartedSTM(gameType, pokerContext, serverAdapterHolder, stateChanger));
    }

    /**
     * Adds a player.
     * <p/>
     *
     * @param player
     */
    public void addPlayer(PokerPlayer player) {
        pokerContext.addPlayer(player);
        stateHolder.get().playerJoined(player);
    }

    public void act(PokerAction action) {
        // Check sizes of caches and log warnings
        pokerContext.checkWarnings();
        getCurrentState().act(action);
    }

    public List<Card> getCommunityCards() {
        return pokerContext.getCommunityCards();
    }

    public boolean isFinished() {
        return pokerContext.isFinished();
    }

    public void timeout() {
        getCurrentState().timeout();
    }

    @Override
    public void playerSitsOut(int playerId, SitOutStatus sitOutStatus) {
        getCurrentState().playerSitsOut(playerId, sitOutStatus);
    }

    public boolean isPlayerSeated(int playerId) {
        return pokerContext.isPlayerSeated(playerId);
    }

    public Collection<PokerPlayer> getSeatedPlayers() {
        return pokerContext.getSeatedPlayers();
    }

    @Override
    public Map<Integer, PokerPlayer> getCurrentHandPlayerMap() {
        return pokerContext.getCurrentHandPlayerMap();
    }

    /**
     * Returns the map of external table properties.
     *
     * @return
     */
    public Map<String, Serializable> getExternalTableProperties() {
        return externalTableProperties;
    }

    @Override
    public PokerPlayer getPlayerInCurrentHand(Integer playerId) {
        return pokerContext.getPlayerInCurrentHand(playerId);
    }

    @Override
    public SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap() {
        return pokerContext.getCurrentHandSeatingMap();
    }

    @Override
    public void sitOutPlayersMarkedForSitOutNextRound() {
        pokerContext.sitOutPlayersMarkedForSitOutNextRound();
    }

    /**
     * Returns true if the player is in the set of players for the hand and
     * we are in a playing state (i.e. not playing or waiting to start will result
     * in false being returned).
     */
    @Override
    public boolean isPlayerInHand(int playerId) {
        return stateHolder.get().isPlayerInHand(playerId);
    }

    public void startHand() {
        getCurrentState().startHand();
    }

    public void scheduleTournamentHandStart() {
        log.debug("Received start hand signal. Scheduling a timeout so the hand doesn't start too quickly.");
        long timeout = getSettings().getTiming().getTime(Periods.START_NEW_HAND);
        log.debug("Scheduling timeout in " + timeout + " millis.");
        serverAdapterHolder.get().scheduleTimeout(timeout);
    }

    public long getStartTime() {
        return pokerContext.getStartTime();
    }

    @VisibleForTesting
    public void commitPendingBalances() {
        pokerContext.commitPendingBalances();
    }

    public PokerGameSTM getGameState() {
        return getCurrentState();
    }

    public void removePlayer(int playerId) {
        pokerContext.removePlayer(playerId);
    }

    public PokerPlayer getPokerPlayer(int playerId) {
        return pokerContext.getPokerPlayer(playerId);
    }

    public TimingProfile getTimingProfile() {
        return pokerContext.getTimingProfile();
    }

    public int getTableSize() {
        return pokerContext.getTableSize();
    }

    // TODO: Refactor to inheritance.
    public void setTournamentTable(boolean tournamentTable) {
        pokerContext.setTournamentTable(tournamentTable);
    }

    public void setTournamentId(int tournamentId) {
        pokerContext.setTournamentId(tournamentId);
    }

    /**
     * Called by the adapter layer when a player rejoins/reconnects.
     *
     * @param playerId the id of the player to check
     */
    public void playerIsSittingIn(int playerId) {
        log.debug("Player " + playerId + " is sitting in.");
        getCurrentState().playerSitsIn(playerId);
    }

    /*------------------------------------------------

         SERVER ADAPTER METHODS

         These methods propagate to the server adapter.
         The nature of the methods is that they
         demand communication with the player(s).

         // TODO: None of these methods should be public here. Instead, inject the server adapter into classes
                  that need to call the server adapter.

      ------------------------------------------------*/

    public void notifyPotAndRakeUpdates(Collection<PotTransition> potTransitions) {
        serverAdapter.notifyPotUpdates(pokerContext.getPotHolder().getPots(), potTransitions);

        // notify all the new balances
        for (PokerPlayer player : pokerContext.getCurrentHandPlayerMap().values()) {
            serverAdapter.notifyPlayerBalance(player);
        }
        notifyRakeInfo();
    }

    public void notifyRakeInfo() {
        serverAdapter.notifyRakeInfo(pokerContext.getPotHolder().calculateRakeIncludingBetStacks(pokerContext.getCurrentHandSeatingMap().values()));
    }

    /**
     * TODO: Should not be here. (The user of PokerGame has no interest in calling or seeing this method)
     */
    public void notifyDealerButton(int dealerButtonSeatId) {
        serverAdapter.notifyDealerButton(dealerButtonSeatId);
    }

    public ServerAdapter getServerAdapter() {
        return serverAdapter;
    }

    public void setServerAdapter(ServerAdapter serverAdapter) {
        this.serverAdapter = serverAdapter;
        serverAdapterHolder.set(serverAdapter);
    }

    // TODO: Refactor. The holder of this instance can create a new class which holds this instance together with other data.
    public Object getAdapterState() {
        return adapterState;
    }

    // TODO: Refactor. The holder of this instance can create a new class which holds this instance together with other data.
    public void setAdapterState(Object adapterState) {
        this.adapterState = adapterState;
    }

    public void unseatPlayer(int playerId, boolean setAsWatcher) {
        serverAdapter.unseatPlayer(playerId, setAsWatcher);
    }

    /*------------------------------------------------

         END OF SERVER ADAPTER METHODS

      ------------------------------------------------*/


    public int getTableId() {
        return pokerContext.getTableId();
    }

    public void setTableId(int tableId) {
        pokerContext.setTableId(tableId);
    }

    public String getStateDescription() {
        return getCurrentState().getClass().getName() + "_" + gameType.getStateDescription();
    }

    public int getBalance(int playerId) {
        return pokerContext.getBalance(playerId);
    }

    public PotHolder getPotHolder() {
        return pokerContext.getPotHolder();
    }

    public int getAnteLevel() {
        return pokerContext.getAnteAmount();
    }

    public int getMinBuyIn() {
        return pokerContext.getMinBuyIn();
    }

    public int getMaxBuyIn() {
        return pokerContext.getMaxBuyIn();
    }

    public boolean removeAsWatcher(int playerId) {
        return pokerContext.removeAsWatcher(playerId);
    }

    public void addWatcher(int playerId) {
        pokerContext.addWatcher(playerId);
    }

    public PokerSettings getSettings() {
        return pokerContext.getSettings();
    }

    @Override
    public void shutdown() {
        log.debug("Shutting down table {}", getTableId());
        stateChanger.changeState(new ShutdownSTM());
    }

    protected PokerGameSTM getCurrentState() {
        return stateHolder.get();
    }

    public long getPlayersTotalContributionToPot(PokerPlayer player) {
        return pokerContext.getPlayersTotalContributionToPot(player);
    }

    @Override
    public void handleBuyInRequest(PokerPlayer pokerPlayer, int amount) {
        pokerPlayer.addRequestedBuyInAmount(amount);
        getCurrentState().performPendingBuyIns(Collections.singleton(pokerPlayer));
    }

    public boolean isWaitingForPlayerToAct(int playerId) {
        return getCurrentState().isCurrentlyWaitingForPlayer(playerId);
    }

    // TODO: Preferably remove this method, or at least replace with code in state class.
    public boolean isPlaying() {
        return getCurrentState() instanceof PlayingSTM;
    }

    public void notifyBuyinInfo(int playerId, boolean mandatoryBuyin) {
        serverAdapter.notifyBuyInInfo(playerId, mandatoryBuyin);
    }

    // TODO: Remove, this is only used from one test.
    public PokerContext getContext() {
        return pokerContext;
    }

    public void playerOpenedSession(int playerId) {
        stateHolder.get().playerOpenedSession(playerId);
    }

    public void setBlindsLevels(int smallBlindAmount, int bigBlindAmount, int ante) {
        pokerContext.setBlindsLevels(smallBlindAmount, bigBlindAmount, ante);
    }
}

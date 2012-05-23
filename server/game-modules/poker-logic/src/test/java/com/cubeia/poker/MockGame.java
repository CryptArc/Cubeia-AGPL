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
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.states.ServerAdapterHolder;
import com.cubeia.poker.variant.HandFinishedListener;

import java.util.*;

public class MockGame implements GameType {

    private static final long serialVersionUID = 1L;

    private SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();

    private SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();

    public BlindsInfo blindsInfo = new BlindsInfo();

    public List<TestListener> listeners = new ArrayList<TestListener>();

    public boolean roundFinished = false;

    public boolean blindsCanceled = false;

    private MockServerAdapter mockServerAdapter = new MockServerAdapter();

    public MockGame() {
    }

    @Override
    public void act(PokerAction action) {
    }

    public void roundFinished() {
        roundFinished = true;
    }

    @Override
    public void startHand() {
    }

    public void initMockState(SortedMap<Integer, PokerPlayer> seatingMap, SortedMap<Integer, PokerPlayer> playerMap) {
        this.seatingMap = seatingMap;
        this.playerMap = playerMap;
    }

    public void addPlayers(MockPlayer[] p) {
        for (MockPlayer m : p) {
            seatingMap.put(m.getSeatId(), m);
            playerMap.put(m.getId(), m);
        }
    }

    @Override
    public void prepareNewHand() {
        // YEAH YEAH.
    }

    @Override
    public void timeout() {
    }

    @Override
    public String getStateDescription() {
        return null;
    }

    public void logDebug(String string) {
    }

    public IPokerState getState() {
        return new IPokerState() {

            private PokerPlayer player1 = new DefaultPokerPlayer(1000);
            private PokerPlayer player2 = new DefaultPokerPlayer(1001);
            private PokerPlayer player3 = new DefaultPokerPlayer(1002);
            private PokerPlayer player4 = new DefaultPokerPlayer(1003);


            @Override
            public void init(GameType gameType, PokerSettings settings) {
            }

            @Override
            public void playerSitsOut(int playerId, SitOutStatus sitOutStatus) {

            }

            @Override
            public SortedMap<Integer, PokerPlayer> getCurrentHandSeatingMap() {
                return seatingMap;
            }

            @Override
            public Map<Integer, PokerPlayer> getCurrentHandPlayerMap() {
                return playerMap;
            }

            @Override
            public int getAnteLevel() {
                return 0;
            }

            @SuppressWarnings("unchecked")
            @Override
            public List<Card> getCommunityCards() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public PokerPlayer getPlayerInCurrentHand(Integer playerId) {
                return playerMap.get(playerId);
            }

            @Override
            public int countNonFoldedPlayers() {
                return 4;
            }

            @Override
            public boolean isPlayerInHand(int playerId) {
                return false;
            }

            @Override
            public void notifyDealerButton(int dealerButtonSeatId) {
                // System.out.println("Dealer button is on seat: " + dealerButtonSeatId);
                // WAEVVA, I'll do what i want, I'm a mock!
            }

            @Override
            public int getEntryBetLevel() {
                return 0;
            }

            @Override
            public void notifyBetStacksUpdated() {
            }

            @Override
            public void shutdown() {
            }

            @Override
            public void sitOutPlayersMarkedForSitOutNextRound() {
            }

            @Override
            public void handleBuyInRequest(PokerPlayer pokerPlayer, int amount) {
            }

            @Override
            public boolean isEveryoneSittingOut() {
                return false;
            }

        };
    }

    @Override
    public void scheduleRoundTimeout() {
    }

    @Override
    public void dealCommunityCards() {

    }

    @Override
    public boolean canPlayerAffordEntryBet(PokerPlayer player, PokerSettings settings, boolean includePending) {
        return true;
    }

    @Override
    public void sendAllNonFoldedPlayersBestHand() {
    }

    @Override
    public boolean isCurrentlyWaitingForPlayer(int playerId) {
        return false;
    }

    @Override
    public void addHandFinishedListener(HandFinishedListener handFinishedListener) {
    }

    @Override
    public void removeHandFinishedListener(HandFinishedListener handFinishedListener) {
    }

    @Override
    public void setPokerContextAndServerAdapter(PokerContext context, ServerAdapterHolder serverAdapterHolder) {

    }

}

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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.rounds.blinds.BlindsInfo;
import com.cubeia.poker.variant.PokerVariant;

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
	
//	@Override
//	public int getAnteLevel() {
//		return 100;
//	}
	
	@Override
	public void act(PokerAction action) {
	}

	@Override
	public BlindsInfo getBlindsInfo() {
		return blindsInfo;
	}

//	@Override
//	public PokerPlayer getPlayer(int playerId) {
//		return playerMap.get(playerId);
//	}

//	@Override
//	public Iterable<PokerPlayer> getPlayers() {
//		return seatingMap.values();
//	}

	@Override
	public void requestAction(ActionRequest r) {
		for (TestListener l : listeners) {
			l.notifyActionRequested(r);
		}
	}
	
	@Override
	public void requestMultipleActions(Collection<ActionRequest> requests) {
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

//	@Override
//	public int countNonFoldedPlayers() {
//		int nonFolded = 0;
//		for (PokerPlayer p : seatingMap.values()) {
//			if (!p.hasFolded()) {
//				nonFolded++;
//			}
//		}
//
//		return nonFolded;
//	}

	@Override
	public void prepareNewHand() {
		// YEAH YEAH.
	}

	@Override
	public ServerAdapter getServerAdapter() {
		return mockServerAdapter;
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

	@Override
	public IPokerState getState() {
		return new IPokerState() {
		    
            private PokerPlayer player1 = new DefaultPokerPlayer(1000);
            private PokerPlayer player2 = new DefaultPokerPlayer(1001);
            private PokerPlayer player3 = new DefaultPokerPlayer(1002);
            private PokerPlayer player4 = new DefaultPokerPlayer(1003);
		    
		    
			@Override
			public void notifyPlayerSittingOut(int playerId) {
			}
			
			@Override
			public void init(RNGProvider rng, PokerSettings settings) {
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
			public Collection<PokerPlayer> getPlayersReadyToStartHand() {
			    return asList(player1, player2, player3, player4);
			}
			
			@Override
			public boolean isPlayerInHand(int playerId) {
				return false;
			}
			
			@Override
			public PokerVariant getPokerVariant() {
			    return PokerVariant.TEXAS_HOLDEM;
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
			public void playerIsSittingOut(int playerId, SitOutStatus misssedAnte) {}
			
			@Override
			public void call() {}

			@Override
			public void exposeShowdownCards() {}
			
			@Override
			public void notifyBetStacksUpdated() {}
			
			@Override
			public void shutdown() {}
			
			@Override
			public boolean isShutdown() { return false; }
			
			@Override
			public boolean isPlaying() { return true; }
			
			@Override
			public void sitOutPlayersMarkedForSitOutNextRound() {}

            @Override
            public void handleBuyInRequest(PokerPlayer pokerPlayer, int amount) {
                // TODO Auto-generated method stub
                
            }
		
		};
	}

	@Override
	public void scheduleRoundTimeout() {}

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
	
}

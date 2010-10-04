package com.cubeia.poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ca.ualberta.cs.poker.Card;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.blinds.BlindsInfo;

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
	public int getAnteLevel() {
		return 100;
	}
	
	public void act(PokerAction action) {
	}

	public BlindsInfo getBlindsInfo() {
		return blindsInfo;
	}

	public List<Card> getCommunityCards() {
		return null;
	}

	public PokerPlayer getPlayer(int playerId) {
		return playerMap.get(playerId);
	}

	public Iterable<PokerPlayer> getPlayers() {
		return seatingMap.values();
	}

	public SortedMap<Integer, PokerPlayer> getSeatingMap() {
		return seatingMap;
	}

	public void requestAction(ActionRequest r) {
		for (TestListener l : listeners) {
			l.notifyActionRequested(r);
		}
	}

//	public void requestAction(PokerPlayer player, PossibleAction... options) {
//		ActionRequest a = new ActionRequest();
//		for (PossibleAction option : options) {
//			a.enable(option);
//		}
//		a.setPlayerId(player.getId());
//		player.setActionRequest(a);
//		requestAction(a);
//	}

	public void roundFinished() {
		roundFinished = true;
	}

	public void startHand(SortedMap<Integer, PokerPlayer> seatingMap, Map<Integer, PokerPlayer> playerMap) {
	}

	public void addPlayers(MockPlayer[] p) {
		for (MockPlayer m : p) {
			seatingMap.put(m.getSeatId(), m);
			playerMap.put(m.getId(), m);
		}
	}

	public int countNonFoldedPlayers() {
		int nonFolded = 0;
		for (PokerPlayer p : getSeatingMap().values()) {
			if (!p.hasFolded()) {
				nonFolded++;
			}
		}

		return nonFolded;
	}

	public void prepareNewHand() {
		// YEAH YEAH.
	}

	public void notifyDealerButton(int dealerButtonSeatId) {
		System.out.println("Dealer button is on seat: " + dealerButtonSeatId);
		// WAEVVA, I'll do what i want, I'm a mock!
	}

	public ServerAdapter getServerAdapter() {
		return mockServerAdapter;
	}

	public void timeout() {
	}

	public String getStateDescription() {
		return null;
	}

	public boolean isPlayerInHand(int playerId) {
		return false;
	}

	public void logDebug(String string) {
	}

	@Override
	public PokerState getState() {
		return null;
	}

	@Override
	public void scheduleRoundTimeout() {}

	@Override
	public void dealCommunityCards() {
		
	}
	
}

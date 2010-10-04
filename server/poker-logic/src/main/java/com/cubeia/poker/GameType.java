package com.cubeia.poker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import ca.ualberta.cs.poker.Card;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.blinds.BlindsInfo;

/**
 * Each game type, such as Texas Hold'em or Omaha should implement this interface.
 *
 * TODO: *SERIOUS* cleanup and probably major refactoring.
 */
public interface GameType extends Serializable {

	public void startHand(SortedMap<Integer, PokerPlayer> seatingMap, Map<Integer, PokerPlayer> playerMap);

	public void act(PokerAction action);

	public List<Card> getCommunityCards();

	public SortedMap<Integer, PokerPlayer> getSeatingMap();

	public PokerPlayer getPlayer(int playerId);

	public void scheduleRoundTimeout();
	
	public void requestAction(ActionRequest r);

//	public void requestAction(PokerPlayer player, PossibleAction... option);

	public BlindsInfo getBlindsInfo();

	public Iterable<PokerPlayer> getPlayers();
	
	public int countNonFoldedPlayers();

	public void prepareNewHand();

	public void notifyDealerButton(int dealerButtonSeatId);

	public ServerAdapter getServerAdapter();

	public void timeout();

	public String getStateDescription();

	public boolean isPlayerInHand(int playerId);

	public PokerState getState();

	public int getAnteLevel();
	
	public void dealCommunityCards();
}

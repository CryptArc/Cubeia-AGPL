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

package com.cubeia.poker.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.model.PlayerHand;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.result.Result;



public class HandResultCalculatorTest extends TestCase {

	private Map<Integer, PokerPlayer> players;
	HandResultCalculator calc = new HandResultCalculator();
	private ArrayList<PlayerHand> hands;
	
	@Override
	protected void setUp() throws Exception {
		// All players has 100 and bets 10, 20 and 40 respectively
		players = new HashMap<Integer, PokerPlayer>();
		PokerPlayer p1 = new DefaultPokerPlayer(1);
		p1.addChips(100);
		p1.addBet(10);
		PokerPlayer p2 = new DefaultPokerPlayer(2);
		p2.addChips(100);
		p2.addBet(20);
		PokerPlayer p3 = new DefaultPokerPlayer(3);
		p3.addChips(100);
		p3.addBet(40);
		
		players.put(1, p1);
		players.put(2, p2);
		players.put(3, p3);
		
	    hands = new ArrayList<PlayerHand>();
		
		String community = "Ac Kc Qd 6h Th";
		hands.add(new PlayerHand(1, new Hand("As Ad "+community))); // Best Hand - 3 Aces
		hands.add(new PlayerHand(2, new Hand("2s 7d "+community)));
		hands.add(new PlayerHand(3, new Hand("3s 8d "+community)));
	}
	
	
	public void testSimpleCase() {
		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(players.values());
		
		assertEquals(1, potHolder.getNumberOfPots());
		assertEquals(70, potHolder.getPotSize(0));
		long p1stake = potHolder.getActivePot().getPotContributors().get(players.get(1));
		assertEquals(10, p1stake);
		long p2stake = potHolder.getActivePot().getPotContributors().get(players.get(2));
		assertEquals(20, p2stake);
		long p3stake = potHolder.getActivePot().getPotContributors().get(players.get(3));
		assertEquals(40, p3stake);
		
		
		Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, players);
		
		Result result1 = playerResults.get(players.get(1));
		assertEquals(60, result1.getNetResult());
		assertEquals(70, result1.getWinningsIncludingOwnBets());
		
		assertEquals(3, playerResults.size());
		
	}
	
	
	public void testGetWinners() {
		hands = new ArrayList<PlayerHand>();
		String community = "Ac Kc Qd 6h Th";
		hands.add(new PlayerHand(1, new Hand("As Ad " + community))); // SPLIT HAND - 3 Aces
		hands.add(new PlayerHand(2, new Hand("As Ad " + community))); // SPLIT HAND - 3 Aces
		hands.add(new PlayerHand(3, new Hand("3s 8d " + community)));
		
		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(players.values());
		
		assertEquals(1, potHolder.getNumberOfPots());
		assertEquals(70, potHolder.getPotSize(0));
		long p1stake = potHolder.getActivePot().getPotContributors().get(players.get(1));
		assertEquals(10, p1stake);
		long p2stake = potHolder.getActivePot().getPotContributors().get(players.get(2));
		assertEquals(20, p2stake);
		long p3stake = potHolder.getActivePot().getPotContributors().get(players.get(3));
		assertEquals(40, p3stake);
		
		assertEquals(1, potHolder.getNumberOfPots());
		
		Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, players);
		
		Result result1 = playerResults.get(players.get(1));
		assertEquals(25, result1.getNetResult());
		assertEquals(35, result1.getWinningsIncludingOwnBets());
		
		Result result2 = playerResults.get(players.get(2));
		assertEquals(15, result2.getNetResult());
		assertEquals(35, result2.getWinningsIncludingOwnBets());
		
		Result result3 = playerResults.get(players.get(3));
		assertEquals(-40, result3.getNetResult());
		assertEquals(0, result3.getWinningsIncludingOwnBets());
		
		assertEquals(0, result1.getNetResult()+result2.getNetResult()+result3.getNetResult());
		
		assertEquals(3, playerResults.size());
	}
	
	public void testMultiplePots() {
		players = new HashMap<Integer, PokerPlayer>();
		PokerPlayer p1 = new DefaultPokerPlayer(1);
		p1.addChips(100);
		p1.addBet(80);
		PokerPlayer p2 = new DefaultPokerPlayer(2);
		p2.addChips(100);
		p2.addBet(80);
		PokerPlayer p3 = new DefaultPokerPlayer(3);
		p3.addChips(40);
		p3.addBet(40);
		
		assertTrue(p3.isAllIn());
		
		players.put(1, p1);
		players.put(2, p2);
		players.put(3, p3);
		
		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(players.values());
		
		assertEquals(2, potHolder.getNumberOfPots());
		
		hands = new ArrayList<PlayerHand>();
		String community = " Ac Kc Qd 6h Th";
		hands.add(new PlayerHand(1, new Hand("Ks 8d"+community))); // Second best hand - 2 Kings
		hands.add(new PlayerHand(2, new Hand("2s 7d"+community)));
		hands.add(new PlayerHand(3, new Hand("As Ad"+community))); // Best Hand - 3 Aces
		
		Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, players);
		
		assertEquals(3, playerResults.size());
		
		Result result1 = playerResults.get(players.get(1));
		assertEquals(0, result1.getNetResult());
		assertEquals(80, result1.getWinningsIncludingOwnBets());
		
		Result result2 = playerResults.get(players.get(2));
		assertEquals(-80, result2.getNetResult());
		assertEquals(0, result2.getWinningsIncludingOwnBets());
		
		Result result3 = playerResults.get(players.get(3));
		assertEquals(80, result3.getNetResult());
		assertEquals(120, result3.getWinningsIncludingOwnBets());
		
		assertEquals(0, result1.getNetResult()+result2.getNetResult()+result3.getNetResult());
		
	}
	
	
	public void testMultipleBets() {
		players = new HashMap<Integer, PokerPlayer>();
		PokerPlayer p1 = new DefaultPokerPlayer(1);
		p1.addChips(100);
		p1.addBet(10);
		PokerPlayer p2 = new DefaultPokerPlayer(2);
		p2.addChips(100);
		p2.addBet(20);
		PokerPlayer p3 = new DefaultPokerPlayer(3);
		p3.addChips(100);
		p3.addBet(40);
		
		players.put(1, p1);
		players.put(2, p2);
		players.put(3, p3);
		
		hands = new ArrayList<PlayerHand>();
		String community = " Ac Kc Qd 6h Th";
		hands.add(new PlayerHand(1, new Hand("As Ad"+community))); // Best Hand - 3 Aces
		hands.add(new PlayerHand(2, new Hand("2s 7d"+community)));
		hands.add(new PlayerHand(3, new Hand("3s 8d"+community)));
		
		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(players.values());
		
		// Exactly the same bets again
		potHolder.moveChipsToPot(players.values());
		
		assertEquals(1, potHolder.getNumberOfPots());
		assertEquals(140, potHolder.getPotSize(0));
		long p1stake = potHolder.getActivePot().getPotContributors().get(players.get(1));
		assertEquals(20, p1stake);
		long p2stake = potHolder.getActivePot().getPotContributors().get(players.get(2));
		assertEquals(40, p2stake);
		long p3stake = potHolder.getActivePot().getPotContributors().get(players.get(3));
		assertEquals(80, p3stake);
		
		Map<PokerPlayer, Result> playerResults = calc.getPlayerResults(hands, potHolder, players);
		
		Result result1 = playerResults.get(players.get(1));
		assertEquals(120, result1.getNetResult());
		assertEquals(140, result1.getWinningsIncludingOwnBets());
		
		assertEquals(3, playerResults.size());
		
	}
}

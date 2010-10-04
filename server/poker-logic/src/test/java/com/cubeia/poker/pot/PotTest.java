package com.cubeia.poker.pot;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;

import junit.framework.TestCase;

public class PotTest extends TestCase {
	private static int counter = 0;

	public void testSimpleCase() {
		PokerPlayer p1 = createPokerPlayer(20);
		PokerPlayer p2 = createPokerPlayer(20);

		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(Arrays.asList(p1, p2));
		assertEquals(1, potHolder.getNumberOfPots());
		assertEquals(40, potHolder.getTotalPotSize());
		assertEquals(40, potHolder.getPotSize(0));
	}

	private PokerPlayer createPokerPlayer(int amountBet) {
		return createPokerPlayer(amountBet, false);
	}
	
	private PokerPlayer createPokerPlayer(int amountBet, Boolean allIn) {
		PokerPlayer p = new DefaultPokerPlayer(counter ++);
		if ( allIn ) {
			p.addChips(amountBet);
		} else {
			p.addChips(5 * amountBet);
		}
		p.addBet(amountBet);
		return p;
	}

	public void testSimpleCaseWithFold() {
		PokerPlayer p1 = createPokerPlayer(20);
		PokerPlayer p2 = createPokerPlayer(20);
		PokerPlayer p3 = createPokerPlayer(10);
		p3.setHasFolded(true);

		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(Arrays.asList(p1, p2, p3));
		assertEquals(1, potHolder.getNumberOfPots());
		assertEquals(50, potHolder.getTotalPotSize());
		assertEquals(50, potHolder.getPotSize(0));
	}

	public void testOneSidePot() {
		PokerPlayer p1 = createPokerPlayer(20);
		PokerPlayer p2 = createPokerPlayer(20);
		PokerPlayer p3 = createPokerPlayer(10, true);

		PotHolder potHolder = new PotHolder();
		SortedSet<PokerPlayer> allIns = new TreeSet<PokerPlayer>();
		allIns.add(p3);
		potHolder.moveChipsToPot(Arrays.asList(p1, p2, p3));
		assertEquals(2, potHolder.getNumberOfPots());
		assertEquals(50, potHolder.getTotalPotSize());
		assertEquals(30, potHolder.getPotSize(0));
		assertEquals(20, potHolder.getPotSize(1));
		assertEquals(3, potHolder.getPot(0).getPotContributors().size());
		assertEquals(2, potHolder.getPot(1).getPotContributors().size());
	}

	public void testTwoSidePots() {
		PotHolder potHolder = createPotWithSidePots();
		assertEquals(3, potHolder.getNumberOfPots());
		assertEquals(60, potHolder.getTotalPotSize());
		assertEquals(20, potHolder.getPotSize(0));
		assertEquals(30, potHolder.getPotSize(1));
		assertEquals(10, potHolder.getPotSize(2));
	}
	
	public void testTwoSidePots2() {
		PokerPlayer p1 = createPokerPlayer(5, true);
		PokerPlayer p2 = createPokerPlayer(10);
		PokerPlayer p3 = createPokerPlayer(8, true);
		PokerPlayer p4 = createPokerPlayer(10);
		PokerPlayer p5 = createPokerPlayer(2);

		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(Arrays.asList(p1, p2, p3, p4, p5));
		assertEquals(3, potHolder.getNumberOfPots());
		assertEquals(35, potHolder.getTotalPotSize());
		assertEquals(22, potHolder.getPotSize(0));
		assertEquals(9, potHolder.getPotSize(1));
		assertEquals(4, potHolder.getPotSize(2));
	}


	public void testConsecutiveMoves() {
		PotHolder potHolder = createPotWithSidePots();
		assertEquals(3, potHolder.getNumberOfPots());
		assertEquals(60, potHolder.getTotalPotSize());
		assertEquals(20, potHolder.getPotSize(0));
		assertEquals(30, potHolder.getPotSize(1));
		assertEquals(10, potHolder.getPotSize(2));

		PokerPlayer p1 = createPokerPlayer(20);
		PokerPlayer p2 = createPokerPlayer(20);
		// Second round
		potHolder.moveChipsToPot(Arrays.asList(p1, p2));
		assertEquals(3, potHolder.getNumberOfPots());
		assertEquals(100, potHolder.getTotalPotSize());
		assertEquals(50, potHolder.getPotSize(2));
	}

	public void testReturnUnCalledChips() {
		PokerPlayer p1 = createPokerPlayer(5);
		PokerPlayer p2 = createPokerPlayer(10);

		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(Arrays.asList(p1, p2));
		assertEquals(5, p2.getReturnedChips());
	}

	public void testReturnUnCalledChipsAfterFold() {
		PokerPlayer p1 = createPokerPlayer(0);
		PokerPlayer p2 = createPokerPlayer(10);

		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(Arrays.asList(p1, p2));
		assertEquals(10, p2.getReturnedChips());
	}

	public void testRake() {
		PotHolder p = new PotHolder();
		p.addPot(5L);
		p.addPot(20L);

		p.rake(10);
		assertEquals(5, p.getRakeAmount());
		assertEquals(0, p.getPot(0).getPotSize());
		assertEquals(25, p.getTotalPotSize());
	}

	public void testRakeNothing() {
		PotHolder p = new PotHolder();
		p.rake(0);
	}
	
	public void testPotId() {
		PotHolder potHolder = createPotWithSidePots();
		int i = 0;
		for (Pot p : potHolder.getPots()) {
			assertEquals(i++, p.getId());
		}
	}

	private PotHolder createPotWithSidePots() {
		PokerPlayer p1 = createPokerPlayer(20);
		PokerPlayer p2 = createPokerPlayer(20);
		PokerPlayer p3 = createPokerPlayer(15, true);
		PokerPlayer p4 = createPokerPlayer(5, true);

		PotHolder potHolder = new PotHolder();
		potHolder.moveChipsToPot(Arrays.asList(p1, p2, p3, p4));
		return potHolder;
	}
	

}

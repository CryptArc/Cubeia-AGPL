package poker.gs.server.blinds.utils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import poker.gs.server.blinds.BlindsPlayer;

import junit.framework.TestCase;

public class PokerUtilsTest extends TestCase {

	public void testGetElementAfter() {
		SortedMap<Integer, BlindsPlayer> map = new TreeMap<Integer, BlindsPlayer>();
		
		BlindsPlayer p1 = new MockPlayer(1);
		BlindsPlayer p2 = new MockPlayer(2);
		BlindsPlayer p3 = new MockPlayer(3);
		
		map.put(3, p3);
		map.put(1, p1);
		map.put(2, p2);
	
		assertEquals(p3, PokerUtils.getElementAfter(2, map));
		assertEquals(p1, PokerUtils.getElementAfter(3, map));
		assertEquals(p2, PokerUtils.getElementAfter(1, map));
	}
	
	public void testEqualsBug() {
		SortedMap<Integer, BlindsPlayer> map = new TreeMap<Integer, BlindsPlayer>();
		
		BlindsPlayer p1 = new MockPlayer(1);
		BlindsPlayer p2 = new MockPlayer(2);
		BlindsPlayer p3 = new MockPlayer(3);
		
		map.put(Integer.valueOf(3), p3);
		map.put(Integer.valueOf(1), p1);
		map.put(Integer.valueOf(2), p2);
	
		assertEquals(p1, PokerUtils.getElementAfter(Integer.valueOf(3), map));
	}
	
	public void testGetElementAfterNonExistingElement() {
		SortedMap<Integer, BlindsPlayer> map = new TreeMap<Integer, BlindsPlayer>();
		
		BlindsPlayer p1 = new MockPlayer(1);
		BlindsPlayer p3 = new MockPlayer(3);
		
		map.put(3, p3);
		map.put(1, p1);
	
		assertEquals(p3, PokerUtils.getElementAfter(2, map));
	}
	
	public void testGetElementAfterSpecialCases() {
		SortedMap<Integer, BlindsPlayer> map = new TreeMap<Integer, BlindsPlayer>();
		
		BlindsPlayer p3 = new MockPlayer(3);
		
		map.put(3, p3);
	
		assertEquals(p3, PokerUtils.getElementAfter(2, map));
		assertEquals(p3, PokerUtils.getElementAfter(3, map));
		assertEquals(p3, PokerUtils.getElementAfter(4, map));
	}	
	
	public void testUnwrapList() {
		SortedMap<Integer, BlindsPlayer> map = new TreeMap<Integer, BlindsPlayer>();
		
		BlindsPlayer p1 = new MockPlayer(1);
		BlindsPlayer p2 = new MockPlayer(2);
		BlindsPlayer p3 = new MockPlayer(3);
		
		map.put(Integer.valueOf(3), p3);
		map.put(Integer.valueOf(1), p1);
		map.put(Integer.valueOf(2), p2);
		
		List<BlindsPlayer> list = PokerUtils.unwrapList(map, 3);
		assertEquals(p3, list.get(0));
		assertEquals(p1, list.get(1));
		assertEquals(p2, list.get(2));
		assertEquals(3, list.size());
	}
	
}

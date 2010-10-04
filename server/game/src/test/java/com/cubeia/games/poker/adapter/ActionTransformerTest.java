package com.cubeia.games.poker.adapter;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import ca.ualberta.cs.poker.Hand;

import com.cubeia.games.poker.io.protocol.HandEnd;
import com.cubeia.poker.model.PlayerHands;

public class ActionTransformerTest extends TestCase {

	public void testCreateHandEndPacket() {
		Hand hand1 = new Hand("As Ks");
		Hand hand2 = new Hand("Td Tc");
		
		Hand community = new Hand("Qs Js Ts 4d 2c");
		hand1.addCards(community.getCards());
		hand2.addCards(community.getCards());
		
		Map<Integer, Hand> hands = new HashMap<Integer, Hand>();
		hands.put(11, hand1);
		hands.put(22, hand2);
		
		
		PlayerHands playerHands = new PlayerHands(hands);
		
		HandEnd end = ActionTransformer.createHandEndPacket(playerHands);
		
		assertEquals(2, end.hands.size());
		assertNotSame("Two High", end.hands.get(0).name);
		assertNotSame("Two High", end.hands.get(1).name);
	}

}

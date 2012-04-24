package com.cubeia.poker.player;

import org.junit.Assert;
import org.junit.Test;

public class DefaultPokerPlayerSitoutTimeTest {
	
	@Test
	public void testTimestamp() {
		PokerPlayer player = new DefaultPokerPlayer(1);
		Assert.assertNull(player.getSitOutTimestamp());
		player.setSitOutStatus(SitOutStatus.SITTING_OUT);
		Assert.assertNotNull(player.getSitOutTimestamp());
		Assert.assertTrue(player.getSitOutTimestamp() <= System.currentTimeMillis());
		
		player.sitIn();
		Assert.assertNull(player.getSitOutTimestamp());
	}
	
}

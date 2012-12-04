package com.cubeia.games.poker.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.io.protocol.AchievementNotificationPacket;
import com.cubeia.games.poker.io.protocol.ProtocolObjectFactory;

public class testMarshalling {

	private StyxSerializer styx = new StyxSerializer(new ProtocolObjectFactory());
	
	@Test
	public void testStyx() throws IOException {
		AchievementNotificationPacket notification = new AchievementNotificationPacket();
		notification.playerId = 123;
		notification.message = "ABC";
		
		ProtocolFactory factory = new ProtocolFactory();
		GameDataAction action = factory.createGameAction(notification, 123, 1);
		
		ByteBuffer data = action.getData();
		AchievementNotificationPacket unpacked = (AchievementNotificationPacket)styx.unpack(data);
		
		assertThat(unpacked.message, is(notification.message));
	}

}

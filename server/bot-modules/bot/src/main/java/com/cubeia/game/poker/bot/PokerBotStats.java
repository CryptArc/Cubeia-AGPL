package com.cubeia.game.poker.bot;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.cubeia.firebase.api.util.TimeCounter;
import com.cubeia.firebase.io.protocol.ProbePacket;
import com.cubeia.firebase.io.protocol.ProbeStamp;

public class PokerBotStats implements PokerBotStatsMBean {
	
	private static PokerBotStats instance = null;

	public static PokerBotStats getInstance() {
		if (instance == null) {
			instance = new PokerBotStats();
			instance.initJmx();
		}
		return instance;
	}

	private final TimeCounter roundTrip = new TimeCounter(1000);
	
	public void report(ProbePacket p) {
		long first = -1;
		long last = -1;
		for (ProbeStamp s : p.stamps) {
			if(first == -1) {
				first = s.timestamp;
			}
			last = s.timestamp;
		}
		long time = last - first;
		roundTrip.register(time);
	}
	
	@Override
	public long getRoundTripAvarage() {
		return (long) roundTrip.calculate();
	}
	
	private void initJmx() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName monitorName = new ObjectName("com.cubeia.bot.poker:type=PokerBotStats");
			mbs.registerMBean(getInstance(), monitorName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

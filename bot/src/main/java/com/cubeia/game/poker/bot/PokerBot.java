package com.cubeia.game.poker.bot;

import org.apache.log4j.Logger;

import com.cubeia.firebase.io.protocol.GameTransportPacket;
import com.cubeia.firebase.io.protocol.ProbePacket;
import com.cubeia.firebase.bot.Bot;
import com.cubeia.firebase.bot.ai.BasicAI;

/**
 * Poker Bot.
 * Relies on the Cubeia load test framework.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerBot extends BasicAI{
	 
	private static transient Logger log = Logger.getLogger(PokerBot.class);
	
	private GameHandler handler;
	
	public PokerBot(Bot bot) {
		super(bot);
		handler = new GameHandler(this);
	}

	public synchronized void handleGamePacket(GameTransportPacket packet) {
		if (table.getId() != packet.tableid) {
    		log.fatal("I received wrong table id! I am seated at: "+table.getId()+". I got packet from: "+packet.tableid+" Packet: "+handler.unpack(packet));
    	}
    	handler.handleGamePacket(packet);
	}

	/**
	 * I don't care, said Pierre,
	 * cause I am from France
	 */
	public void handleProbePacket(ProbePacket packet) {}

	public void stop() {}

	public boolean trackTableState() {
		return true;
	}

}

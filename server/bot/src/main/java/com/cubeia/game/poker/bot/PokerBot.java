package com.cubeia.game.poker.bot;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoRequest;
import se.jadestone.dicearena.service.network.protocol.LoginRequestPayloadStruct;
import se.jadestone.dicearena.service.network.protocol.ProtocolObjectFactory;

import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.firebase.io.protocol.GameTransportPacket;
import com.cubeia.firebase.io.protocol.ProbePacket;
import com.cubeia.firebase.bot.Bot;
import com.cubeia.firebase.bot.action.Action;
import com.cubeia.firebase.bot.ai.BasicAI;
import com.cubeia.firebase.bot.ai.Delays;

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
	
	@Override
	protected void handleLoggedin() {
		super.handleLoggedin();
	}
	
	
    protected void handleConnect() {
        Action action = new Action(bot) {
            
        	public void run() {
        		bot.login(createCredentials());
            }

			private byte[] createCredentials() {
				LoginRequestPayloadStruct str = new LoginRequestPayloadStruct();
				str.partnerCode = "dicearena";
				str.credential = bot.getScreenname();
				str.desiredNickName = bot.getScreenname();
				str.partnerAccountId = bot.getScreenname();
				try {
					return new StyxSerializer(new ProtocolObjectFactory()).packArray(str);
				} catch (IOException e) {
					throw new IllegalStateException("Failed to pack login credentials", e);
				}
			}
        };
        
        executor.schedule(action, Delays.LOGIN_DELAY_SECONDS, TimeUnit.SECONDS);
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
	
	/**
	 * Send a buy in info request as soon as we are seated.
	 */
	@Override
	protected void handleSeated() {
		super.handleSeated();
		BuyInInfoRequest buyInInfoRequest = new BuyInInfoRequest();
		getBot().sendGameData(getTable().getId(), getBot().getPid(), buyInInfoRequest);
	}

}

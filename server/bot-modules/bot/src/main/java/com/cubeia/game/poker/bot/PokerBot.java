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

package com.cubeia.game.poker.bot;

import java.util.Random;

import org.apache.log4j.Logger;

import com.cubeia.firebase.bot.Bot;
import com.cubeia.firebase.bot.ai.BasicAI;
import com.cubeia.firebase.bot.ai.Delays;
import com.cubeia.firebase.io.protocol.GameTransportPacket;
import com.cubeia.firebase.io.protocol.ProbePacket;

/**
 * Poker Bot.
 * Relies on the Cubeia load test framework.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerBot extends BasicAI {

	private static transient Random RAND = new Random();
    private static transient Logger log = Logger.getLogger(PokerBot.class);

    private GameHandler handler;
    // private int loginDelay;
    private long calculatedLoginDelay;
    private long calculatedJoinDelay;
    // private int joinDelay;
   
    public PokerBot(Bot bot) {
        super(bot);
        handler = new GameHandler(this);
        setLoginDelay(0);
        setJoinDelay(0);
    }
    
    public void setLoginDelay(int loginDelay) {
		// this.loginDelay = loginDelay;
		if(loginDelay > 0) {
			calculatedLoginDelay = RAND.nextInt(loginDelay * 1000);
			getBot().logDebug("Calculated login delay in millis: " + calculatedLoginDelay);
		} else {
			calculatedLoginDelay = Delays.LOGIN_DELAY_SECONDS * 1000;
		}
	}
    
    public void setJoinDelay(int joinDelay) {
		// this.joinDelay = joinDelay;
		if(joinDelay > 0) {
			calculatedJoinDelay = RAND.nextInt(joinDelay * 1000);
			getBot().logDebug("Calculated join delay in millis: " + calculatedLoginDelay);
		} else {
			calculatedJoinDelay = Delays.SEAT_DELAY_SECONDS * 1000;
		}
	}
    
    @Override
    protected long getJoinDelay() {
    	return calculatedJoinDelay;
    }
    
    @Override
    protected long getLoginDelay() {
    	return calculatedLoginDelay;
    }
    
    @Override
    public void handleProbePacket(ProbePacket packet) {
        // if (table.getId() == packet.tableid) {
        	handler.handleProbePacket(packet);
        // }
    }
    
    public synchronized void handleGamePacket(GameTransportPacket packet) {
        if (table.getId() != packet.tableid) {
            log.fatal("I received wrong table id! I am seated at: " + table.getId() + ". I got packet from: " + packet.tableid + " Packet: " + handler.unpack(packet));
        }
        handler.handleGamePacket(packet);
    }

    @Override
    protected void handleLoggedin() {
        super.handleLoggedin();
    }

    public void stop() {
    }

    public boolean trackTableState() {
        return true;
    }

    /**
     * Send a buy in info request as soon as we are seated.
     */
    @Override
    protected void handleSeated() {
    	super.setDisableLeaveTable(true); // enable after entered first hand (on first action request)
        super.handleSeated();
        // Do not send buying request, we'll get that when the session opens
        // BuyInInfoRequest buyInInfoRequest = new BuyInInfoRequest();
        // getBot().sendGameData(getTable().getId(), getBot().getPid(), buyInInfoRequest);
    }

}

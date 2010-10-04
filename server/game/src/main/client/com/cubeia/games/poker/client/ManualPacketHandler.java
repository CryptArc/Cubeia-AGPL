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

package com.cubeia.games.poker.client;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.cubeia.firebase.clients.java.connector.text.IOContext;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.firebase.io.protocol.GameTransportPacket;
import com.cubeia.games.poker.io.protocol.ProtocolObjectFactory;

/**
 * 
 * Created on 2006-sep-19
 * @author Fredrik Johansson
 *
 * $RCSFile: $
 * $Revision: $
 * $Author: $
 * $Date: $
 */
public class ManualPacketHandler extends ClientPacketAdapterHandler {
    
    @SuppressWarnings("unused")
	private Logger log = Logger.getLogger(ManualPacketHandler.class);
    
    @SuppressWarnings("unused")
	private IOContext context;
    
    /**
     * Handles the test game specific actions.
     * You need to inject an implementation before
     * using testgame packets, or they wont be handled!
     */
    private ManualGameHandler gameHandler;
    
    private StyxSerializer styxDecoder = new StyxSerializer(new ProtocolObjectFactory());
    
    public ManualPacketHandler(IOContext context) {
    	this.context = context;
    }
    
    
    
	public ManualGameHandler getTestHandler() {
		return gameHandler;
	}


	/**
	 * IOC injection.
	 * 
	 * @param testHandler
	 */
	public void setTestHandler(ManualGameHandler testHandler) {
		this.gameHandler = testHandler;
	}

	public void visit(GameTransportPacket packet) {
		ProtocolObject data;
		try {
			data = styxDecoder.unpack(ByteBuffer.wrap(packet.gamedata));
			if (data != null) {
				data.accept(gameHandler);
			}
		} catch (IOException e) {
			System.out.println("Can't create packet: "+packet);
			return;
		}
	}
	
}


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


package com.cubeia.games.poker.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

import com.cubeia.firebase.clients.java.connector.text.SimpleTextClient;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.io.protocol.PlayerAction;
import com.cubeia.games.poker.io.protocol.Enums.ActionType;

public class PokerTextClient extends SimpleTextClient {
	
	public static int seq = -1;
	
    private StyxSerializer styxEncoder = new StyxSerializer(null);
	
	
	public PokerTextClient(String host, int port) {
		super(host, port);
		
		ManualPacketHandler handler = new ManualPacketHandler(context);
		handler.setTestHandler(new ManualGameHandler(context));
		context.getConnector().addPacketHandler(handler);
	}
	
	/**
     * Override this in your implementation to handle
     * client specific commands.
     * 
     * @param command
     */
	@Override
    public void handleCommand(String command) {
    	try {
			String[] args = command.split(" ");
			
			if (args.length < 1) {
				reportBadCommand(command);
				return;
			}
			
			
			if (args[0].equalsIgnoreCase("help")) {
				printHelp();
			} else {
				handlePokerCommand(args);
			}
			
			
		} catch (Exception e) {
			reportBadCommand(e.toString());
		}
	}
    
	

	private void handlePokerCommand(String[] args) {
		PerformAction packet = new PerformAction();
		packet.player = context.getPlayerId();
		packet.seq = seq;
		
		if (args[0].equals("small")) {
			PlayerAction type = new PlayerAction();
			type.type = ActionType.SMALL_BLIND;
			packet.action = type;
			
		} else if (args[0].equals("big")) {
			PlayerAction type = new PlayerAction();
			type.type = ActionType.BIG_BLIND;
			packet.action = type;
			
		} else if (args[0].equals("check")) {
			PlayerAction type = new PlayerAction();
			type.type = ActionType.CHECK;
			packet.action = type;
			
		} else if (args[0].equals("call")) {
			PlayerAction type = new PlayerAction();
			type.type = ActionType.CALL;
			packet.action = type;
			
		} else if (args[0].equals("bet")) {
			try {
				PlayerAction type = new PlayerAction();
				type.type = ActionType.BET;
				packet.action = type;
				packet.betAmount = Integer.parseInt(args[2]);
			} catch (Exception e) {
				System.out.println("usage: bet <tid> <amount>");
			}
			
		} else if (args[0].equals("raise")) {
			try {
				PlayerAction type = new PlayerAction();
				type.type = ActionType.RAISE;
				packet.action = type;
				packet.betAmount = Integer.parseInt(args[2]);
			} catch (Exception e) {
				System.out.println("usage: bet <tid> <amount>");
			}
			
		} else if (args[0].equals("fold")) {
			PlayerAction type = new PlayerAction();
			type.type = ActionType.FOLD;
			packet.action = type;
			
		}

		
		ByteBuffer buffer;
		try {
			buffer = styxEncoder.pack(packet);
			int tableid = Integer.parseInt(args[1]);
			// Sends data wrapped in a GameTransportPacket
			// the context attribute is supplied by the super class
			context.getConnector().sendDataPacket(tableid, context.getPlayerId(), buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
            System.err.println("Usage: java PokerTextClient [port] host \nEx.: " +
            		"\n\t java PokerTextClient localhost" +
            		"\n\t java PokerTextClient 4123 localhost");
            return;
        }
		

        int hostIndex = 0;
        int port = 4123; // Default
        
        // If the first argument is a string of digits then we take that
        // to be the port number to use
        if (Pattern.matches("[0-9]+", args[0])) {
            port = Integer.parseInt(args[0]);            
            hostIndex = 1;
        }
       
        PokerTextClient client = new PokerTextClient(args[hostIndex], port);
        client.run();


	}
	

	/**
	 * Print bad command to user with a specified error.
	 * 
	 * @param error
	 */
	private void reportBadCommand(String error) {
		System.err.println("Invalid command ("+error+") Format:cmd TID <amount>");
	}
	
	private void printHelp() {
		System.out.println("Available Poker Commands:");
		System.out.println("\t help             \t : print help");
		System.out.println("\t small TID        \t : post small blind");
		System.out.println("\t big TID          \t : post big blind");
		
		System.out.println("\t check TID        \t : Check");
		System.out.println("\t call TID         \t : Call");
		System.out.println("\t bet TID <amount> \t : Bet");
		System.out.println("\t fold TID         \t : Fold");
	}
}

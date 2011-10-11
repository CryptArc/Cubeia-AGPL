package com.cubeia.games.poker.debugger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import se.jadestone.dicearena.game.poker.network.protocol.Enums.ActionType;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerAction;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;
import se.jadestone.dicearena.game.poker.network.protocol.RequestAction;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;

/**
 * Starts and run the service stand alone
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class WebserverRunner {

	@Mock ServiceContext context;
	
	@Mock ServiceRegistry registry;
	
	/** Serializer for poker packets */
	private static StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new WebserverRunner();
	}
	
	public WebserverRunner() {
		try {
			init();
			start();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
	
	public void init() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(context.getParentRegistry()).thenReturn(registry);
	}

	public void start() throws Exception {
		HandDebuggerFacade facade = new HandDebuggerFacade();
		Thread thread = new Thread(new Runner(facade));
		thread.setDaemon(true);
		thread.start();
		
		Thread.sleep(1000);
		
		PlayerAction data = new PlayerAction(ActionType.BET, 10, 10);
		List<PlayerAction> actionsAllowed = new ArrayList<PlayerAction>();
		actionsAllowed.add(data);
		RequestAction request = new RequestAction(1, 111, actionsAllowed, 100);
		GameDataAction action1 = addGameEvent(request);
		facade.addPublicAction(1, action1);
		
		PerformAction playerActed = new PerformAction(2, 111, data, 0, 0, 100, false, 123);
		GameDataAction action2 = addGameEvent(playerActed);
		facade.addPublicAction(1, action2);
		
		Thread.sleep(100000);
	}

	private GameDataAction addGameEvent(ProtocolObject data) throws IOException {
		GameDataAction action = new GameDataAction(111, 1);
		ByteBuffer src = serializer.pack(data);
		action.setData(src);
		return action;
	}
	
	
	private class Runner implements Runnable {

		private final HandDebuggerFacade facade;

		public Runner(HandDebuggerFacade facade) {
			this.facade = facade;
		}
		
		@Override
		public void run() {
			try {
				facade.init(context);
				facade.start();
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
	}
}

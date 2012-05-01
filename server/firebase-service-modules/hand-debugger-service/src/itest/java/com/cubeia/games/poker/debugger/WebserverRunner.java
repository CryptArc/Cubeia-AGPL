package com.cubeia.games.poker.debugger;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.io.protocol.*;
import com.cubeia.games.poker.io.protocol.Enums.ActionType;
import com.cubeia.games.poker.io.protocol.Enums.PlayerTableStatus;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Starts and run the service stand alone
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class WebserverRunner {

    @Mock
    ServiceContext context;

    @Mock
    ServiceRegistry registry;

    /**
     * Serializer for poker packets
     */
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

        // Add some events
        addEvent(facade, new PlayerPokerStatus(111, PlayerTableStatus.SITIN));
        addEvent(facade, new DealerButton(Byte.valueOf("1")));

        PlayerAction data = new PlayerAction(ActionType.BET, 10, 10);
        List<PlayerAction> actionsAllowed = new ArrayList<PlayerAction>();
        actionsAllowed.add(data);
        addEvent(facade, new RequestAction(1, 1, 111, actionsAllowed, 100));

        addEvent(facade, new PerformAction(2, 111, data, 0, 0, 100, false));
        addPrivateEvent(facade, new DealPrivateCards());

        Thread.sleep(100000);
    }

    private void addEvent(HandDebuggerFacade facade, ProtocolObject request) throws IOException {
        GameDataAction action = addGameEvent(request);
        facade.addPublicAction(1, action);
    }

    private void addPrivateEvent(HandDebuggerFacade facade, ProtocolObject request) throws IOException {
        GameDataAction action = addGameEvent(request);
        facade.addPrivateAction(1, 111, action);
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

package com.cubeia.games.poker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;

import com.cubeia.firebase.api.action.AbstractGameAction;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.TableChatAction;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class TableCrashHandler {
    private static Logger log = LoggerFactory.getLogger(TableCrashHandler.class);
    private static StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
    
    private final PokerState state;
    private final ActionCache actionCache;
    private final BackendPlayerSessionHandler backendPlayerSessionHandler;
    
    @Inject
    public TableCrashHandler(PokerState state, ActionCache actionCache, BackendPlayerSessionHandler backendPlayerSessionHandler) {
        this.state = state;
        this.actionCache = actionCache;
        this.backendPlayerSessionHandler = backendPlayerSessionHandler;
    }
    
    
    public void handleCrashOnTable(AbstractGameAction action, Table table, Throwable throwable) {
        log.info("handling crashed table {}", table.getId());
        
        printToErrorLog(action, table, throwable);
        
        Collection<GenericPlayer> players = Lists.newArrayList(table.getPlayerSet().getPlayers());
        
        // 1. stop table from accepting actions
        //    Gotcha: client actions should not be accepted but callbacks from backend should.
        state.shutdown();
        
        // 2. set table to invisible in lobby
        makeTableInvisibleInLobby(table);
        
        // 3. send error message to clients, must include hand id
        sendErrorMessageToClient(table);
        
        // 4. remove players from table
        Collection<PokerPlayer> removedPokerPlayers = removePlayersFromTable(table, players);
        
        // 5. close player sessions
        closePlayerSessions(table, removedPokerPlayers);
        
        // 5. close table session
        // TODO!
        
        
    }

    private void sendErrorMessageToClient(Table table) {
        // TODO: should send dialog message and not a chat message
        
        String msg = "Unexpected table state. Table has been stopped.";

        TableChatAction chat = new TableChatAction(-1, table.getId(), msg);
        table.getNotifier().notifyAllPlayers(chat);
        
    }

    private void closePlayerSessions(Table table, Collection<PokerPlayer> pokerPlayers) {
        for (PokerPlayer pokerPlayer : pokerPlayers) {
            backendPlayerSessionHandler.endPlayerSessionInBackend(table, pokerPlayer);
        }
    }

    private Collection<PokerPlayer> removePlayersFromTable(Table table, Collection<GenericPlayer> players) {
        Collection<PokerPlayer> removedPlayers = new ArrayList<PokerPlayer>();
        
        for (GenericPlayer genericPlayer : players) {
            PokerPlayer pokerPlayer = state.getPokerPlayer(genericPlayer.getPlayerId());
            table.getPlayerSet().removePlayer(genericPlayer.getPlayerId());
            removedPlayers.add(pokerPlayer);
        }
        
        return removedPlayers;
    }


    private void makeTableInvisibleInLobby(Table table) {
        log.debug("setting table {} as invisible in lobby", table.getId());
        table.getAttributeAccessor().setIntAttribute("VISIBLE_IN_LOBBY", 0);
    }


    private void printToErrorLog(AbstractGameAction action, Table table, Throwable throwable) {
        if (action instanceof GameDataAction) {
            GameDataAction gda = (GameDataAction) action;
            ProtocolObject packet = null;
            try {
                packet = serializer.unpack(gda.getData());
            } catch (IOException e) {
                log.warn("error unpacking action for diagnostics", e);
            }
            printActionsToErrorLog(throwable, "error handling game action: "+action+" Table: "+table.getId()+" Packet: "+packet, table);
        } else if (action instanceof GameObjectAction) {
            printActionsToErrorLog(throwable, "error handling command action: "+action+" on table: "+table, table);
        } else {
            printActionsToErrorLog(throwable, "error handling action (" + action.getClass().getSimpleName() + "): "+action+" on table: "+table, table);
        }
    }


    /**
     * Dump all cached actions to the error log in case of an error.
     * @param throwable optional throwable that caused the error
     * @param description description of the error
     * @param table the current table
     */
    public void printActionsToErrorLog(Throwable throwable, String description, Table table) {
        List<GameAction> actions = actionCache.getPublicActions(table.getId());
        StringBuilder error = new StringBuilder(description);
        error.append("\nState: "+state);
        for (GameAction history : actions) {
            ProtocolObject packet = null;
            try {
                if (history instanceof GameDataAction) {
                    GameDataAction dataAction = (GameDataAction) history;
                    packet = serializer.unpack(dataAction.getData());
                }
            } catch (Exception e2) {};
            error.append("\n\t"+packet);
        }
        error.append("\nStackTrace: ");
        log.error(error.toString(), throwable);
    }
    
}

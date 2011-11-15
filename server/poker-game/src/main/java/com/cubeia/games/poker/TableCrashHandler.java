package com.cubeia.games.poker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.Enums;
import se.jadestone.dicearena.game.poker.network.protocol.ErrorPacket;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;

import com.cubeia.firebase.api.action.AbstractGameAction;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.lobby.PokerLobbyAttributes;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.PokerPlayer;
import com.google.common.annotations.VisibleForTesting;
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
        
//        // 4. remove players from table
//        Collection<PokerPlayer> removedPokerPlayers = removePlayersFromTable(table, players);
        
//        // 5. close player sessions
//        closePlayerSessions(table, removedPokerPlayers);
        
        // 5. mark table as closed and let the activator take care of destroying it
        markTableReadyForClose(table);
    }

    private void markTableReadyForClose(Table table) {
        table.getAttributeAccessor().setAttribute(PokerLobbyAttributes.TABLE_READY_FOR_CLOSE.name(), new AttributeValue(1));
    }

    private void sendErrorMessageToClient(Table table) {
        for (GenericPlayer player : table.getPlayerSet().getPlayers()) {
            Enums.ErrorCode errorCode = Enums.ErrorCode.UNSPECIFIED_ERROR;
            ErrorPacket errorPacket = new ErrorPacket(errorCode,"knark");
            
            log.debug("sending error message to player: {}", player.getPlayerId());
            
            GameDataAction errorAction = new GameDataAction(player.getPlayerId(), table.getId());
            ByteBuffer packetBuffer;
            try {
                packetBuffer = serializer.pack(errorPacket);
                errorAction.setData(packetBuffer);
                table.getNotifier().notifyPlayer(player.getPlayerId(), errorAction);
            } catch (IOException e) {
                log.error("failed to send error message to client", e);
            }
        }
    }

    @VisibleForTesting
    protected void closePlayerSessions(Table table, Collection<PokerPlayer> pokerPlayers) {
        for (PokerPlayer pokerPlayer : pokerPlayers) {
            try {
                backendPlayerSessionHandler.endPlayerSessionInBackend(table, pokerPlayer);
            } catch (Exception e) {
                log.error("error closing player session for player = " + pokerPlayer.getId(), e);
            }
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
        table.getAttributeAccessor().setIntAttribute(PokerLobbyAttributes.VISIBLE_IN_LOBBY.name(), 0);
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

package com.cubeia.games.poker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;
import se.jadestone.dicearena.game.poker.network.protocol.RequestAction;
import se.jadestone.dicearena.game.poker.network.protocol.StartHandHistory;
import se.jadestone.dicearena.game.poker.network.protocol.StopHandHistory;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Class responsible for sending the cached game state to a connecting or re-connecting client.
 * @author w
 */
public class GameStateSender {
    private static Logger log = LoggerFactory.getLogger(PokerTableListener.class);
    private final ActionCache actionCache;

    @Inject
    public GameStateSender(ActionCache actionCache) {
        this.actionCache = actionCache;
    }
    
    public void sendGameState(Table table, int playerId) {
        ProtocolFactory protocolFactory = new ProtocolFactory();
        int tableId = table.getId();
        
        log.debug("sending stored game actions to client, player id = {}", playerId);
        List<GameAction> actions = new LinkedList<GameAction>();
        actions.add(protocolFactory.createGameAction(new StartHandHistory(), playerId, tableId));
        
        List<GameAction> actionsFromCache = actionCache.getPrivateAndPublicActions(tableId, playerId);
        actionsFromCache = filterRequestActions(actionsFromCache);
        actions.addAll(actionsFromCache);
        
        actions.add(protocolFactory.createGameAction(new StopHandHistory(), playerId, tableId));
        log.debug("done sending {} stored game actions, player id = {}", actions.size() - 2, playerId);
        
        table.getNotifier().notifyPlayer(playerId, actions);
    }

    /**
     * Filter the game actions list by removing all GameDataActions containing RequestAction packets
     * that have been answered by a PerformAction.
     * 
     * @param actions actions to filter
     * @return new filtered list
     */
    @VisibleForTesting
    protected List<GameAction> filterRequestActions(List<GameAction> actions) {
        LinkedList<GameAction> filteredActions = new LinkedList<GameAction>();
        StyxSerializer styxalizer = new StyxSerializer(new ProtocolObjectFactory());
        
        GameAction lastRequest = null;
        
        for (GameAction ga : actions) {
            if (ga instanceof GameDataAction) {
                GameDataAction gda = (GameDataAction) ga;
                ProtocolObject packet;
                try {
                    packet = styxalizer.unpack(gda.getData());
                    
                    if (packet instanceof RequestAction) {
                    	lastRequest = ga;
						 
					} else if (packet instanceof PerformAction) {
						lastRequest = null;
						filteredActions.add(ga);
						
					} else {
						filteredActions.add(ga);
					}
                } catch (IOException e) {
                    log.error("error unpacking cached packet", e);
                }
            } else {
                filteredActions.add(ga);
            }
        }
        
        // If we have an unanswered request then add it last
        if (lastRequest != null) {
        	filteredActions.add(lastRequest);
        }
        
        return filteredActions;
    }
    
}

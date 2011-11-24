package com.cubeia.games.poker;

import java.io.IOException;
import java.util.Collection;
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
import com.cubeia.games.poker.cache.ActionContainer;
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
    	try {
	        ProtocolFactory protocolFactory = new ProtocolFactory();
	        int tableId = table.getId();
	        
	        log.debug("sending stored game actions to client, player id = {}", playerId);
	        List<GameAction> actions = new LinkedList<GameAction>();
	        actions.add(protocolFactory.createGameAction(new StartHandHistory(), playerId, tableId));
	        
	        Collection<ActionContainer> containers = actionCache.getPrivateAndPublicActions(tableId, playerId);
	        Collection<GameAction> actionsFromCache = filterRequestActions(containers, playerId);
	        actions.addAll(actionsFromCache);
	        
	        actions.add(protocolFactory.createGameAction(new StopHandHistory(), playerId, tableId));
	        
	        table.getNotifier().notifyPlayer(playerId, actions);
    	} catch (Exception e) {
    		log.error("Failed to create and send game state to player "+playerId, e);
    	}
    }

    
    
    /**
     * 1. Filter the game actions list by removing all GameDataActions containing RequestAction packets
     * that have been answered by a PerformAction.
     * 
     * 2. Remove all actions that are marked as excluded for this player id to avoid duplicates.
     * 
     * @param actions actions to filter
     * @param playerId, player id to check for exclusion.
     * @return new filtered list
     * @throws IOException 
     */
    @VisibleForTesting
    protected List<GameAction> filterRequestActions(Collection<ActionContainer> actions, int playerId) throws IOException {
        LinkedList<GameAction> filteredActions = new LinkedList<GameAction>();
        StyxSerializer styxalizer = new StyxSerializer(new ProtocolObjectFactory());
        
        ActionContainer lastContainer = null; 
        RequestAction lastRequest = null;
        
        for (ActionContainer container : actions) {
        	if (container.getExcludedPlayerId() != null && container.getExcludedPlayerId() == playerId) {
        		continue; // Exclude this action from the list
        	}
        	
        	GameAction ga = container.getGameAction();
            if (ga instanceof GameDataAction) {
                GameDataAction gda = (GameDataAction) ga;
                ProtocolObject packet;
                try {
                    packet = styxalizer.unpack(gda.getData());
                    
                    if (packet instanceof RequestAction) {
                    	lastRequest = (RequestAction)packet;
                    	lastContainer = container;
						 
					} else if (packet instanceof PerformAction) {
						lastRequest = null;
						lastContainer = null;
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
        
        // If we have an unanswered request then adjust the time left to act and add it last
        if (lastRequest != null) {
        	GameDataAction requestAction = adjustTimeToAct(styxalizer, lastContainer, lastRequest);
        	filteredActions.add(requestAction);
        }
        
        return filteredActions;
    }

	private GameDataAction adjustTimeToAct(StyxSerializer styxalizer, ActionContainer lastContainer, RequestAction lastRequest) throws IOException {
		long elapsed = System.currentTimeMillis() - lastContainer.getTimestamp();
		int timeToAct = lastRequest.timeToAct - (int)elapsed;
		if (timeToAct < 0) {
			timeToAct = 0;
		}
		lastRequest.timeToAct = timeToAct;
		GameDataAction requestAction = (GameDataAction)lastContainer.getGameAction();
		requestAction.setData(styxalizer.pack(lastRequest));
		return requestAction;
	}
    
}

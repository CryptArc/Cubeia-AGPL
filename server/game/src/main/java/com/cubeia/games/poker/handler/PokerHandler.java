package com.cubeia.games.poker.handler;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.games.poker.FirebaseState;
import com.cubeia.games.poker.adapter.ActionTransformer;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.google.inject.Inject;

public class PokerHandler extends DefaultPokerHandler {

    private static transient Logger log = Logger.getLogger(PokerHandler.class);
    
	int playerId;
	
	@Inject
    Table table;
	
	@Inject
	PokerState state;
	
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public void visit(PerformAction packet) {
	    if (verifySequence(packet)) {
	    	TimeoutCache.getInstance().removeTimeout(table.getId(), playerId, table.getScheduler());
	        PokerAction action = new PokerAction(playerId, ActionTransformer.transform(packet.action.type));
	        action.setBetAmount(packet.betAmount);
	        state.act(action);
	    } 
	}


    private boolean verifySequence(PerformAction packet) {
        FirebaseState fbState = (FirebaseState)state.getAdapterState();
        int current = fbState.getCurrentRequestSequence();
        if (current >= 0 && current == packet.seq) {
            return true;
            
        } else {
            log.debug("Ignoring action. current-seq["+current+"] packet-seq["+packet.seq+"] - packet["+packet+"]");
            return false;
        }
        
    }
    
    public boolean verifySequence(Trigger command) {
        FirebaseState fbState = (FirebaseState)state.getAdapterState();
        int current = fbState.getCurrentRequestSequence();
        if (current == command.getSeq()) {
            return true;
            
        } else {
            log.warn("Ignoring scheduled command, current-seq["+current+"] command-seq["+command.getSeq()+"] - command["+command+"] state["+state+"]");
            return false;
        }
    }


	
	
}

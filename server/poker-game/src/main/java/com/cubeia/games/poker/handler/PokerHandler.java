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

package com.cubeia.games.poker.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoRequest;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoResponse;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInRequest;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInResponse;
import se.jadestone.dicearena.game.poker.network.protocol.Enums;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitinRequest;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitoutRequest;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.FirebaseState;
import com.cubeia.games.poker.adapter.ActionTransformer;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.google.inject.Inject;

public class PokerHandler extends DefaultPokerHandler {

    private static Logger log = LoggerFactory.getLogger(PokerHandler.class);
    
	int playerId;
	
	@Inject
    Table table;
	
	@Inject
	PokerState state;
	
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	@Override
	public void visit(PerformAction packet) {
	    if (verifySequence(packet)) {
	    	TimeoutCache.getInstance().removeTimeout(table.getId(), playerId, table.getScheduler());
	        PokerAction action = new PokerAction(playerId, ActionTransformer.transform(packet.action.type));
	        action.setBetAmount(packet.betAmount);
	        state.act(action);
	    } 
	}
	
	// player wants to sit out next hand
    @Override
	public void visit(PlayerSitoutRequest packet) {
		state.playerIsSittingOut(playerId);
	}

	// player wants to sit in again
    @Override
	public void visit(PlayerSitinRequest packet) {
		state.playerIsSittingIn(playerId);
	}
	
	@Override
	public void visit(BuyInInfoRequest packet) {
	    // TODO: implement!
	    
	    log.warn("SENDING MOCKED BUY IN INFO RESPONSE!!!");
	    
	    BuyInInfoResponse resp = new BuyInInfoResponse();
	    resp.balanceInWallet = 500000;
	    resp.balanceOnTable = 0;
	    resp.maxAmount = 330000;
	    resp.minAmount = 500;
	    
	    GameDataAction gda = new GameDataAction(playerId, table.getId());
	    
	    StyxSerializer styx = new StyxSerializer(null);
	    try {
            gda.setData(styx.pack(resp));
        } catch (IOException e) {
            e.printStackTrace();
        }
	    
	    table.getNotifier().notifyPlayer(playerId, gda);
	}
	
	@Override
	public void visit(BuyInRequest packet) {
        log.warn("SENDING MOCKED BUY IN RESPONSE!!!");
	    
        BuyInResponse resp = new BuyInResponse();
        resp.balance = packet.amount;
        resp.resultCode = Enums.BuyInResultCode.OK;
        
        GameDataAction gda = new GameDataAction(playerId, table.getId());
        
        StyxSerializer styx = new StyxSerializer(null);
        try {
            gda.setData(styx.pack(resp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (packet.sitInIfSuccessful) {
            state.playerIsSittingIn(playerId);
        }
        
        GenericPlayer player = table.getPlayerSet().getPlayer(playerId);
        log.debug("player id: {}, player: {}", playerId, player);
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

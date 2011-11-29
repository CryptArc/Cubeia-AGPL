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

package com.cubeia.games.poker.client;

import se.jadestone.dicearena.game.poker.network.protocol.AamsSessionInfoPacket;
import se.jadestone.dicearena.game.poker.network.protocol.BestHand;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoRequest;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoResponse;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInRequest;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInResponse;
import se.jadestone.dicearena.game.poker.network.protocol.CardToDeal;
import se.jadestone.dicearena.game.poker.network.protocol.DealPrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.DealPublicCards;
import se.jadestone.dicearena.game.poker.network.protocol.DealerButton;
import se.jadestone.dicearena.game.poker.network.protocol.DeckInfo;
import se.jadestone.dicearena.game.poker.network.protocol.ErrorPacket;
import se.jadestone.dicearena.game.poker.network.protocol.ExposePrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.GameCard;
import se.jadestone.dicearena.game.poker.network.protocol.HandCanceled;
import se.jadestone.dicearena.game.poker.network.protocol.HandEnd;
import se.jadestone.dicearena.game.poker.network.protocol.PacketVisitor;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerBalance;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerPokerStatus;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitinRequest;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitoutRequest;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerState;
import se.jadestone.dicearena.game.poker.network.protocol.Pot;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfer;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfers;
import se.jadestone.dicearena.game.poker.network.protocol.RakeInfo;
import se.jadestone.dicearena.game.poker.network.protocol.RequestAction;
import se.jadestone.dicearena.game.poker.network.protocol.StartHandHistory;
import se.jadestone.dicearena.game.poker.network.protocol.StartNewHand;
import se.jadestone.dicearena.game.poker.network.protocol.StopHandHistory;
import se.jadestone.dicearena.game.poker.network.protocol.TakeBackUncalledBet;
import se.jadestone.dicearena.game.poker.network.protocol.TournamentOut;

import com.cubeia.firebase.clients.java.connector.text.IOContext;

public class ManualGameHandler implements PacketVisitor {

	private final IOContext context;
	
	public ManualGameHandler(IOContext context) {
		this.context = context;
	}

	public void visit(DealerButton packet) {
		System.out.println("Player["+packet.seat+"] is dealer");
	}
	
	public void visit(DealPublicCards packet) {
		System.out.println("Public cards dealt: "+packet.cards);
	}

	public void visit(DealPrivateCards packet) {
		for (CardToDeal card : packet.cards) {
			card.accept(this);
		}
	}

	public void visit(StartNewHand packet) {
		System.out.println("Start a new hand. Dealer: "+packet.dealerSeatId);
	}

	public void visit(ExposePrivateCards packet) {
		for (CardToDeal card : packet.cards) { 
			System.out.println("Player "+card.player+" shows: "+card);
		}
	}

	public void visit(HandEnd packet) {
		String out = "\nHand over. Hands:\n";
		for (BestHand hand : packet.hands) {
			out += "\t"+hand.player +" - "+hand+" ("+hand.handType+")\n";
		}
		System.out.println(out);
	}
	
	public void visit(RequestAction packet) {
		if (packet.player == context.getPlayerId()) {
			System.out.println("I was requested to do something: "+packet.allowedActions);
			PokerTextClient.seq = packet.seq;
		} else {
			System.out.println("Player["+packet.player+"] was requested to act.");
		}
	}
	
	public void visit(PerformAction packet) {
		if (packet.player == context.getPlayerId()) {
			// System.out.println("I acted with: "+packet.action.type.name()+"  bet: "+packet.bet);
		} else {
			System.out.println("Player["+packet.player+"] acted: "+packet.action.type.name()+"  bet: "+packet.betAmount);
		}
	}
	
	public void visit(StartHandHistory packet) {
        System.out.println("-- Start History");
    }

    public void visit(StopHandHistory packet) {
        System.out.println("-- Stop History");
    }
	
    public void visit(TournamentOut packet) {
        System.out.println("Player: "+packet.player+" was out of tournament");
    }
    
    public void visit(PlayerBalance packet) {
        System.out.println("I got balance: "+packet.balance+", pending: "+packet.pendingBalance);
    }
    
    @Override
	public void visit(CardToDeal packet) {
    	if (packet.player == context.getPlayerId()) {
    		System.out.println("I was dealt: "+packet.card);
    	} else {
    		System.out.println("Player["+packet.player+"] was dealt: "+packet.card);
    	}
	}
    
    @Override
	public void visit(PlayerPokerStatus packet) {
		if (packet.player == context.getPlayerId()) {
			System.out.println("My status has changed to: "+packet.status);
		} else {
			System.out.println("Player["+packet.player+"]'s status has changed to: "+packet.status);
		}
	}
    
    @Override
	public void visit(BuyInInfoResponse packet) {
    	System.out.println("Buy in info - min["+packet.minAmount+"] max["+packet.maxAmount+"] mandatory["+packet.mandatoryBuyin+"]");
    }

	public void visit(GameCard packet) {}
	public void visit(BestHand packet) {}
	public void visit(PlayerState packet) {}
	public void visit(PlayerAction packet) {}
    public void visit(Pot packet) {}
	@Override
	public void visit(PlayerSitinRequest packet) {}
	@Override
	public void visit(PlayerSitoutRequest packet) {}
	@Override
	public void visit(AamsSessionInfoPacket packet) {}
	@Override
	public void visit(HandCanceled packet) {}
	@Override
	public void visit(BuyInInfoRequest packet) {}
	@Override
	public void visit(BuyInRequest packet) {}
	@Override
	public void visit(BuyInResponse packet) {}
	@Override
	public void visit(PotTransfer packet) {}
	@Override
	public void visit(PotTransfers packet) {}
	@Override
	public void visit(RakeInfo packet) {}
	@Override
	public void visit(DeckInfo packet) {}
	@Override
	public void visit(ErrorPacket packet) {}
	@Override
	public void visit(TakeBackUncalledBet packet) {}
    
}

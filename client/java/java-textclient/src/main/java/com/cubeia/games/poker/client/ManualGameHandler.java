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

import com.cubeia.firebase.clients.java.connector.text.IOContext;
import com.cubeia.games.poker.io.protocol.*;

public class ManualGameHandler implements PacketVisitor {

    private final IOContext context;

    private PokerFormatter formatter = new PokerFormatter();

    public ManualGameHandler(IOContext context) {
        this.context = context;
    }

    public void visit(DealerButton packet) {
        System.out.println("Player[" + packet.seat + "] is dealer");
    }

    public void visit(DealPublicCards packet) {
        System.out.println("Public cards dealt: " + formatter.format(packet.cards));
    }

    public void visit(DealPrivateCards packet) {
        for (CardToDeal card : packet.cards) {
            card.accept(this);
        }
    }

    public void visit(StartNewHand packet) {
        System.out.println("Start a new hand. Dealer: " + packet.dealerSeatId);
    }

    public void visit(ExposePrivateCards packet) {
        for (CardToDeal card : packet.cards) {
            System.out.println("Player " + card.player + " shows: " + formatter.format(card.card));
        }
    }

    public void visit(HandEnd packet) {
        String out = "\nHand over. Hands:\n";
        for (BestHand hand : packet.hands) {
            out += "\t" + hand.player + " - " + hand + " (" + hand.handType + ")\n";
        }
        System.out.println(out);
    }

    public void visit(RequestAction packet) {
        if (packet.player == context.getPlayerId()) {
            System.out.println("I was requested to do something: " + packet.allowedActions);
            PokerTextClient.seq = packet.seq;
        } else {
            System.out.println("Player[" + packet.player + "] was requested to act.");
        }
    }

    public void visit(PerformAction packet) {
        if (packet.player == context.getPlayerId()) {
            // System.out.println("I acted with: "+packet.action.type.name()+"  bet: "+packet.bet);
        } else {
            System.out.println("Player[" + packet.player + "] acted: " + packet.action.type.name() + "  bet: " + packet.betAmount);
        }
    }

    public void visit(StartHandHistory packet) {
        System.out.println("-- Start History");
    }

    public void visit(StopHandHistory packet) {
        System.out.println("-- Stop History");
    }

    public void visit(TournamentOut packet) {
        System.out.println("Player: " + packet.player + " was out of tournament");
    }

    public void visit(PlayerBalance packet) {
        if (packet.player == context.getPlayerId()) {
            System.out.println("I got balance: " + packet.balance + ", pending: " + packet.pendingBalance);
        } else {
            System.out.println("Player[" + packet.player + "] balance: " + packet.balance);
        }
    }

    @Override
    public void visit(CardToDeal packet) {
        if (packet.player == context.getPlayerId()) {
            System.out.println("I was dealt: " + formatter.format(packet.card));
        } else {
            System.out.println("Player[" + packet.player + "] was dealt: " + formatter.format(packet.card));
        }
    }

    @Override
    public void visit(PlayerPokerStatus packet) {
        if (packet.player == context.getPlayerId()) {
            System.out.println("My status has changed to: " + packet.status);
        } else {
            System.out.println("Player[" + packet.player + "]'s status has changed to: " + packet.status);
        }
    }

    @Override
    public void visit(BuyInInfoResponse packet) {
        System.out.println("Buy in info - min[" + packet.minAmount + "] max[" + packet.maxAmount + "] mandatory[" + packet.mandatoryBuyin + "]");
    }

    public void visit(GameCard packet) {
    }

    public void visit(BestHand packet) {
    }

    public void visit(PlayerState packet) {
    }

    public void visit(PlayerAction packet) {
    }

    public void visit(Pot packet) {
    }

    @Override
    public void visit(PlayerSitinRequest packet) {
    }

    @Override
    public void visit(PlayerSitoutRequest packet) {
    }

    @Override
    public void visit(HandCanceled packet) {
    }

    @Override
    public void visit(BuyInInfoRequest packet) {
    }

    @Override
    public void visit(BuyInRequest packet) {
    }

    @Override
    public void visit(BuyInResponse packet) {
    }

    @Override
    public void visit(PotTransfer packet) {
    }

    @Override
    public void visit(PotTransfers packet) {
    }

    @Override
    public void visit(RakeInfo packet) {
    }

    @Override
    public void visit(DeckInfo packet) {
    }

    @Override
    public void visit(ErrorPacket packet) {
    }

    @Override
    public void visit(TakeBackUncalledBet packet) {
    }

    @Override
    public void visit(ExternalSessionInfoPacket packet) {
    }

    @Override
    public void visit(FuturePlayerAction packet) {
    }

    @Override
    public void visit(InformFutureAllowedActions packet) {
    }

    @Override
    public void visit(PlayerHandStartStatus packet) {
    }

    @Override
    public void visit(PlayerDisconnectedPacket packet) {
    }

    @Override
    public void visit(PlayerReconnectedPacket packet) {
    }

    @Override
    public void visit(PingPacket packet) {
    }

    @Override
    public void visit(PongPacket packet) {
    }

}

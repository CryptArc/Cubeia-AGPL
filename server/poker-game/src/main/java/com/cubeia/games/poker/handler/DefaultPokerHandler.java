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
import se.jadestone.dicearena.game.poker.network.protocol.ExternalSessionInfoPacket;
import se.jadestone.dicearena.game.poker.network.protocol.FuturePlayerAction;
import se.jadestone.dicearena.game.poker.network.protocol.GameCard;
import se.jadestone.dicearena.game.poker.network.protocol.HandCanceled;
import se.jadestone.dicearena.game.poker.network.protocol.HandEnd;
import se.jadestone.dicearena.game.poker.network.protocol.InformFutureAllowedActions;
import se.jadestone.dicearena.game.poker.network.protocol.PacketVisitor;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerBalance;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerHandStartStatus;
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

public class DefaultPokerHandler implements PacketVisitor {
	
	public void visit(PerformAction packet) {}
	
	// -----  TO CLIENTS
	@Override
	public void visit(GameCard packet) {}
	@Override
	public void visit(DealPublicCards packet) {}
	@Override
	public void visit(DealPrivateCards packet) {}
	@Override
	public void visit(StartNewHand packet) {}
	@Override
	public void visit(ExposePrivateCards packet) {}
	@Override
	public void visit(HandEnd packet) {}
	@Override
	public void visit(BestHand packet) {}
	@Override
	public void visit(PlayerState packet) {}
	@Override
	public void visit(PlayerAction packet) {}
	@Override
	public void visit(RequestAction packet) {}
	@Override
	public void visit(DealerButton packet) {}
	@Override
    public void visit(StartHandHistory packet) {}
    @Override
    public void visit(StopHandHistory packet) {}
    @Override
    public void visit(TournamentOut packet) {}
    @Override
	public void visit(PlayerBalance packet) {}
    @Override
    public void visit(Pot packet) {}
	@Override
	public void visit(PlayerSitinRequest packet) {}
	@Override
	public void visit(PlayerPokerStatus packet) {}
	@Override
	public void visit(PlayerSitoutRequest packet) {}
	@Override
	public void visit(CardToDeal packet) {}
	@Override
	public void visit(ExternalSessionInfoPacket packet) {}
	@Override
	public void visit(DeckInfo packet) {}
    @Override
    public void visit(PotTransfer packet) {}
    @Override
    public void visit(PotTransfers packet) {}
    @Override
    public void visit(BuyInInfoRequest packet) {}
    @Override
    public void visit(BuyInInfoResponse packet) {}
    @Override
    public void visit(BuyInRequest packet) {}
    @Override
    public void visit(BuyInResponse packet) {}
    @Override
    public void visit(HandCanceled packet) {}
    @Override
    public void visit(RakeInfo packet) {}
	@Override
	public void visit(ErrorPacket packet) {}
	@Override
	public void visit(TakeBackUncalledBet packet) {}
	@Override
	public void visit(FuturePlayerAction packet) {}
	@Override
	public void visit(InformFutureAllowedActions packet) {}
	@Override
	public void visit(PlayerHandStartStatus packet) {}
}

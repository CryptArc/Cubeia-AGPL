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

import com.cubeia.games.poker.io.protocol.AddOnOffer;
import com.cubeia.games.poker.io.protocol.BestHand;
import com.cubeia.games.poker.io.protocol.BlindsAreUpdated;
import com.cubeia.games.poker.io.protocol.BlindsLevel;
import com.cubeia.games.poker.io.protocol.BlindsStructure;
import com.cubeia.games.poker.io.protocol.BuyInInfoRequest;
import com.cubeia.games.poker.io.protocol.BuyInInfoResponse;
import com.cubeia.games.poker.io.protocol.BuyInRequest;
import com.cubeia.games.poker.io.protocol.BuyInResponse;
import com.cubeia.games.poker.io.protocol.CardToDeal;
import com.cubeia.games.poker.io.protocol.ChipStatistics;
import com.cubeia.games.poker.io.protocol.DealPrivateCards;
import com.cubeia.games.poker.io.protocol.DealPublicCards;
import com.cubeia.games.poker.io.protocol.DealerButton;
import com.cubeia.games.poker.io.protocol.DeckInfo;
import com.cubeia.games.poker.io.protocol.ErrorPacket;
import com.cubeia.games.poker.io.protocol.ExposePrivateCards;
import com.cubeia.games.poker.io.protocol.ExternalSessionInfoPacket;
import com.cubeia.games.poker.io.protocol.FuturePlayerAction;
import com.cubeia.games.poker.io.protocol.GameCard;
import com.cubeia.games.poker.io.protocol.GameState;
import com.cubeia.games.poker.io.protocol.HandCanceled;
import com.cubeia.games.poker.io.protocol.HandEnd;
import com.cubeia.games.poker.io.protocol.HandStartInfo;
import com.cubeia.games.poker.io.protocol.InformFutureAllowedActions;
import com.cubeia.games.poker.io.protocol.LevelInfo;
import com.cubeia.games.poker.io.protocol.PacketVisitor;
import com.cubeia.games.poker.io.protocol.Payout;
import com.cubeia.games.poker.io.protocol.PayoutInfo;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.io.protocol.PerformAddOn;
import com.cubeia.games.poker.io.protocol.PingPacket;
import com.cubeia.games.poker.io.protocol.PlayerAction;
import com.cubeia.games.poker.io.protocol.PlayerBalance;
import com.cubeia.games.poker.io.protocol.PlayerDisconnectedPacket;
import com.cubeia.games.poker.io.protocol.PlayerHandStartStatus;
import com.cubeia.games.poker.io.protocol.PlayerPokerStatus;
import com.cubeia.games.poker.io.protocol.PlayerReconnectedPacket;
import com.cubeia.games.poker.io.protocol.PlayerSitinRequest;
import com.cubeia.games.poker.io.protocol.PlayerSitoutRequest;
import com.cubeia.games.poker.io.protocol.PlayerState;
import com.cubeia.games.poker.io.protocol.PlayersLeft;
import com.cubeia.games.poker.io.protocol.PongPacket;
import com.cubeia.games.poker.io.protocol.Pot;
import com.cubeia.games.poker.io.protocol.PotTransfer;
import com.cubeia.games.poker.io.protocol.PotTransfers;
import com.cubeia.games.poker.io.protocol.RakeInfo;
import com.cubeia.games.poker.io.protocol.RebuyOffer;
import com.cubeia.games.poker.io.protocol.RebuyResponse;
import com.cubeia.games.poker.io.protocol.RequestAction;
import com.cubeia.games.poker.io.protocol.RequestBlindsStructure;
import com.cubeia.games.poker.io.protocol.RequestPayoutInfo;
import com.cubeia.games.poker.io.protocol.RequestTournamentLobbyData;
import com.cubeia.games.poker.io.protocol.RequestTournamentPlayerList;
import com.cubeia.games.poker.io.protocol.RequestTournamentRegistrationInfo;
import com.cubeia.games.poker.io.protocol.RequestTournamentStatistics;
import com.cubeia.games.poker.io.protocol.RequestTournamentTable;
import com.cubeia.games.poker.io.protocol.StartHandHistory;
import com.cubeia.games.poker.io.protocol.StopHandHistory;
import com.cubeia.games.poker.io.protocol.TakeBackUncalledBet;
import com.cubeia.games.poker.io.protocol.TournamentDestroyed;
import com.cubeia.games.poker.io.protocol.TournamentInfo;
import com.cubeia.games.poker.io.protocol.TournamentLobbyData;
import com.cubeia.games.poker.io.protocol.TournamentOut;
import com.cubeia.games.poker.io.protocol.TournamentPlayer;
import com.cubeia.games.poker.io.protocol.TournamentPlayerList;
import com.cubeia.games.poker.io.protocol.TournamentRegistrationInfo;
import com.cubeia.games.poker.io.protocol.TournamentStatistics;
import com.cubeia.games.poker.io.protocol.TournamentTable;
import com.cubeia.games.poker.io.protocol.WaitingForPlayers;
import com.cubeia.games.poker.io.protocol.WaitingToStartBreak;

public class DefaultPokerHandler implements PacketVisitor {

    public void visit(PerformAction packet) {
    }

    // -----  TO CLIENTS
    @Override
    public void visit(GameCard packet) {
    }

    @Override
    public void visit(DealPublicCards packet) {
    }

    @Override
    public void visit(DealPrivateCards packet) {
    }

    @Override
    public void visit(HandStartInfo packet) {
    }

    @Override
    public void visit(ExposePrivateCards packet) {
    }

    @Override
    public void visit(HandEnd packet) {
    }

    @Override
    public void visit(BestHand packet) {
    }

    @Override
    public void visit(PlayerState packet) {
    }

    @Override
    public void visit(PlayerAction packet) {
    }

    @Override
    public void visit(RequestAction packet) {
    }

    @Override
    public void visit(DealerButton packet) {
    }

    @Override
    public void visit(StartHandHistory packet) {
    }

    @Override
    public void visit(StopHandHistory packet) {
    }

    @Override
    public void visit(TournamentOut packet) {
    }

    @Override
    public void visit(PlayerBalance packet) {
    }

    @Override
    public void visit(Pot packet) {
    }

    @Override
    public void visit(PlayerSitinRequest packet) {
    }

    @Override
    public void visit(PlayerPokerStatus packet) {
    }

    @Override
    public void visit(PlayerSitoutRequest packet) {
    }

    @Override
    public void visit(CardToDeal packet) {
    }

    @Override
    public void visit(ExternalSessionInfoPacket packet) {
    }

    @Override
    public void visit(DeckInfo packet) {
    }

    @Override
    public void visit(WaitingToStartBreak packet) {

    }

    @Override
    public void visit(WaitingForPlayers packet) {

    }

    @Override
    public void visit(BlindsAreUpdated packet) {

    }

    @Override
    public void visit(PotTransfer packet) {
    }

    @Override
    public void visit(PotTransfers packet) {
    }

    @Override
    public void visit(BuyInInfoRequest packet) {
    }

    @Override
    public void visit(BuyInInfoResponse packet) {
    }

    @Override
    public void visit(BuyInRequest packet) {
    }

    @Override
    public void visit(BuyInResponse packet) {
    }

    @Override
    public void visit(HandCanceled packet) {
    }

    @Override
    public void visit(RakeInfo packet) {
    }

    @Override
    public void visit(ErrorPacket packet) {
    }

    @Override
    public void visit(TakeBackUncalledBet packet) {
    }

    @Override
    public void visit(FuturePlayerAction packet) {
    }

    @Override
    public void visit(GameState packet) {

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

    @Override
    public void visit(RequestTournamentPlayerList packet) {

    }

    @Override
    public void visit(TournamentPlayerList packet) {

    }

    @Override
    public void visit(TournamentPlayer packet) {

    }

    @Override
    public void visit(RequestBlindsStructure packet) {

    }

    @Override
    public void visit(BlindsStructure packet) {

    }

    @Override
    public void visit(BlindsLevel packet) {

    }

    @Override
    public void visit(RequestPayoutInfo packet) {

    }

    @Override
    public void visit(PayoutInfo packet) {

    }

    @Override
    public void visit(Payout packet) {

    }

    @Override
    public void visit(RequestTournamentStatistics packet) {

    }

    @Override
    public void visit(ChipStatistics packet) {

    }

    @Override
    public void visit(LevelInfo packet) {

    }

    @Override
    public void visit(PlayersLeft packet) {

    }

    @Override
    public void visit(TournamentStatistics packet) {

    }

    @Override
    public void visit(TournamentInfo packet) {

    }

    @Override
    public void visit(RequestTournamentLobbyData packet) {

    }

    @Override
    public void visit(TournamentLobbyData packet) {

    }

    @Override
    public void visit(RequestTournamentTable packet) {

    }

    @Override
    public void visit(TournamentTable packet) {

    }

    @Override
    public void visit(RebuyOffer packet) {

    }

    @Override
    public void visit(TournamentDestroyed packet) {

    }

    @Override
    public void visit(RequestTournamentRegistrationInfo packet) {

    }

    @Override
    public void visit(TournamentRegistrationInfo packet) {

    }

    @Override
    public void visit(RebuyResponse packet) {
    }

    @Override
    public void visit(AddOnOffer packet) {

    }

    @Override
    public void visit(PerformAddOn packet) {

    }

}

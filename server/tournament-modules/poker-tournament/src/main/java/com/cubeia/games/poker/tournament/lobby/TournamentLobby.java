/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.tournament.lobby;

import com.cubeia.firebase.api.action.mtt.MttDataAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.MttNotifier;
import com.cubeia.firebase.api.mtt.model.MttPlayer;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.io.protocol.BlindsLevel;
import com.cubeia.games.poker.io.protocol.BlindsStructure;
import com.cubeia.games.poker.io.protocol.ChipStatistics;
import com.cubeia.games.poker.io.protocol.Enums;
import com.cubeia.games.poker.io.protocol.LevelInfo;
import com.cubeia.games.poker.io.protocol.Payout;
import com.cubeia.games.poker.io.protocol.PayoutInfo;
import com.cubeia.games.poker.io.protocol.PlayersLeft;
import com.cubeia.games.poker.io.protocol.TournamentInfo;
import com.cubeia.games.poker.io.protocol.TournamentLobbyData;
import com.cubeia.games.poker.io.protocol.TournamentPlayer;
import com.cubeia.games.poker.io.protocol.TournamentPlayerList;
import com.cubeia.games.poker.io.protocol.TournamentStatistics;
import com.cubeia.games.poker.io.protocol.TournamentTable;
import com.cubeia.games.poker.tournament.configuration.blinds.Level;
import com.cubeia.games.poker.tournament.configuration.payouts.Payouts;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;
import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.cubeia.firebase.api.mtt.model.MttPlayerStatus.OUT;
import static com.cubeia.games.poker.common.MoneyFormatter.format;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

/**
 * This class is responsible for serving tournament lobby data to clients, such as:
 * <p/>
 * - A list of players in the tournament (with chips, position and other data)
 * - The payout structure
 * - The blinds structure
 */
public class TournamentLobby {

    private static final Logger log = Logger.getLogger(TournamentLobby.class);

    private MttInstance instance;

    private StyxSerializer serializer;

    private MTTStateSupport state;

    private PokerTournamentState pokerState;

    private MttNotifier notifier;

    private SystemTime dateFetcher;

    public TournamentLobby(MttInstance instance, StyxSerializer serializer, MTTStateSupport state, PokerTournamentState pokerState, SystemTime dateFetcher) {
        this.instance = instance;
        this.serializer = serializer;
        this.dateFetcher = dateFetcher;
        this.notifier = instance.getMttNotifier();
        this.state = state;
        this.pokerState = pokerState;
    }

    public void sendPlayerListTo(int playerId) {
        sendPacketToPlayer(pokerState.getPlayerList(), playerId);
    }

    public void sendBlindsStructureTo(int playerId) {
        BlindsStructure packet = getBlindsStructurePacket();
        sendPacketToPlayer(packet, playerId);
    }

    public void sendPayoutInfoTo(int playerId) {
        PayoutInfo payoutInfo = createPayoutInfoPacket();
        sendPacketToPlayer(payoutInfo, playerId);
    }

    PayoutInfo createPayoutInfoPacket() {
        PayoutInfo payoutInfo = new PayoutInfo();
        List<Payout> payouts = newArrayList();
        Payouts payoutStructure = pokerState.getPayouts();
        for (int i = 1; i <= payoutStructure.getNumberOfPlacesInTheMoney(); i++) {
            payouts.add(new Payout(i, format(payoutStructure.getPayoutsForPosition(i))));
        }
        payoutInfo.payouts = payouts;
        payoutInfo.prizePool = pokerState.getPrizePool().intValue();
        return payoutInfo;
    }

    BlindsStructure getBlindsStructurePacket() {
        if (pokerState.getBlindsStructurePacket() == null) {
            pokerState.setBlindsStructurePacket(createBlindsStructurePacket());
        }
        return pokerState.getBlindsStructurePacket();
    }

    BlindsStructure createBlindsStructurePacket() {
        BlindsStructure packet = new BlindsStructure();
        List<BlindsLevel> list = newArrayList();
        for (Level level : pokerState.getBlindsStructure().getBlindsLevels()) {
            list.add(new BlindsLevel(
                    format(level.getSmallBlindAmount()),
                    format(level.getBigBlindAmount()),
                    format(level.getAnteAmount()),
                    level.isBreak(),
                    level.getDurationInMinutes()));
        }
        packet.blindsLevels = list;
        return packet;
    }

    private void sendPacketToPlayer(ProtocolObject packet, int playerId) {
        try {
            MttDataAction action = new MttDataAction(instance.getId(), playerId);
            action.setData(serializer.pack(packet));
            notifier.notifyPlayer(playerId, action);
        } catch (IOException e) {
            log.debug("Failed sending player list to player " + playerId, e);
        }
    }

    @VisibleForTesting
    TournamentPlayerList getPlayerList() {
        if (pokerState.getPlayerList() != null) {
            return pokerState.getPlayerList();
        }
        TournamentPlayerList list = new TournamentPlayerList();
        List<TournamentPlayer> players = newArrayList();
        int runningPosition = 0;
        int sharedPlaces = 1;
        long lastChipStack = -1;

        List<MttPlayer> sortedPlayers = sortPlayers(state.getPlayerRegistry().getPlayers());
        log.debug("Sorted players: " + Arrays.toString(sortedPlayers.toArray()));
        for (MttPlayer player : sortedPlayers) {
            log.debug("Player status: " + player.getStatus());
            int playerId = player.getPlayerId();
            long stackSize = pokerState.getPlayerBalance(playerId);

            // Deal with shared places. If two or more players have the same stack size, they share a place, the next guy will be on place
            // "shared + number of shared places" => I.e. 4. Adam (1500), 4. Ben (1500), 6. Caesar (1550)
            if (stackSize != lastChipStack) {
                runningPosition += sharedPlaces;
                sharedPlaces = 1;
            } else {
                sharedPlaces++;
            }

            int position = player.getStatus() == OUT ? player.getPosition() : runningPosition;

            players.add(new TournamentPlayer(player.getScreenname(), format(stackSize), position, format(getWinningsFor(playerId)), getTableFor(playerId)));
            lastChipStack = stackSize;
        }

        list.players = players;
        pokerState.setPlayerList(list);
        return list;
    }

    private int getTableFor(int playerId) {
        return pokerState.getTableFor(playerId, state);
    }

    private List<MttPlayer> sortPlayers(Collection<MttPlayer> players) {
        List<MttPlayer> list = newArrayList(players);
        sort(list, reverseOrder(new TournamentPlayerListComparator(pokerState)));
        return list;
    }

    private int getWinningsFor(int playerId) {
        return pokerState.getWinningsFor(pokerState.getTournamentPlayer(playerId, state));
    }

    public TournamentStatistics getTournamentStatistics() {
        if (pokerState.getTournamentStatistics() == null) {
            ChipStatistics chipStatistics = getChipStatistics();
            LevelInfo levelInfo = getLevelInfo();
            PlayersLeft playersLeft = getPlayerLeft();
            TournamentStatistics statistics = new TournamentStatistics(chipStatistics, levelInfo, playersLeft);
            pokerState.setTournamentStatistics(statistics);
        }
        return pokerState.getTournamentStatistics();
    }

    ChipStatistics getChipStatistics() {
        long smallestStack = Integer.MAX_VALUE;
        long biggestStack = 0;
        long totalChips = 0;
        double averageStack = 0;
        for (MttPlayer player : state.getPlayerRegistry().getPlayers()) {
            long chipStack = pokerState.getPlayerBalance(player.getPlayerId());
            if (chipStack == 0) {
                continue;
            }
            if (chipStack < smallestStack) {
                smallestStack = chipStack;
            }
            if (chipStack > biggestStack) {
                biggestStack = chipStack;
            }
            totalChips += chipStack;
        }
        if (smallestStack == Integer.MAX_VALUE) {
            smallestStack = 0;
        }
        if (state.getRemainingPlayerCount() > 0) {
            averageStack = totalChips / (double) state.getRemainingPlayerCount();
        }
        log.debug("Total chips: " + totalChips + " Players still in: " + state.getRemainingPlayerCount() + " Average: " + averageStack);
        return new ChipStatistics(format(smallestStack), format(biggestStack), format(averageStack));
    }

    private PlayersLeft getPlayerLeft() {
        return new PlayersLeft(state.getRemainingPlayerCount(), state.getPlayerRegistry().size());
    }

    private LevelInfo getLevelInfo() {
        return new LevelInfo(pokerState.getCurrentBlindsLevelNr() + 1, pokerState.getTimeToNextLevel(dateFetcher.date()));
    }

    public void sendTournamentLobbyDataTo(int playerId) {
        TournamentLobbyData lobbyData = new TournamentLobbyData();
        lobbyData.blindsStructure = getBlindsStructurePacket();
        lobbyData.payoutInfo = createPayoutInfoPacket();
        lobbyData.players = getPlayerList();
        lobbyData.tournamentStatistics = getTournamentStatistics();
        lobbyData.tournamentInfo = getTournamentInfo();
        sendPacketToPlayer(lobbyData, playerId);
    }

    private TournamentInfo getTournamentInfo() {
        TournamentInfo tournamentInfo = new TournamentInfo();
        tournamentInfo.buyIn = format(pokerState.getBuyInAsMoney().getAmount());
        tournamentInfo.fee = format(pokerState.getFeeAsMoney().getAmount());
        tournamentInfo.gameType = "No Limit Hold'em"; // TODO: Change when we actually support anything other than NL Hold'em..
        tournamentInfo.maxPlayers = state.getCapacity();
        tournamentInfo.minPlayers = state.getMinPlayers();
        tournamentInfo.startTime = String.valueOf(pokerState.getStartTime().getMillis());
        tournamentInfo.tournamentName = state.getName();
        tournamentInfo.tournamentStatus = convertTournamentStatus(pokerState.getStatus());
        return tournamentInfo;
    }

    public void sendTournamentTableTo(int playerId) {
        TournamentTable tournamentTable = new TournamentTable(pokerState.getTableFor(playerId, state));
        sendPacketToPlayer(tournamentTable, playerId);
    }

    Enums.TournamentStatus convertTournamentStatus(PokerTournamentStatus status) {
        return Enums.TournamentStatus.valueOf(status.name());
    }
}

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

import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.cache.ActionCache;
import com.cubeia.games.poker.io.protocol.*;
import com.cubeia.games.poker.io.protocol.Enums.BuyInResultCode;
import com.cubeia.games.poker.logic.TimeoutCache;
import com.cubeia.games.poker.model.PokerPlayerImpl;
import com.cubeia.games.poker.state.FirebaseState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.util.ThreadLocalProfiler;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.cubeia.backend.cashgame.dto.ReserveFailedResponse.ErrorCode.AMOUNT_TOO_HIGH;

/**
 * This class visits incoming packets and calls the appropriate method in the poker game.
 */
public class PokerHandler extends DefaultPokerHandler {

    private static final int SLOW_RESPONSE_TIME_MS = 100;

    private static Logger log = LoggerFactory.getLogger(PokerHandler.class);

    public int playerId;

    @Inject
    @VisibleForTesting
    ActionCache cache;

    @Inject
    @VisibleForTesting
    public Table table;

    @Inject
    @VisibleForTesting
    public PokerState state;

    @Inject
    @VisibleForTesting
    public ActionTransformer actionTransformer;

    @Inject
    @VisibleForTesting
    protected TimeoutCache timeoutCache;

    @Service
    @VisibleForTesting
    CashGamesBackendContract cashGameBackend;

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void visit(PerformAction packet) {
        if (verifySequence(packet)) {
            long start = System.currentTimeMillis();
            ThreadLocalProfiler.start();
            try {
                timeoutCache.removeTimeout(table.getId(), playerId, table.getScheduler());
                PokerAction action = new PokerAction(playerId, actionTransformer.transform(packet.action.type));
                action.setBetAmount(packet.betAmount);
                state.act(action);

            } finally {
                // Report profiling if slow
                long elapsed = System.currentTimeMillis() - start;
                if (elapsed > SLOW_RESPONSE_TIME_MS) {
                    ThreadLocalProfiler.stop();
                    log.warn("Slow response time detected. Perform Action took " + elapsed + "ms, (more than " + SLOW_RESPONSE_TIME_MS + " ms.) " +
                            "Packet: " + packet + "\n" +
                            ThreadLocalProfiler.getCallStackAsString());
                }
                ThreadLocalProfiler.clear();
            }
        }
    }

    // player wants to sit out next hand
    @Override
    public void visit(PlayerSitoutRequest packet) {
        state.playerSitsOut(playerId, SitOutStatus.SITTING_OUT);
    }

    // player wants to sit in again
    @Override
    public void visit(PlayerSitinRequest packet) {
        state.playerIsSittingIn(playerId);
    }

    @Override
    public void visit(BuyInInfoRequest packet) {
        state.notifyBuyinInfo(playerId, false);
    }

    @Override
    public void visit(BuyInRequest packet) {
        try {
            PokerPlayerImpl pokerPlayer = (PokerPlayerImpl) state.getPokerPlayer(playerId);
            if (pokerPlayer != null) {

                if (pokerPlayer.getPlayerSessionId() != null) {

                    // Check if the amount is allowed by the table
                    long sum = packet.amount + pokerPlayer.getBalance() + pokerPlayer.getBalanceNotInHand();
                    if (sum <= state.getMaxBuyIn() && sum >= state.getMinBuyIn()) {
                        state.handleBuyInRequest(pokerPlayer, packet.amount);

                        BuyInResponse buyInResponse = new BuyInResponse((int) pokerPlayer.getBalance(), (int) pokerPlayer.getPendingBalanceSum(), 0, BuyInResultCode.PENDING);
                        sendBuyInResponseToPlayer(pokerPlayer, buyInResponse);

                        // sit in the player
                        state.playerIsSittingIn(playerId);

                        // sit in the player when the buyin is done
                        pokerPlayer.setSitInAfterSuccessfulBuyIn(true);
                    } else {
                        ReserveFailedResponse failResponse = new ReserveFailedResponse(pokerPlayer.getPlayerSessionId(), AMOUNT_TOO_HIGH, "Requested buy in plus balance cannot be more than max buy in", false);

                        ReserveCallback callback = cashGameBackend.getCallbackFactory().createReserveCallback(table);
                        callback.requestFailed(failResponse);
                    }

                } else {
                    log.warn("PlayerSessionId was null when Poker Player tried to buy in. Table[" + table.getId() + "], Request[" + packet + "]");
                }
            } else {
                log.warn("Poker Player that was not found at table tried to buy in. Table[" + table.getId() + "], Request[" + packet + "]");
            }
        } catch (Exception e) {
            log.error("Buy in request failed, request[" + packet + "]", e);
        }
    }

    @Override
    public void visit(PingPacket packet) {
        try {
            PokerPlayerImpl pokerPlayer = (PokerPlayerImpl) state.getPokerPlayer(playerId);
            if (pokerPlayer != null) {
                sendPongToPlayer(pokerPlayer, packet.identifier);
            } else {
                log.warn("Poker Player that was not found at table tried to ping. Table[" + table.getId() + "], Request[" + packet + "]");
            }
        } catch (Exception e) {
            log.error("Ping request failed, request[" + packet + "],e");
        }
    }

    private void sendPongToPlayer(PokerPlayerImpl pokerPlayer, int identifier) throws IOException {
        log.debug("sending pong to player {}", pokerPlayer.getId());
        StyxSerializer styx = new StyxSerializer(null);
        PongPacket pongPacket = new PongPacket(identifier);
        GameDataAction gameDataAction = new GameDataAction(playerId, table.getId());
        gameDataAction.setData(styx.pack(pongPacket));
        table.getNotifier().sendToClient(pokerPlayer.getId(), gameDataAction);
    }

    private void sendBuyInResponseToPlayer(PokerPlayerImpl pokerPlayer, BuyInResponse buyInResponse) throws IOException {
        log.debug("sending buy in response to player {}: {}", pokerPlayer.getId(), buyInResponse);
        StyxSerializer styx = new StyxSerializer(null);
        GameDataAction gameDataAction = new GameDataAction(playerId, table.getId());
        gameDataAction.setData(styx.pack(buyInResponse));
        table.getNotifier().sendToClient(pokerPlayer.getId(), gameDataAction);
        if (cache != null) {
            /*
                * We're not adding this to the cache as it will never be removed when finished,
                * so if you reserve and get "pending" then leave the table and return, it will still
                * say "pending" in the client as you get the cache. By removing this in the cache
                * the other version will be true: if you have pending money and reconnect it will
                * not be shown in the client... /LJN
                */
            // cache.addPrivateAction(table.getTableId(), playerId, gameDataAction);
        }
    }

    private boolean verifySequence(PerformAction packet) {
        FirebaseState fbState = (FirebaseState) state.getAdapterState();
        int current = fbState.getCurrentRequestSequence();
        if (current >= 0 && current == packet.seq) {
            return true;

        } else {
            log.warn("Ignoring action. current-seq[" + current + "] packet-seq[" + packet.seq + "] - packet[" + packet + "]");
            return false;
        }

    }

    public boolean verifySequence(Trigger command) {
        FirebaseState fbState = (FirebaseState) state.getAdapterState();
        int current = fbState.getCurrentRequestSequence();
        if (current == command.getSeq()) {
            return true;

        } else {
            log.warn("Ignoring scheduled command, current-seq[" + current + "] command-seq[" + command.getSeq() + "] - command[" + command + "] state[" + state + "]");
            return false;
        }
    }


}

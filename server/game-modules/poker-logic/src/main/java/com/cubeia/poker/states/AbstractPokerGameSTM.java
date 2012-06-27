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

package com.cubeia.poker.states;

import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.GameType;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public abstract class AbstractPokerGameSTM implements PokerGameSTM {

    private static final long serialVersionUID = 1L;

    @VisibleForTesting
    StateChanger stateChanger;

    protected GameType gameType;

    protected PokerContext context;

    protected ServerAdapterHolder serverAdapterHolder;

    private static final Logger log = LoggerFactory.getLogger(AbstractPokerGameSTM.class);

    public AbstractPokerGameSTM(GameType gameType, PokerContext context, ServerAdapterHolder serverAdapter, StateChanger stateChanger) {
        this.gameType = gameType;
        this.stateChanger = stateChanger;
        this.context = context;
        this.serverAdapterHolder = serverAdapter;
    }

    protected AbstractPokerGameSTM() {

    }

    @Override
    public void enterState() {
    }

    @Override
    public void exitState() {
    }

    @Override
    public void timeout() {
        log.warn("Ignoring timeout in state " + this + " context: " + context);
    }

    @Override
    public void act(PokerAction action) {
        throw new IllegalStateException("PokerState: " + context + " Action: " + action);
    }

    @Override
    public String getStateDescription() {
        return getClass().getName();
    }

    @Override
    public boolean isCurrentlyWaitingForPlayer(int playerId) {
        return false;
    }

    @Override
    public void playerJoined(PokerPlayer player) {
    }

    @Override
    public boolean isPlayerInHand(int playerId) {
        return false;
    }

    @Override
    public void startHand() {
        log.warn("Won't start hand. Current state = " + this);
    }

    @Override
    public void playerSitsOut(int playerId, SitOutStatus status) {
        log.info("Player with id " + playerId + " sits out");
        if (context.setSitOutStatus(playerId, status)) {
            notifyPlayerSittingOut(playerId);
        }
    }

    @Override
    public void playerSitsIn(int playerId) {
        log.debug("player {} is sitting in", playerId);

        PokerPlayer player = context.getPokerPlayer(playerId);
        if (player == null) {
            log.warn("player {} not at table but tried to sit in. Ignoring.", playerId);
            return;
        }

        if (!player.isSittingOut()) {
            log.debug("sit in status has not changed");
            return;
        }

        if (gameType.canPlayerAffordEntryBet(player, context.getSettings(), true)) {
            log.debug("Player {} can afford ante. Sit in", player);

            player.sitIn();
            player.setSitOutNextRound(false);
            player.setSitInAfterSuccessfulBuyIn(false);
            notifyPlayerSittingIn(playerId);

            // This might start the game.
            playerJoined(player);
        } else {
            log.debug("player {} is out of cash, must bring more before joining", player);

            if (!player.isBuyInRequestActive() && player.getRequestedBuyInAmount() == 0L) {
                log.debug("player {} does not have buy in request active so notify buy in info", player);
                notifyBuyinInfo(playerId, true);
            }
        }
    }

    @Override
    public void performPendingBuyIns(Set<PokerPlayer> singleton) {
        log.debug("Not performing pending buy-ins as the current state does not think that's appropriate: " + this);
    }

    @Override
    public void playerOpenedSession(int playerId) {
        boolean enoughMoney = gameType.canPlayerAffordEntryBet(context.getPlayer(playerId), context.getSettings(), false);
        log.debug("Player {} opened session. Sending buy-in request if he doesn't have enough money for an entry bet: {}", playerId, enoughMoney);
        if (!enoughMoney) {
            getServerAdapterHolder().notifyBuyInInfo(playerId, false);
        }
    }

    private void notifyPlayerSittingIn(int playerId) {
        log.debug("notifyPlayerSittingIn() id: " + playerId + " status:" + PokerPlayerStatus.SITIN.name());
        boolean isInCurrentHand = context.isPlayerInHand(playerId);
        getServerAdapterHolder().notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITIN, isInCurrentHand);
    }

    private void notifyBuyinInfo(int playerId, boolean mandatoryBuyin) {
        getServerAdapterHolder().notifyBuyInInfo(playerId, mandatoryBuyin);
    }


    protected void doPerformPendingBuyIns(Set<PokerPlayer> players) {
        getServerAdapterHolder().performPendingBuyIns(players);
    }

    private void notifyPlayerSittingOut(int playerId) {
        log.debug("playerSitsOut() id: " + playerId + " status:" + PokerPlayerStatus.SITOUT.name());
        boolean isInCurrentHand = context.isPlayerInHand(playerId);
        getServerAdapterHolder().notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITOUT, isInCurrentHand);
    }

    protected void changeState(AbstractPokerGameSTM newState) {
        newState.context = context;
        newState.gameType = gameType;
        newState.serverAdapterHolder = serverAdapterHolder;
        newState.stateChanger = stateChanger;
        stateChanger.changeState(newState);
    }

    protected ServerAdapter getServerAdapterHolder() {
        return serverAdapterHolder.get();
    }

    protected void doStartHand() {
        context.setHandFinished(false);
        Collection<PokerPlayer> playersReadyToStartHand = getPlayersReadyToStartHand();
        if (playersReadyToStartHand.size() > 1) {
            context.prepareHand(getReadyPlayerFilter());

            notifyNewHand();
            notifyAllPlayerBalances();
            notifyAllHandStartPlayerStatus();

            gameType.prepareNewHand();
            gameType.startHand();

            changeState(new PlayingSTM());
        } else {
            log.warn("Not enough players to start hand: " + playersReadyToStartHand.size());
            changeState(new NotStartedSTM());
        }
    }

    protected Collection<PokerPlayer> getPlayersReadyToStartHand() {
        return context.getPlayersReadyToStartHand(getReadyPlayerFilter());
    }

    /**
     * Notify everyone about hand start status.
     */
    public void notifyAllHandStartPlayerStatus() {
        for (PokerPlayer player : context.getSeatedPlayers()) {
            if (player.isSittingOut()) {
                getServerAdapterHolder().notifyHandStartPlayerStatus(player.getId(), PokerPlayerStatus.SITOUT);
            } else {
                getServerAdapterHolder().notifyHandStartPlayerStatus(player.getId(), PokerPlayerStatus.SITIN);
            }
        }
    }

    public void notifyNewHand() {
        getServerAdapterHolder().notifyNewHand();
    }

    public void notifyAllPlayerBalances() {
        for (PokerPlayer player : context.getSeatedPlayers()) {
            notifyPlayerBalance(player);
        }
    }

    public void notifyPlayerBalance(PokerPlayer player) {
        getServerAdapterHolder().notifyPlayerBalance(player);
    }

    private Predicate<PokerPlayer> getReadyPlayerFilter() {
        return new Predicate<PokerPlayer>() {
            @Override
            public boolean apply(@Nullable PokerPlayer pokerPlayer) {
                boolean canAffordEntryBet = gameType.canPlayerAffordEntryBet(pokerPlayer, context.getSettings(), false);
                boolean isSittingIn = !pokerPlayer.isSittingOut();
                boolean buyInActive = pokerPlayer.isBuyInRequestActive();
                return canAffordEntryBet && isSittingIn && !buyInActive;
            }
        };
    }

    public boolean playerReadyToStartHand(PokerPlayer pokerPlayer) {
        return getReadyPlayerFilter().apply(pokerPlayer);
    }
}

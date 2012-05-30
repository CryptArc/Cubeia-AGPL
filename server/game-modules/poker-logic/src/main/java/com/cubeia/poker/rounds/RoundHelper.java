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

package com.cubeia.poker.rounds;

import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.timing.Periods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

public class RoundHelper implements Serializable{

    private PokerContext context;

    private ServerAdapterHolder serverAdapter;

    private static final Logger log = LoggerFactory.getLogger(RoundHelper.class);

    public RoundHelper(PokerContext context, ServerAdapterHolder serverAdapterHolder) {
        this.context = context;
        this.serverAdapter = serverAdapterHolder;
    }

    public void requestMultipleActions(Collection<ActionRequest> requests) {
        for (ActionRequest request : requests) {
            addTimeoutAndPotSize(request);
        }
        serverAdapter.get().requestMultipleActions(requests);
    }

    public void requestAction(ActionRequest request) {
        addTimeoutAndPotSize(request);
        log.debug("Send player action request [" + request + "]");
        serverAdapter.get().requestAction(request);
    }

    private void addTimeoutAndPotSize(ActionRequest request) {
        request.setTimeToAct(context.getTimingProfile().getTime(Periods.ACTION_TIMEOUT));
        request.setTotalPotSize(context.getTotalPotSize());
    }

    public void notifyPotSizeAndRakeInfo() {
        serverAdapter.get().notifyRakeInfo(context.calculateRakeInfo());
    }

    public void scheduleRoundTimeout(PokerContext context, ServerAdapter serverAdapter) {
        log.debug("scheduleRoundTimeout in: " + context.getTimingProfile().getTime(Periods.RIVER));
        serverAdapter.scheduleTimeout(context.getTimingProfile().getTime(Periods.RIVER));
    }

    public void setPlayerSitOut(PokerPlayer player, SitOutStatus status, PokerContext context, ServerAdapter serverAdapter) {
        int playerId = player.getId();
        context.setSitOutStatus(player.getId(), status);
        serverAdapter.notifyPlayerStatusChanged(playerId, PokerPlayerStatus.SITOUT, context.isPlayerInHand(playerId));
    }

    public void scheduleTimeoutForAutoAction() {
        serverAdapter.get().scheduleTimeout(context.getTimingProfile().getTime(Periods.AUTO_POST_BLIND_DELAY));
    }
}

package com.cubeia.poker.rounds;

import com.cubeia.poker.PokerContext;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.states.ServerAdapterHolder;
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

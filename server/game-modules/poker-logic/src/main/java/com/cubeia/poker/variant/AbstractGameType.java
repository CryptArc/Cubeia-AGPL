package com.cubeia.poker.variant;

import com.cubeia.poker.GameType;
import com.cubeia.poker.PokerContext;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.rounds.RoundHelper;
import com.cubeia.poker.sitout.SitoutCalculator;
import com.cubeia.poker.states.ServerAdapterHolder;
import com.cubeia.poker.timing.Periods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractGameType implements GameType {

    protected ServerAdapterHolder serverAdapterHolder;

    protected PokerContext context;

    protected RoundHelper roundHelper;

    private Collection<HandFinishedListener> handFinishedListeners = new HashSet<HandFinishedListener>();

    private static final Logger log = LoggerFactory.getLogger(AbstractGameType.class);

    /**
     * Expose all pocket cards for players still in the hand
     * i.e. not folded. Will set a flag so that sequential calls
     * will not generate any outgoing packets.
     */
    public void exposeShowdownCards() {
        if (context.countNonFoldedPlayers() > 1) {
            ExposeCardsHolder holder = new ExposeCardsHolder();
            for (PokerPlayer p : context.getPlayersInHand()) {
                if (!p.hasFolded() && !p.isExposingPocketCards()) {
                    // exposePrivateCards(p.getId(), p.getPrivatePocketCards());
                    holder.setExposedCards(p.getId(), p.getPrivatePocketCards());
                    p.setExposingPocketCards(true);
                }
            }

            if (holder.hasCards()) {
                exposePrivateCards(holder);
            }
        }
    }

    private void exposePrivateCards(ExposeCardsHolder holder) {
        getServerAdapter().exposePrivateCards(holder);
    }

    public boolean haveAllPlayersExposedCards() {
        if (context.countNonFoldedPlayers() > 1) {
            for (PokerPlayer p : context.getPlayersInHand()) {
                if (!p.hasFolded() && !p.isExposingPocketCards()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void requestMultipleActions(Collection<ActionRequest> requests) {
        for (ActionRequest request : requests) {
            request.setTimeToAct(context.getTimingProfile().getTime(Periods.ACTION_TIMEOUT));
            request.setTotalPotSize(context.getTotalPotSize());
        }
        getServerAdapter().requestMultipleActions(requests);
    }

    public void notifyPotAndRakeUpdates(Collection<PotTransition> potTransitions) {
        getServerAdapter().notifyPotUpdates(context.getPotHolder().getPots(), potTransitions);

        // notify all the new balances
        for (PokerPlayer player : context.getPlayersInHand()) {
            getServerAdapter().notifyPlayerBalance(player);
        }
        notifyRakeInfo();
    }

    public void notifyRakeInfo() {
        getServerAdapter().notifyRakeInfo(context.getPotHolder().calculateRakeIncludingBetStacks(context.getPlayersInHand()));
    }

    /**
     * Removes all disconnected players from the table
     */
    public void cleanupPlayers() {
        // Clean up players in states not accessible to the poker logic
        getServerAdapter().cleanupPlayers(new SitoutCalculator());
    }

    public void setLastPlayerToBeCalled(PokerPlayer lastPlayerToBeCalled) {
        context.setLastPlayerToBeCalled(lastPlayerToBeCalled);
    }

    public void notifyCommunityCards(List<Card> cards) {
        getServerAdapter().notifyCommunityCards(cards);
    }

    public void notifyPrivateCards(int playerId, List<Card> cards) {
        getServerAdapter().notifyPrivateCards(playerId, cards);
    }

    public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
        getServerAdapter().notifyPrivateExposedCards(playerId, cards);
    }

    public void notifyPlayerBalance(int playerId) {
        getServerAdapter().notifyPlayerBalance(context.getPokerPlayer(playerId));
    }

    public void notifyAllPlayerBalances() {
        for (PokerPlayer player : context.getSeatedPlayers()) {
            notifyPlayerBalance(player.getId());
        }
    }

    public void notifyTakeBackUncalledBets(int playerId, long amount) {
        getServerAdapter().notifyTakeBackUncalledBet(playerId, (int) amount);
    }

    /**
     * Notify everyone about hand start status.
     */
    public void notifyAllHandStartPlayerStatus() {
        for (PokerPlayer player : context.getSeatedPlayers()) {
            if (player.isSittingOut()) {
                getServerAdapter().notifyHandStartPlayerStatus(player.getId(), PokerPlayerStatus.SITOUT);
            } else {
                getServerAdapter().notifyHandStartPlayerStatus(player.getId(), PokerPlayerStatus.SITIN);
            }
        }
    }

    public ServerAdapter getServerAdapter() {
        return serverAdapterHolder.get();
    }

    @Override
    public void setPokerContextAndServerAdapter(PokerContext context, ServerAdapterHolder serverAdapterHolder) {
        this.context = context;
        this.serverAdapterHolder = serverAdapterHolder;
        this.roundHelper = new RoundHelper(context, serverAdapterHolder);
    }

    @Override
    public void addHandFinishedListener(HandFinishedListener handFinishedListener) {
        handFinishedListeners.add(handFinishedListener);
    }

    @Override
    public void removeHandFinishedListener(HandFinishedListener handFinishedListener) {
        handFinishedListeners.remove(handFinishedListener);
    }

    protected void notifyHandFinished(HandResult handResult, HandEndStatus status) {
        for (HandFinishedListener listener : handFinishedListeners) {
            listener.handFinished(handResult, status);
        }
    }
}
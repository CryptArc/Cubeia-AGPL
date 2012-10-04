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

package com.cubeia.games.poker.adapter;

import static com.cubeia.games.poker.adapter.HandHistoryTranslator.translate;
import static com.cubeia.games.poker.adapter.HandHistoryTranslator.translateCards;
import static com.cubeia.games.poker.lobby.PokerLobbyAttributes.TABLE_EXTERNAL_ID;
import static com.cubeia.poker.adapter.HandEndStatus.CANCELED_TOO_FEW_PLAYERS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TablePlayerSet;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.games.poker.entity.HandIdentifier;
import com.cubeia.games.poker.state.FirebaseState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.ExposedCards;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.handhistory.api.DeckInfo;
import com.cubeia.poker.handhistory.api.HandHistoryCollectorService;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HandIdentification;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.PlayerAction;
import com.cubeia.poker.handhistory.api.PlayerCardsDealt;
import com.cubeia.poker.handhistory.api.PlayerCardsExposed;
import com.cubeia.poker.handhistory.api.PotUpdate;
import com.cubeia.poker.handhistory.api.Results;
import com.cubeia.poker.handhistory.api.RoundStarted;
import com.cubeia.poker.handhistory.api.TableCardsDealt;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.util.ThreadLocalProfiler;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Lars J. Nilsson
 */
@Singleton
public class HandHistoryReporter {

    @Service
    private HandHistoryCollectorService service;

    @Inject
    private Table table;

    @Inject
    private PokerState state;

    public void notifyPotUpdates(Collection<Pot> iterable, Collection<PotTransition> potTransitions) {
        PotUpdate ev = new PotUpdate();
        ev.getPots().addAll(translate(iterable));
        post(ev);
    }

    public void notifyActionPerformed(PokerAction action, PokerPlayer pokerPlayer) {
        PlayerAction ev = translate(action);
        post(ev);
    }

    public void exposePrivateCards(ExposeCardsHolder holder) {
        for (ExposedCards exposedCards : holder.getExposedCards()) {
            PlayerCardsExposed ev = new PlayerCardsExposed(exposedCards.getPlayerId());
            ev.getCards().addAll(translateCards(exposedCards.getCards()));
            post(ev);
        }
    }

    public void notifyCommunityCards(List<Card> cards) {
        TableCardsDealt ev = new TableCardsDealt();
        ev.getCards().addAll(translateCards(cards));
        post(ev);
    }

    public void notifyDeckInfo(int size, Rank rankLow) {
        if (!checkHasService()) {
            return; // SANITY CHECK
        }
        service.reportDeckInfo(table.getId(), new DeckInfo(size, translate(rankLow)));
    }

    public void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus, Map<Integer, String> playerTransactions) {
        ThreadLocalProfiler.add("HandHistoryReportAdapter.notifyHandEnd.start");
        if (!checkHasService()) {
            return; // SANITY CHECK
        }
    	ThreadLocalProfiler.add("HandHistoryReportAdapter.notifyHandEnd.start");
        if (handEndStatus == CANCELED_TOO_FEW_PLAYERS) {
            service.cancelHand(table.getId());
        } else {
            Map<PokerPlayer, Result> map = handResult.getResults();
            Results res = new Results();
            for (PokerPlayer pl : map.keySet()) {
            	// translate results
            	com.cubeia.poker.handhistory.api.HandResult hr = translate(pl.getId(), map.get(pl));
                String transactionId = playerTransactions.get(pl.getId());
            	hr.setTransactionId(transactionId);
                res.getResults().put(pl.getId(), hr);
                // get player rake and add
                long playerRake = handResult.getRakeContributionByPlayer(pl);
                res.getResults().get(pl.getId()).setRake(playerRake);
            }
            res.setTotalRake(handResult.getTotalRake());
            service.reportResults(table.getId(), res);
            service.stopHand(table.getId());
        }
        ThreadLocalProfiler.add("HandHistoryReportAdapter.notifyHandEnd.stop");
    }

    public void notifyNewHand() {
        if (!checkHasService()) {
            return; // SANITY CHECK
        }
        List<Player> seats = getSeatsFromState();
        String tableExtId = getIntegrationTableId();
        String handExtId = getIntegrationHandId();
        HandIdentification id = new HandIdentification(table.getId(), tableExtId, handExtId);
        this.service.startHand(id, seats);
    }

    public void notifyNewRound() {
        post(new RoundStarted());
    }

    public void notifyPrivateCards(int playerId, List<Card> cards) {
        PlayerCardsDealt ev = new PlayerCardsDealt(playerId, false);
        ev.getCards().addAll(translateCards(cards));
        post(ev);
    }

    public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
        PlayerCardsDealt ev = new PlayerCardsDealt(playerId, true);
        ev.getCards().addAll(translateCards(cards));
        post(ev);
    }

    // --- PRIVATE METHODS --- //

    private FirebaseState getFirebaseState() {
        return (FirebaseState) state.getAdapterState();
    }

    private String getIntegrationHandId() {
        HandIdentifier id = getFirebaseState().getCurrentHandIdentifier();
        return (id == null ? null : id.getIntegrationId());
    }

    private String getIntegrationTableId() {
        return state.getSettings().getAttributes().get(TABLE_EXTERNAL_ID.name()).toString();
    }

    private void post(HandHistoryEvent ev) {
        if (!checkHasService()) return; // SANITY CHECK
        service.reportEvent(table.getId(), ev);
    }

    private boolean checkHasService() {
        if (this.service == null) {
            Logger.getLogger(getClass()).warn("No hand history collector service deployed!");
            return false;
        } else {
            return true;
        }
    }

    private List<Player> getSeatsFromState() {
        List<Player> list = new ArrayList<Player>(6);
        SortedMap<Integer, PokerPlayer> plyrs = state.getCurrentHandSeatingMap();
        for (int seat : plyrs.keySet()) {
            PokerPlayer pl = plyrs.get(seat);
            if (!pl.isSittingOut()) {
                String name = getPlayerName(pl.getId());
                Player p = new Player(pl.getId());
                p.setInitialBalance(pl.getBalance());
                p.setName(name);
                p.setSeatId(seat);
                list.add(p);
            }
        }
        return list;
    }

    private String getPlayerName(int id) {
        TablePlayerSet set = table.getPlayerSet();
        return set.getPlayer(id).getName();
    }
}

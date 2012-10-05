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

package com.cubeia.games.poker.activator;

import static com.cubeia.games.poker.lobby.PokerLobbyAttributes.TABLE_EXTERNAL_ID;
import static com.cubeia.games.poker.lobby.PokerLobbyAttributes.TABLE_TEMPLATE;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.firebase.CashGamesBackendContract;
import com.cubeia.backend.firebase.FirebaseCallbackFactory;
import com.cubeia.firebase.api.game.GameDefinition;
import com.cubeia.firebase.api.game.activator.DefaultCreationParticipant;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.lobby.LobbyPath;
import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.cubeia.games.poker.lobby.PokerLobbyAttributes;
import com.cubeia.games.poker.state.FirebaseState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.settings.BetStrategyName;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.GameType;
import com.cubeia.poker.variant.PokerVariant;
import com.cubeia.poker.variant.factory.GameTypeFactory;
import com.cubeia.poker.variant.telesina.TelesinaDeckUtil;


/**
 * Table Creator.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerParticipant extends DefaultCreationParticipant {

    private static final TelesinaDeckUtil TELESINA_DECK_UTIL = new TelesinaDeckUtil();

    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(PokerParticipant.class);

    public static final int GAME_ID = 1;

    private final String domain;
    private final PokerStateCreator stateCreator;
    private final CashGamesBackendContract cashGameBackendService;
    private final TableConfigTemplate template;

    private final TableNameManager tableNamer;

    public PokerParticipant(TableConfigTemplate template, String domain, PokerStateCreator stateCreator,
            CashGamesBackendContract cashGameBackendService, TableNameManager tableNamer) {
        this.domain = domain;
        this.template = template;
        this.stateCreator = stateCreator;
        this.cashGameBackendService = cashGameBackendService;
        this.tableNamer = tableNamer;
    }

    @Override
    public LobbyPath getLobbyPathForTable(Table table) {
        return new LobbyPath(GAME_ID, domain + "/" + template.getVariant().name());
    }

    @Override
    public void tableCreated(Table table, LobbyTableAttributeAccessor acc) {
        super.tableCreated(table, acc);
        PokerVariant variant = template.getVariant();

        // Create state.
        PokerState pokerState = stateCreator.newPokerState();
        GameType gameType = GameTypeFactory.createGameType(variant);
        PokerSettings settings = createSettings(table);
        pokerState.init(gameType, settings);
        pokerState.setAdapterState(new FirebaseState());
        pokerState.setTableId(table.getId());
        table.getGameState().setState(pokerState);

        // Set lobby attributes
        acc.setIntAttribute(TABLE_TEMPLATE.name(), template.getId());
        acc.setIntAttribute(PokerLobbyAttributes.VISIBLE_IN_LOBBY.name(), 0);
        acc.setStringAttribute(PokerLobbyAttributes.SPEED.name(), template.getTiming().name());
        acc.setIntAttribute(PokerLobbyAttributes.ANTE.name(), template.getAnte());
        acc.setIntAttribute(PokerLobbyAttributes.SMALL_BLIND.name(), settings.getSmallBlindAmount());
        acc.setIntAttribute(PokerLobbyAttributes.BIG_BLIND.name(), settings.getBigBlindAmount());
        acc.setStringAttribute(PokerLobbyAttributes.BETTING_GAME_BETTING_MODEL.name(), "NO_LIMIT");
        acc.setStringAttribute(PokerLobbyAttributes.MONETARY_TYPE.name(), "REAL_MONEY");
        acc.setStringAttribute(PokerLobbyAttributes.VARIANT.name(), variant.name());
        acc.setIntAttribute(PokerLobbyAttributes.MIN_BUY_IN.name(), pokerState.getMinBuyIn());
        acc.setIntAttribute(PokerLobbyAttributes.MAX_BUY_IN.name(), pokerState.getMaxBuyIn());
        int deckSize = TELESINA_DECK_UTIL.createDeckCards(pokerState.getTableSize()).size();
        acc.setIntAttribute(PokerLobbyAttributes.DECK_SIZE.name(), deckSize);

        // Announce table
        FirebaseCallbackFactory callbackFactory = cashGameBackendService.getCallbackFactory();
        AnnounceTableRequest announceRequest = new AnnounceTableRequest(table.getId());   // TODO: this should be the id from the table record
        cashGameBackendService.announceTable(announceRequest, callbackFactory.createAnnounceTableCallback(table));
    }

    private PokerSettings createSettings(Table table) {
        int minBuyIn = template.getAnte() * template.getMinBuyInMultiplier();
        int maxBuyIn = template.getAnte() * template.getMaxBuyInMultiplier();
        int seats = table.getPlayerSet().getSeatingMap().getNumberOfSeats();
        RakeSettings rake = new RakeSettings(template.getRakeFraction(), template.getRakeLimit(), template.getRakeHeadsUpLimit());
        BetStrategyName limit = BetStrategyName.NO_LIMIT;
        // Map<Serializable,Serializable> attributes = Collections.emptyMap();
        Map<Serializable, Serializable> attributes = Collections.<Serializable, Serializable>singletonMap(TABLE_EXTERNAL_ID.name(), "MOCK::" + table.getId());
        // TODO: Make this configurable.
        int smallBlindAmount = template.getAnte();
        int bigBlindAmount = 2 * smallBlindAmount;
        TimingProfile profile = TimingFactory.getRegistry().getTimingProfile(template.getTiming());
        return new PokerSettings(
                template.getAnte(),
                smallBlindAmount,
                bigBlindAmount,
                minBuyIn,
                maxBuyIn,
                profile,
                seats,
                limit,
                rake,
                attributes);
    }

    @Override
    public String getTableName(GameDefinition def, Table t) {
        return tableNamer.tableCreated(t);
    }

    public int getSeats() {
        return template.getSeats();
    }

    public CashGamesBackendContract getCashGameBackendService() {
        return cashGameBackendService;
    }

    public TableConfigTemplate getTemplate() {
        return template;
    }

    @Override
    public String toString() {
        return "PokerParticipant [domain=" + domain + ", template=" + template + "]";
    }
}

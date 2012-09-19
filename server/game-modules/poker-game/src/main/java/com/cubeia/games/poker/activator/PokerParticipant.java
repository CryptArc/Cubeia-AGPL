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

import static com.cubeia.games.poker.activator.PokerActivator.ATTR_EXTERNAL_TABLE_ID;

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
import com.cubeia.poker.rng.RNGProvider;
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
    private final RNGProvider rngProvider; // should be removed...
    private final PokerStateCreator stateCreator;
    private final CashGamesBackendContract cashGameBackendService;
    private final TableConfigTemplate template;
    
    public PokerParticipant(TableConfigTemplate template, String domain, PokerStateCreator stateCreator, RNGProvider rngProvider, CashGamesBackendContract cashGameBackendService) {
        this.domain = domain;
        this.template = template;
		this.stateCreator = stateCreator;
        this.cashGameBackendService = cashGameBackendService;
        this.rngProvider = rngProvider;
    }

    @Override
    public LobbyPath getLobbyPathForTable(Table table) {
        return new LobbyPath(GAME_ID, domain + "/" + template.getVariant().name());
    }

    @Override
    public void tableCreated(Table table, LobbyTableAttributeAccessor acc) {
        super.tableCreated(table, acc);
        PokerVariant variant = template.getVariant();
        // create state
        PokerState pokerState = stateCreator.newPokerState();
        GameType gameType = GameTypeFactory.createGameType(variant, rngProvider);
        PokerSettings settings = createSettings(table, variant);
        pokerState.init(gameType, settings);
        pokerState.setAdapterState(new FirebaseState());
        pokerState.setTableId(table.getId());
        table.getGameState().setState(pokerState);
        // set lobby attributes
        acc.setIntAttribute(PokerLobbyAttributes.VISIBLE_IN_LOBBY.name(), 0);
        acc.setStringAttribute(PokerLobbyAttributes.SPEED.name(), template.getTiming().name());
        acc.setIntAttribute(PokerLobbyAttributes.BETTING_GAME_ANTE.name(), template.getAnte());
        acc.setStringAttribute(PokerLobbyAttributes.BETTING_GAME_BETTING_MODEL.name(), "NO_LIMIT");
        acc.setStringAttribute(PokerLobbyAttributes.MONETARY_TYPE.name(), "REAL_MONEY");
        acc.setStringAttribute(PokerLobbyAttributes.VARIANT.name(), variant.name());
        acc.setIntAttribute(PokerLobbyAttributes.MIN_BUY_IN.name(), pokerState.getMinBuyIn());
        acc.setIntAttribute(PokerLobbyAttributes.MAX_BUY_IN.name(), pokerState.getMaxBuyIn());
        int deckSize = TELESINA_DECK_UTIL.createDeckCards(pokerState.getTableSize()).size();
        acc.setIntAttribute(PokerLobbyAttributes.DECK_SIZE.name(), deckSize);
        // announce table
        FirebaseCallbackFactory callbackFactory = cashGameBackendService.getCallbackFactory();
        AnnounceTableRequest announceRequest = new AnnounceTableRequest(table.getId());   // TODO: this should be the id from the table record
        cashGameBackendService.announceTable(announceRequest, callbackFactory.createAnnounceTableCallback(table));
    }

    private PokerSettings createSettings(Table table, PokerVariant variant) {
        int minBuyIn = template.getAnte() * template.getMinBuyInMultiplyer();
        int maxBuyIn = template.getAnte() * template.getMaxBuyInMultiplyer();
        int seats = table.getPlayerSet().getSeatingMap().getNumberOfSeats();
        RakeSettings rake = new RakeSettings(template.getRakeFraction(), template.getRakeLimit(), template.getRakeHeadsUpLimit());
        BetStrategyName limit = BetStrategyName.NO_LIMIT;
        Map<Serializable,Serializable> attributes = Collections.<Serializable, Serializable>singletonMap(ATTR_EXTERNAL_TABLE_ID, "MOCK::" + table.getId());
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
        return template.getVariant().name() + "<" + t.getId() + ">";
    }

    public int getSeats() {
        return template.getSeats();
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return "PokerParticipant [domain=" + domain + ", template=" + template + "]";
    }
}

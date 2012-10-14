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

package com.cubeia.games.poker.admin.wicket.pages.history;

import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.cubeia.games.poker.admin.Configuration;
import com.cubeia.games.poker.admin.service.history.HandHistoryService;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.admin.wicket.util.ExternalLinkPanel;
import com.cubeia.poker.handhistory.api.Amount;
import com.cubeia.poker.handhistory.api.GameCard;
import com.cubeia.poker.handhistory.api.GamePot;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HandResult;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.PlayerAction;
import com.cubeia.poker.handhistory.api.PlayerCardsDealt;
import com.cubeia.poker.handhistory.api.PlayerCardsExposed;
import com.cubeia.poker.handhistory.api.PotUpdate;
import com.cubeia.poker.handhistory.api.Results;
import com.cubeia.poker.handhistory.api.TableCardsDealt;

public class ShowHand extends BasePage {

    @SpringBean
    private HandHistoryService historyService;

    @SpringBean(name="webConfig")
    private Configuration config;
    
    public ShowHand(PageParameters parameters) {
        String handId = parameters.get("handId").toString();
        HistoricHand hand = historyService.findById(handId);
        addSummary(hand);
        addPlayerList(hand);
        addEvents(hand);
    }

    private void addPlayerList(final HistoricHand hand) {
        DataView<Player> players = new DataView<Player>("players", new ListDataProvider<Player>(hand.getSeats())) {

            private static final long serialVersionUID = 1908334758912501993L;

			@Override
            protected void populateItem(Item<Player> item) {
                Player player = item.getModelObject();
                Results results = hand.getResults();
                // item.add(new Label("playerName", player.getName()));
                item.add(new ExternalLinkPanel("playerName", player.getName(), createPlayerLink(player)));
                item.add(new Label("playerId", String.valueOf(player.getPlayerId())));
                item.add(new Label("seat", String.valueOf(player.getSeatId())));
                item.add(new Label("bet", formatAmount(results.getResults().get(player.getPlayerId()).getTotalBet())));
                String net = formatAmount(results.getResults().get(player.getPlayerId()).getNetWin());
                HandResult handResult = results.getResults().get(player.getPlayerId());
                if(handResult.getTransactionId() != null) {
                	item.add(new ExternalLinkPanel("net", net, createTransactionLink(handResult)));
                } else {
                	item.add(new Label("net", net));
                }
            }

			private String createTransactionLink(HandResult handResult) {
				String tmp = normalizeBaseUrl();
				tmp += "wicket/bookmarkable/com.cubeia.backoffice.web.wallet.TransactionInfo?transactionId=" + handResult.getTransactionId();
				return tmp;
			}

			private String createPlayerLink(Player player) {
				String tmp = normalizeBaseUrl();
				tmp += "wicket/bookmarkable/com.cubeia.backoffice.web.user.UserSummary?userId=" + player.getPlayerId();
				return tmp;
			}

			private String normalizeBaseUrl() {
				String tmp = config.getNetworkUrl();
				if(!tmp.endsWith("/")) {
					tmp += "/";
				}
				return tmp;
			}
        };
        add(players);
    }

    private void addSummary(HistoricHand hand) {
        add(new Label("handId", hand.getHandId().getHandId()));
        add(new Label("tableId", String.valueOf(hand.getHandId().getTableId())));
        add(new Label("tableIntegrationId", hand.getHandId().getTableIntegrationId()));
        add(new Label("startTime", new Date(hand.getStartTime()).toString()));
        add(new Label("endTime", new Date(hand.getEndTime()).toString()));
        add(new Label("totalRake", formatAmount(hand.getResults().getTotalRake())));
    }

    private void addEvents(final HistoricHand hand) {
        DataView<HandHistoryEvent> events = new DataView<HandHistoryEvent>("events", new ListDataProvider<HandHistoryEvent>(hand.getEvents())) {
            @Override
            protected void populateItem(Item<HandHistoryEvent> item) {
                HandHistoryEvent event = item.getModelObject();

                Model<String> action = new Model<String>();
                Model<String> amount = new Model<String>();
                Model<String> playerId = new Model<String>();
                CardList cards = new CardList("cards");

                item.add(new Label("type", event.getType()));
                item.add(new Label("time", new Date(event.getTime()).toString()));
                item.add(new Label("action", action));
                item.add(new Label("amount", amount));
                item.add(cards);
                item.add(new Label("playerId", playerId));

                if (event instanceof PlayerAction) {
                    PlayerAction playerAction = (PlayerAction) event;
                    action.setObject(playerAction.getAction() .toString());
                    playerId.setObject(String.valueOf(playerAction.getPlayerId()));
                    amount.setObject(formatAmount(playerAction.getAmount()));
                } else if (event instanceof PotUpdate) {
                    PotUpdate potUpdate = (PotUpdate) event;
                    amount.setObject(stringRepresentation(potUpdate));
                } else if (event instanceof PlayerCardsDealt) {
                    PlayerCardsDealt playerCards = (PlayerCardsDealt) event;
                    cards.setList(playerCards.getCards());
                    playerId.setObject(String.valueOf(playerCards.getPlayerId()));
                } else if (event instanceof TableCardsDealt) {
                    TableCardsDealt playerCards = (TableCardsDealt) event;
                    cards.setList(playerCards.getCards());
                } else if (event instanceof PlayerCardsExposed) {
                    PlayerCardsExposed exposed = (PlayerCardsExposed) event;
                    playerId.setObject(String.valueOf(exposed.getPlayerId()));
                    cards.setList(exposed.getCards());
                }
            }
        };
        add(events);
    }

    @Override
    public String getPageTitle() {
        return "Hand History";
    }

    private String formatAmount(Amount amount) {
        if (amount == null) {
            return "";
        }
        return formatAmount(amount.getAmount());
    }

    private String formatAmount(long amount) {
        if (amount % 100 == 0) {
            return String.valueOf(amount / 100);
        }
        return String.format("%.2f", amount / 100.0);
    }

    private String stringRepresentation(PotUpdate potUpdate) {
        String result = "";
        List<GamePot> pots = potUpdate.getPots();
        if (pots.size() > 0) {
            result += "Pot: " + formatAmount(pots.get(0).getPotSize());
        }
        for (int i = 1; i < pots.size(); i++) {
            result += "\nSide pot " + i + ": " + formatAmount(pots.get(i).getPotSize());
        }
        return result;
    }

    private class CardList extends ListView<GameCard> {

        public CardList(String id) {
            super(id);
        }

        @Override
        protected void populateItem(ListItem<GameCard> item) {
            GameCard card = item.getModelObject();
            String imageName = card.getRank().getAbbreviation() + card.getSuit().name().charAt(0);
            item.add(new ContextImage("cardImage", "images/cards/" + imageName + ".png"));
        }

    }
}

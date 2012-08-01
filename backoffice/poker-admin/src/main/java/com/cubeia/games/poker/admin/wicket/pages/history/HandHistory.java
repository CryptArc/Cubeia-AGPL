package com.cubeia.games.poker.admin.wicket.pages.history;

import com.cubeia.games.poker.admin.service.history.HandHistoryService;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.cubeia.games.poker.admin.wicket.BasePage;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Homepage
 */
public class HandHistory extends BasePage {

    private static final long serialVersionUID = 1L;

    private final AbstractAjaxBehavior historyList;

    private final AbstractAjaxBehavior hand;

    @SpringBean
    private HandHistoryService historyService;

    public HandHistory(final PageParameters parameters) {
        add(historyList = new AbstractAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.renderJavaScript(
                        "var historyUrl = '" + historyList.getCallbackUrl() + "';",
                        "callbackurl");
            }

            @Override
            public void onRequest() {
                RequestCycle requestCycle = RequestCycle.get();
                /*
                     * Request request = requestCycle.getRequest();
                     * IRequestParameters irp = request.getRequestParameters();
                     * StringValue state = irp.getParameterValue("term");
                     */

                requestCycle.scheduleRequestHandlerAfterCurrent(new TextRequestHandler(
                        "application/json", "UTF-8", historyService.findHandHistoryByPlayerId(1)));

            }

        });
        add(hand = new AbstractAjaxBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.renderJavaScript(
                        "var handUrl = '" + hand.getCallbackUrl() + "';",
                        "callbackurl");
            }

            @Override
            public void onRequest() {
                RequestCycle requestCycle = RequestCycle.get();

                requestCycle.scheduleRequestHandlerAfterCurrent(new TextRequestHandler(
                        "application/json", "UTF-8", getHand()));

            }

            private String getHand() {
                return "{ \"handId\" : { \"tableId\": 13, \"tableIntegrationId\" : \"MOCK::13\", \"handId\" : \"1343748958838\" },\"startTime\" : 1343748958838, \"endTime\" : 1343749016362, \"results\" : { \"totalRake\" : 40, \"results\": { \"1\" : { \"playerId\" : 1, \"netWin\" : -2000, \"totalWin\" : 0, \"rake\" :20, \"totalBet\" : 2000 }, \"2\" : { \"playerId\" : 2, \"netWin\" : 1960,\"totalWin\" : 3960, \"rake\" : 20, \"totalBet\" : 2000 } } }, \"events\" : [{\"action\" : \"SMALL_BLIND\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 100},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" :1343748958869},{\"action\" : \"BIG_BLIND\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748958876},{\"playerId\" : 1,\"cards\" : [{\"suit\" : \"CLUBS\",\"rank\" : \"THREE\"},{\"suit\" : \"SPADES\",\"rank\" : \"TWO\"}],\"isExposed\" : false,\"type\" : \"PlayerCardsDealt\",\"time\" : 1343748958878},{\"playerId\" : 2,\"cards\" : [{\"suit\" : \"HEARTS\",\"rank\" : \"QUEEN\"},{\"suit\" : \"SPADES\",\"rank\" : \"JACK\"}],\"isExposed\" : false,\"type\" : \"PlayerCardsDealt\",\"time\" : 1343748958880},{\"action\" : \"RAISE\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 400},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343748962248},{\"action\" : \"CALL\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748965310},{\"pots\" : [{\"potId\" : 0,\"players\" : [1,2],\"potSize\" : 800}],\"type\" : \"PotUpdate\",\"time\" : 1343748965312},{\"cards\" : [{\"suit\" : \"DIAMONDS\",\"rank\" : \"JACK\"},{\"suit\" : \"HEARTS\",\"rank\" : \"JACK\"},{\"suit\" : \"DIAMONDS\",\"rank\" : \"SEVEN\"}],\"type\" : \"TableCardsDealt\",\"time\" : 1343748965316},{\"action\" : \"CHECK\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 0},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748972500},{\"action\" : \"BET\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343748976754},{\"action\" : \"CALL\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748980358},{\"pots\" : [{\"potId\" : 0,\"players\" : [1,2],\"potSize\" : 1200}],\"type\" : \"PotUpdate\",\"time\" : 1343748980360},{\"cards\" : [{\"suit\" : \"SPADES\",\"rank\" : \"SEVEN\"}],\"type\" : \"TableCardsDealt\",\"time\" : 1343748980364},{\"action\" : \"CHECK\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 0},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748986620},{\"action\" : \"BET\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343748990732},{\"action\" : \"CALL\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 200},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343748994921},{\"pots\" : [{\"potId\" : 0,\"players\" : [1,2],\"potSize\" : 1600}],\"type\" : \"PotUpdate\",\"time\" : 1343748994923},{\"cards\" : [{\"suit\" : \"CLUBS\",\"rank\" : \"EIGHT\"}],\"type\" : \"TableCardsDealt\",\"time\" : 1343748994925},{\"action\" : \"CHECK\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 0},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343749001468},{\"action\" : \"BET\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 400},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343749004244},{\"action\" : \"RAISE\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 800},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343749009196},{\"action\" : \"RAISE\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 1200},\"timeout\" : false,\"playerId\" : 2,\"type\" : \"PlayerAction\",\"time\" : 1343749012325},{\"action\" : \"CALL\",\"amount\" : {\"type\" : \"BET\",\"amount\" : 400},\"timeout\" : false,\"playerId\" : 1,\"type\" : \"PlayerAction\",\"time\" : 1343749016343},{\"pots\" : [{\"potId\" : 0,\"players\" : [1,2],\"potSize\" : 4000}],\"type\" : \"PotUpdate\",\"time\" : 1343749016345},{\"playerId\" : 1,\"cards\" : [{\"suit\" : \"CLUBS\",\"rank\" : \"THREE\"},{\"suit\" : \"SPADES\",\"rank\" : \"TWO\"}],\"type\" : \"PlayerCardsExposed\",\"time\" : 1343749016347},{\"playerId\" : 2,\"cards\" : [{\"suit\" : \"SPADES\",\"rank\" : \"JACK\"},{\"suit\" : \"HEARTS\",\"rank\" : \"QUEEN\"}],\"type\" : \"PlayerCardsExposed\",\"time\" : 1343749016347}], \"seats\" : [{\"id\" : 1,\"initialBalance\" : 11372,\"seatId\" : 0,\"name\" : \"1\"},{\"id\" : 2,\"initialBalance\" : 8600,\"seatId\" : 1,\"name\" : \"2\"}] }";
            }
        });

    }

    @Override
    public String getPageTitle() {
        return "Hand History";
    }
}

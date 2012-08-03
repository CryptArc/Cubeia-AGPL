package com.cubeia.games.poker.admin.wicket.pages.history;

import com.cubeia.games.poker.admin.service.history.HandHistoryService;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.poker.handhistory.api.HistoricHand;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;
import java.util.List;

/**
 * Homepage
 */
public class HandHistory extends BasePage {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private HandHistoryService historyService;

    public HandHistory(final PageParameters parameters) {
        List<HistoricHand> hands = historyService.findHandHistoryByPlayerId(1);
        ListView<HistoricHand> list = new ListView<HistoricHand>("hands", hands) {

            @Override
            protected void populateItem(ListItem<HistoricHand> item) {
                HistoricHand hand = item.getModelObject();
                item.add(new Label("handId", String.valueOf(hand.getHandId().getHandId())));
                item.add(new Label("tableId", hand.getHandId().getTableIntegrationId()));
                item.add(new Label("startTime", formatDate(hand.getStartTime())));
                item.add(new Label("endTime", formatDate(hand.getEndTime())));
                item.add(new Label("totalRake", "" + hand.getResults().getTotalRake()));
            }
        };

        add(list);
    }

    private String formatDate(long millis) {
        return new Date(millis).toString();
    }

    @Override
    public String getPageTitle() {
        return "Hand History";
    }
}

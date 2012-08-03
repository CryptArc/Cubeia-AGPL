package com.cubeia.games.poker.admin.wicket.pages.history;

import com.cubeia.games.poker.admin.service.history.HandHistoryService;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.poker.handhistory.api.HistoricHand;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Homepage
 */
public class HandHistory extends BasePage {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private HandHistoryService historyService;

    public HandHistory(final PageParameters parameters) {
        List<IColumn<HistoricHand>> columns = new ArrayList<IColumn<HistoricHand>>();

        columns.add(new PropertyColumn<HistoricHand>(Model.of("Hand id"), "handId.handId"));
        columns.add(new PropertyColumn<HistoricHand>(Model.of("Table id"), "handId.tableIntegrationId"));
        columns.add(new PropertyColumn<HistoricHand>(Model.of("Start date"), "startTime"));
        columns.add(new PropertyColumn<HistoricHand>(Model.of("End date"), "endTime"));
        columns.add(new PropertyColumn<HistoricHand>(Model.of("Total rake"), "results.totalRake"));

        add(new AjaxFallbackDefaultDataTable<HistoricHand>("hands", columns, new HandProvider(), 8));
    }

    private String formatDate(long millis) {
        return new Date(millis).toString();
    }

    @Override
    public String getPageTitle() {
        return "Hand History";
    }

    private class HandProvider extends SortableDataProvider<HistoricHand> {

        @Override
        public Iterator<? extends HistoricHand> iterator(int first, int count) {
            return historyService.findHandHistoryByPlayerId(1).iterator();
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public IModel<HistoricHand> model(HistoricHand historicHand) {
            return Model.of(historicHand);
        }
    }
}

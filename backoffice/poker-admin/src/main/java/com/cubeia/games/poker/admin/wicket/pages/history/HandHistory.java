package com.cubeia.games.poker.admin.wicket.pages.history;

import com.cubeia.games.poker.admin.service.history.HandHistoryService;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.poker.handhistory.api.HistoricHand;
import org.apache.log4j.Logger;
import org.apache.wicket.IClusterable;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Page for searching for and viewing hand histories.
 */
public class HandHistory extends BasePage {

    private static final Logger log = Logger.getLogger(HandHistory.class);

    private static final long serialVersionUID = 1L;

    @SpringBean
    private HandHistoryService historyService;
    private final HandHistory.HandProvider handProvider = new HandProvider();;

    public HandHistory() {
        List<IColumn<HistoricHand>> columns = createColumns();
        add(new AjaxFallbackDefaultDataTable<HistoricHand>("hands", columns, handProvider, 8));
        addForm();
    }

    private List<IColumn<HistoricHand>> createColumns() {
        List<IColumn<HistoricHand>> columns = new ArrayList<IColumn<HistoricHand>>();
        columns.add(new PropertyColumn<HistoricHand>(Model.of("Hand id"), "handId.handId"));
        columns.add(new PropertyColumn<HistoricHand>(Model.of("Table id"), "handId.tableIntegrationId"));
        columns.add(new PropertyColumn<HistoricHand>(Model.of("Start date"), "startTime"));
        columns.add(new PropertyColumn<HistoricHand>(Model.of("End date"), "endTime"));
        columns.add(new PropertyColumn<HistoricHand>(Model.of("Total rake"), "results.totalRake"));
        return columns;
    }

    private void addForm() {
        Form form = new Form<HandHistorySearch>("form",  new CompoundPropertyModel<HandHistorySearch>(new HandHistorySearch())) {
            @Override
            protected void onSubmit() {
                handProvider.search(getModel().getObject());
            }
        };
        form.add(new TextField<Integer>("playerId"));
        form.add(new DateField("fromDate"));
        form.add(new DateField("toDate"));
        add(form);
    }

    @Override
    public String getPageTitle() {
        return "Hand History";
    }

    private class HandProvider extends SortableDataProvider<HistoricHand> {

        private List<HistoricHand> hands = newArrayList();

        private HandProvider() {
        }

        @Override
        public Iterator<? extends HistoricHand> iterator(int first, int count) {
            return hands.iterator();
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public IModel<HistoricHand> model(HistoricHand historicHand) {
            return Model.of(historicHand);
        }

        public void search(HandHistorySearch params) {
            hands = historyService.findHandHistoryByPlayerId(params.playerId);
        }
    }

    private class HandHistorySearch implements IClusterable {
        int playerId;
        Date fromDate;
        Date toDate;
    }
}

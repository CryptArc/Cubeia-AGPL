package com.cubeia.games.poker.admin.wicket.pages.tournaments.history;

import com.cubeia.games.poker.admin.service.history.HistoryService;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.admin.wicket.util.DatePanel;
import com.cubeia.games.poker.admin.wicket.util.LabelLinkPanel;
import com.cubeia.games.poker.admin.wicket.util.ParamBuilder;
import com.cubeia.poker.tournament.history.api.HistoricTournament;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class SearchTournamentHistory extends BasePage {

    private static final Logger log = Logger.getLogger(SearchTournamentHistory.class);

    private static final long serialVersionUID = 1L;

    @SpringBean
    private HistoryService historyService;

    private final TournamentProvider tournamentProvider = new TournamentProvider();

    public SearchTournamentHistory(PageParameters p) {
        super(p);
        addForm();
        addResultsTable();
        add(new FeedbackPanel("feedback"));
    }

    private void addResultsTable() {
        List<IColumn<HistoricTournament,String>> columns = createColumns();
        add(new AjaxFallbackDefaultDataTable<HistoricTournament,String>("tournaments", columns, tournamentProvider, 8));
    }

    private List<IColumn<HistoricTournament,String>> createColumns() {
        List<IColumn<HistoricTournament,String>> columns = new ArrayList<IColumn<HistoricTournament,String>>();

        // Add column with clickable hand ids.
        columns.add(new AbstractColumn<HistoricTournament,String>(new Model<String>("Historic id")) {
            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(Item<ICellPopulator<HistoricTournament>> item, String componentId, IModel<HistoricTournament> model) {
                HistoricTournament tournament = model.getObject();
                String historicId = tournament.getId();
                Component panel = new LabelLinkPanel(componentId, historicId, ShowTournament.class, ParamBuilder.params("historicTournamentId", historicId));
                item.add(panel);
            }

            @Override
            public boolean isSortable() {
                return false;
            }

        });
        // columns.add(new PropertyColumn<HistoricTournament>(Model.of("Hand id"), "handId.handId"));
        columns.add(new PropertyColumn<HistoricTournament,String>(Model.of("Name"), "tournamentName"));
        columns.add(new AbstractColumn<HistoricTournament,String>(new Model<String>("Start date")) {
            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(Item<ICellPopulator<HistoricTournament>> item, String componentId, IModel<HistoricTournament> model) {
                HistoricTournament tournament = model.getObject();
                item.add(new DatePanel(componentId, tournament.getStartTime()));
            }

            @Override
            public boolean isSortable() {
                return false;
            }
        });
        columns.add(new AbstractColumn<HistoricTournament,String>(new Model<String>("End date")) {
            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(Item<ICellPopulator<HistoricTournament>> item, String componentId, IModel<HistoricTournament> model) {
                HistoricTournament tournament = model.getObject();
                item.add(new DatePanel(componentId, tournament.getEndTime()));
            }

            @Override
            public boolean isSortable() {
                return false;
            }
        });

        return columns;
    }

    private void addForm() {
        Form<TournamentSearch> form = new Form<TournamentSearch>("form",  new CompoundPropertyModel<TournamentSearch>(new TournamentSearch())) {
            @Override
            protected void onSubmit() {
                tournamentProvider.search(getModel().getObject());
            }
        };
        form.add(new DateField("fromDate"));
        form.add(new DateField("toDate"));
        add(form);
    }

    @Override
    public String getPageTitle() {
        return "Search Tournament History";
    }

    private class TournamentProvider extends SortableDataProvider<HistoricTournament,String> {

        private List<HistoricTournament> tournaments = newArrayList();

        private TournamentProvider() {
        }

        @Override
        public Iterator<? extends HistoricTournament> iterator(long first, long count) {
            return tournaments.iterator();
        }

        @Override
        public long size() {
            return tournaments.size();
        }

        @Override
        public IModel<HistoricTournament> model(HistoricTournament historicTournament) {
            return Model.of(historicTournament);
        }

        public void search(TournamentSearch params) {
            tournaments = historyService.findTournaments(params.fromDate, params.toDate);
        }
    }

    private static class TournamentSearch implements IClusterable {
        Date fromDate;
        Date toDate;
    }

}

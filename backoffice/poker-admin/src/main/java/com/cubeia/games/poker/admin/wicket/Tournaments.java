package com.cubeia.games.poker.admin.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cubeia.games.poker.admin.wicket.pages.history.ShowHand;
import com.cubeia.games.poker.admin.wicket.tournament.EditTournament;
import com.cubeia.games.poker.admin.wicket.util.LabelLinkPanel;
import com.cubeia.games.poker.admin.wicket.util.ParamBuilder;
import com.cubeia.poker.handhistory.api.HistoricHand;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;

/**
 * Page for listing all tournaments. Currently lists sit&go tournaments.
 */
public class Tournaments extends BasePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "adminDAO")
    private AdminDAO adminDAO;


    /**
     * Constructor that is invoked when page is invoked without a session.
     *
     * @param parameters Page parameters
     */
    public Tournaments(final PageParameters parameters) {
        SortableDataProviderExtension dataProvider = new SortableDataProviderExtension();
        ArrayList<AbstractColumn> columns = new ArrayList<AbstractColumn>();

        columns.add(new AbstractColumn<TournamentConfiguration>(new Model<String>("Id")) {
            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(Item<ICellPopulator<TournamentConfiguration>> item, String componentId, IModel<TournamentConfiguration> model) {
                TournamentConfiguration tournament = model.getObject();
                Component panel = new LabelLinkPanel(
                    componentId,
                    "" + tournament.getId(),
                    EditTournament.class,
                    ParamBuilder.params("tournamentId", tournament.getId()));
                item.add(panel);
            }

            @Override
            public boolean isSortable() {
                return false;
            }
        });

//        columns.add(new PropertyColumn(new Model("Id"), "id"));
        columns.add(new PropertyColumn(new Model("Name"), "name"));
        columns.add(new PropertyColumn(new Model("Seats"), "seatsPerTable"));
        columns.add(new PropertyColumn(new Model("Min"), "minPlayers"));
        columns.add(new PropertyColumn(new Model("Max"), "maxPlayers"));

        DefaultDataTable userTable = new DefaultDataTable("tournamentTable", columns, dataProvider, 20);
        add(userTable);
    }

    private List<TournamentConfiguration> getTournamentList() {
        return adminDAO.getAllTournaments();
    }

    private final class SortableDataProviderExtension extends SortableDataProvider<TournamentConfiguration> {
        private static final long serialVersionUID = 1L;

        public SortableDataProviderExtension() {
            setSort("id", SortOrder.DESCENDING);
        }

        @Override
        public Iterator<TournamentConfiguration> iterator(int first, int count) {
            return getTournamentList().subList(first, count + first).iterator();
        }

        @Override
        public IModel<TournamentConfiguration> model(TournamentConfiguration object) {
            return new Model(object);
        }

        @Override
        public int size() {
            return getTournamentList().size();
        }
    }

    @Override
    public String getPageTitle() {
        return "Tournaments";
    }
}

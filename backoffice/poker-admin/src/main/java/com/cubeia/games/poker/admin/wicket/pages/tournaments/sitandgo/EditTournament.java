package com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.ListTournaments;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class EditTournament extends BasePage {

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;
    
    @SuppressWarnings("unused")
    private SitAndGoConfiguration tournament;
    
    public EditTournament(final PageParameters parameters) {
        final Integer tournamentId = parameters.get("tournamentId").toInt();
        
        loadFormData(tournamentId);
        
        Form<SitAndGoConfiguration> tournamentForm = new Form<SitAndGoConfiguration>("tournamentForm",
                                                                new CompoundPropertyModel<SitAndGoConfiguration>(tournament)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSubmit() {
                // TODO: Update tournament configuration here
                SitAndGoConfiguration object = getModel().getObject();
                adminDAO.save(object);
                info("Tournament updated, id = " + tournamentId);
                setResponsePage(ListSitAndGoTournaments.class);
            }
        };
        
        tournamentForm.add(new TextField("name", new PropertyModel(this, "tournament.configuration.name")));
        tournamentForm.add(new TextField<Integer>("seatsPerTable", new PropertyModel(this, "tournament.configuration.seatsPerTable")));
        tournamentForm.add(new TextField<Integer>("timingType", new PropertyModel(this, "tournament.configuration.timingType")));
        tournamentForm.add(new TextField<Integer>("minPlayers", new PropertyModel(this, "tournament.configuration.minPlayers")));
        tournamentForm.add(new TextField<Integer>("maxPlayers", new PropertyModel(this, "tournament.configuration.maxPlayers")));
        
        add(tournamentForm);

        add(new FeedbackPanel("feedback"));
    }

    private void loadFormData(final Integer tournamentId) {
        tournament = adminDAO.getItem(SitAndGoConfiguration.class, tournamentId);
    }
    
    @Override
    public String getPageTitle() {
        return "Edit Tournament";
    }

}

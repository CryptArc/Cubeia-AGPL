package com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo;

import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;
import com.cubeia.games.poker.admin.wicket.BasePage;

public class CreateSitAndGo extends BasePage {

    private static final transient Logger log = Logger.getLogger(CreateSitAndGo.class);

    
    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;
    
    private SitAndGoConfiguration tournament;
    
    public CreateSitAndGo(final PageParameters parameters) {
        resetFormData();
        
        Form tournamentForm = new Form("tournamentForm") {
            private static final long serialVersionUID = 1L;
            
            
            @Override
            protected void onSubmit() {
                adminDAO.persist(tournament);
                log.debug("created tournament config with id = " + tournament);
                setResponsePage(ListSitAndGoTournaments.class);
            }
        };
        
        tournamentForm.add(new RequiredTextField("name", new PropertyModel(this, "tournament.configuration.name")));
        tournamentForm.add(new RequiredTextField("seatsPerTable", new PropertyModel(this, "tournament.configuration.seatsPerTable")));
        tournamentForm.add(new RequiredTextField("timingType", new PropertyModel(this, "tournament.configuration.timingType")));
        tournamentForm.add(new RequiredTextField("minPlayers", new PropertyModel(this, "tournament.configuration.minPlayers")));
        tournamentForm.add(new RequiredTextField("maxPlayers", new PropertyModel(this, "tournament.configuration.maxPlayers")));
        
        
        add(tournamentForm);
        add(new FeedbackPanel("feedback"));
    }
    
    private void resetFormData() {
        tournament = new SitAndGoConfiguration();
    }


    @Override
    public String getPageTitle() {
        return "Create Sit-And-Go Tournament";
    }

}

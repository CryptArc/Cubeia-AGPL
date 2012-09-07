package com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.CreateSitAndGo;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentSchedule;
import org.apache.log4j.Logger;
import org.apache.wicket.IClusterable;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;

public class CreateTournament extends BasePage {

    private static final transient Logger log = Logger.getLogger(CreateSitAndGo.class);

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;

    public CreateTournament(final PageParameters parameters) {
        Form<ScheduledTournamentForm> tournamentForm = new Form<ScheduledTournamentForm>("tournamentForm",
                                new CompoundPropertyModel<ScheduledTournamentForm>(new ScheduledTournamentForm())) {
            @Override
            protected void onSubmit() {
                ScheduledTournamentForm form = getModel().getObject();
                TournamentSchedule schedule = new TournamentSchedule(form.startDate, form.endDate, form.schedule, form.minutesInAnnounced,
                                                                     form.minutesInRegistering, form.minutesVisibleAfterFinished);
                ScheduledTournamentConfiguration tournament = new ScheduledTournamentConfiguration();
                tournament.getConfiguration().setName(form.name);
                tournament.getConfiguration().setMinPlayers(form.minPlayers);
                tournament.getConfiguration().setMaxPlayers(form.maxPlayers);
                tournament.getConfiguration().setSeatsPerTable(form.seatsPerTable);

                tournament.getConfiguration().setTimingType(form.timingType);
                tournament.setSchedule(schedule);
                adminDAO.persist(tournament);
                log.debug("created tournament config with id = " + tournament);
                setResponsePage(ListTournaments.class);
            }
        };

        tournamentForm.add(new RequiredTextField("name"));
        tournamentForm.add(new DateField("startDate"));
        tournamentForm.add(new DateField("endDate"));
        tournamentForm.add(new RequiredTextField("schedule"));
        tournamentForm.add(new TextField<Integer>("minutesInAnnounced"));
        tournamentForm.add(new TextField<Integer>("minutesInRegistering"));
        tournamentForm.add(new TextField<Integer>("minutesVisibleAfterFinished"));
        tournamentForm.add(new TextField<Integer>("seatsPerTable"));
        tournamentForm.add(new TextField<Integer>("timingType"));
        tournamentForm.add(new TextField<Integer>("minPlayers"));
        tournamentForm.add(new TextField<Integer>("maxPlayers"));

        add(tournamentForm);
        add(new FeedbackPanel("feedback"));
    }

    @Override
    public String getPageTitle() {
        return "Create Sit-And-Go Tournament";
    }

    private class ScheduledTournamentForm implements IClusterable {
        String name;
        Date startDate;
        Date endDate;
        String schedule;
        Integer minutesInAnnounced;
        Integer minutesInRegistering;
        Integer minutesVisibleAfterFinished;
        Integer seatsPerTable;
        Integer timingType;
        Integer minPlayers;
        Integer maxPlayers;
    }

}

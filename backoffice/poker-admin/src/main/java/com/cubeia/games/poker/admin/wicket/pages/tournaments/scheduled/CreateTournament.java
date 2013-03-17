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

package com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.configuration.TournamentConfigurationPanel;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.rebuy.RebuyConfigurationPanel;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.CreateSitAndGo;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentSchedule;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

import java.util.Date;

public class CreateTournament extends BasePage {

    private static final transient Logger log = Logger.getLogger(CreateSitAndGo.class);
    private RebuyConfigurationPanel rebuyConfigurationPanel;

    @SpringBean(name = "adminDAO")
    private AdminDAO adminDAO;
    private PropertyModel<TournamentConfiguration> configuration = new PropertyModel<TournamentConfiguration>(new ScheduledTournamentConfiguration(), "configuration");


    public CreateTournament(final PageParameters parameters) {
        super(parameters);
        Form<ScheduledTournamentForm> tournamentForm = new Form<ScheduledTournamentForm>("tournamentForm",
            new CompoundPropertyModel<ScheduledTournamentForm>(new ScheduledTournamentForm())) {

            @Override
            protected void onSubmit() {
                ScheduledTournamentForm form = getModel().getObject();
                TournamentSchedule schedule = new TournamentSchedule(form.startDate, form.endDate, form.schedule, form.minutesInAnnounced,
                                                                     form.minutesInRegistering, form.minutesVisibleAfterFinished);
                ScheduledTournamentConfiguration tournament = new ScheduledTournamentConfiguration();
                tournament.setConfiguration(configuration.getObject());
                tournament.setSchedule(schedule);
                adminDAO.persist(tournament);
                log.debug("created tournament config with id = " + tournament);
                setResponsePage(ListTournaments.class);
            }
        };

        tournamentForm.add(new TournamentConfigurationPanel("configuration", configuration, false));
        tournamentForm.add(new DateField("startDate"));
        tournamentForm.add(new DateField("endDate"));
        tournamentForm.add(new RequiredTextField<String>("schedule"));
        tournamentForm.add(new TextField<Integer>("minutesInAnnounced"));
        tournamentForm.add(new TextField<Integer>("minutesInRegistering"));
        tournamentForm.add(new TextField<Integer>("minutesVisibleAfterFinished"));
        addRebuyPanel(tournamentForm);

        add(tournamentForm);
        add(new FeedbackPanel("feedback"));
    }

    private void addRebuyPanel(Form<ScheduledTournamentForm> tournamentForm) {
        CheckBox enableRebuys = new CheckBox("rebuysEnabled");
        enableRebuys.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        log.debug("Setting enabled " + !rebuyConfigurationPanel.isEnabled());
                        rebuyConfigurationPanel.setRebuysEnabled(!rebuyConfigurationPanel.isEnabled());
                        target.add(rebuyConfigurationPanel);
                    }
                });
        tournamentForm.add(enableRebuys);

        rebuyConfigurationPanel = new RebuyConfigurationPanel("rebuyConfiguration", configuration.getObject().getRebuyConfiguration(), false);
        rebuyConfigurationPanel.setOutputMarkupId(true);
        tournamentForm.add(rebuyConfigurationPanel);
    }

    @Override
    public String getPageTitle() {
        return "Create Scheduled Tournament";
    }

    private static class ScheduledTournamentForm implements IClusterable {
        boolean rebuysEnabled;
        Date startDate;
        Date endDate;
        String schedule;
        Integer minutesInAnnounced;
        Integer minutesInRegistering;
        Integer minutesVisibleAfterFinished;
    }
}

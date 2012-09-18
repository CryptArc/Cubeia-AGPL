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

import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;
import com.cubeia.games.poker.admin.wicket.BasePage;

public class EditTournament extends BasePage {

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;
    
    @SuppressWarnings("unused")
    private ScheduledTournamentConfiguration tournament;
    
    public EditTournament(final PageParameters parameters) {
        final Integer tournamentId = parameters.get("tournamentId").toInt();
        
        loadFormData(tournamentId);
        
        Form<ScheduledTournamentConfiguration> tournamentForm = new Form<ScheduledTournamentConfiguration>("tournamentForm",
                                                                new CompoundPropertyModel<ScheduledTournamentConfiguration>(tournament)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSubmit() {
                // TODO: Update tournament configuration here
                ScheduledTournamentConfiguration object = getModel().getObject();
                adminDAO.save(object);
                info("Tournament updated, id = " + tournamentId);
                setResponsePage(ListTournaments.class);
            }
        };
        
        tournamentForm.add(new TextField("name", new PropertyModel(this, "tournament.configuration.name")));
        tournamentForm.add(new DateField("startDate", new PropertyModel(this, "tournament.schedule.startDate")));
        tournamentForm.add(new DateField("endDate", new PropertyModel(this, "tournament.schedule.endDate")));
        tournamentForm.add(new RequiredTextField("schedule", new PropertyModel(this, "tournament.schedule.cronSchedule")));
        tournamentForm.add(new TextField<Integer>("minutesInAnnounced", new PropertyModel(this, "tournament.schedule.minutesInAnnounced")));
        tournamentForm.add(new TextField<Integer>("minutesInRegistering", new PropertyModel(this, "tournament.schedule.minutesInRegistering")));
        tournamentForm.add(new TextField<Integer>("minutesVisibleAfterFinished", new PropertyModel(this, "tournament.schedule.minutesVisibleAfterFinished")));
        tournamentForm.add(new TextField<Integer>("seatsPerTable", new PropertyModel(this, "tournament.configuration.seatsPerTable")));
        tournamentForm.add(new TextField<Integer>("timingType", new PropertyModel(this, "tournament.configuration.timingType")));
        tournamentForm.add(new TextField<Integer>("minPlayers", new PropertyModel(this, "tournament.configuration.minPlayers")));
        tournamentForm.add(new TextField<Integer>("maxPlayers", new PropertyModel(this, "tournament.configuration.maxPlayers")));
        
        add(tournamentForm);

        add(new FeedbackPanel("feedback"));
    }

    private void loadFormData(final Integer tournamentId) {
        tournament = adminDAO.getItem(ScheduledTournamentConfiguration.class, tournamentId);
    }
    
    @Override
    public String getPageTitle() {
        return "Edit Tournament";
    }

}

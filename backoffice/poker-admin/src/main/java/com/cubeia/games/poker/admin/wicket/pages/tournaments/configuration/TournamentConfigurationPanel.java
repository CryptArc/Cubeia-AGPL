/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.admin.wicket.pages.tournaments.configuration;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructure;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructure;
import com.cubeia.poker.timing.TimingProfile;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;

public class TournamentConfigurationPanel extends Panel {

    private static final Logger log = Logger.getLogger(TournamentConfigurationPanel.class);

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;

    private PropertyModel<TournamentConfiguration> model;

    public TournamentConfigurationPanel(String id, PropertyModel<TournamentConfiguration> propertyModel) {
        super(id, propertyModel);
        this.model = propertyModel;

        add(new TextField("name", new PropertyModel(model, "name")));
        add(new TextField<Integer>("seatsPerTable", new PropertyModel(model, "seatsPerTable")));
        add(new DropDownChoice<TimingProfile>("timingType", new PropertyModel(model, "timingType"), adminDAO.getTimingProfiles(),
                new ChoiceRenderer<TimingProfile>("name")));
        add(new TextField<Integer>("minPlayers", new PropertyModel(model, "minPlayers")));
        add(new TextField<Integer>("maxPlayers", new PropertyModel(model, "maxPlayers")));
        add(new TextField<BigDecimal>("buyIn", new PropertyModel(model, "buyIn")));
        add(new TextField<BigDecimal>("fee", new PropertyModel(model, "fee")));
        add(new DropDownChoice<BlindsStructure>("blindsStructure", new PropertyModel(model, "blindsStructure"), adminDAO.getBlindsStructures(),
                new ChoiceRenderer<BlindsStructure>("name")));
        add(new DropDownChoice<PayoutStructure>("payoutStructure", new PropertyModel(model, "payoutStructure"), adminDAO.getPayoutStructures(),
                new ChoiceRenderer<PayoutStructure>("name")));
    }
}

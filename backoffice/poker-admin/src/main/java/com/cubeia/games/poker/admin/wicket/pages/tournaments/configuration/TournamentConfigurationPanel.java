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
import com.cubeia.games.poker.admin.network.NetworkClient;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.blinds.BlindsStructure;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructure;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.timing.TimingProfile;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import java.math.BigDecimal;

import static java.util.Arrays.asList;

public class TournamentConfigurationPanel extends Panel {

    private static final Logger log = Logger.getLogger(TournamentConfigurationPanel.class);

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;

    @SpringBean
    private NetworkClient networkClient;

    private PropertyModel<TournamentConfiguration> model;

    public TournamentConfigurationPanel(String id, PropertyModel<TournamentConfiguration> propertyModel, boolean sitAndGo) {
        super(id, propertyModel);
        this.model = propertyModel;
        add(new TextField<String>("name", new PropertyModel(model, "name")));
        add(new TextField<Integer>("seatsPerTable", new PropertyModel(model, "seatsPerTable")));
        add(new DropDownChoice<TimingProfile>("timingType", model("timingType"), adminDAO.getTimingProfiles(), renderer("name")));
        add(new TextField<Integer>("minPlayers", new PropertyModel(model, "minPlayers")));
        TextField<Integer> maxPlayers = new TextField<Integer>("maxPlayers", new PropertyModel(model, "maxPlayers"));
        add(maxPlayers);
        add(new TextField<BigDecimal>("buyIn", new PropertyModel(model, "buyIn")));
        add(new TextField<BigDecimal>("fee", new PropertyModel(model, "fee")));
        add(new TextField<BigDecimal>("guaranteedPrizePool", new PropertyModel(model, "guaranteedPrizePool")));
        add(new TextField<Long>("startingChips", new PropertyModel(model, "startingChips")).add(RangeValidator.minimum(1L)));
        add(new DropDownChoice<BetStrategyType>("betStrategy", model("betStrategy"), asList(BetStrategyType.values()), renderer("name")));
        add(new DropDownChoice<String>("currency", model("currency"), networkClient.getCurrencies(), new ChoiceRenderer<String>()));
        add(new DropDownChoice<BlindsStructure>("blindsStructure", model("blindsStructure"), adminDAO.getBlindsStructures(), renderer("name")));
        add(new DropDownChoice<PayoutStructure>("payoutStructure", model("payoutStructure"), adminDAO.getPayoutStructures(), renderer("name")));

        if (sitAndGo) {
            maxPlayers.setVisible(false);
        }
    }

    private PropertyModel model(String expression) {
        return new PropertyModel(model, expression);
    }

    private <T> ChoiceRenderer<T> renderer(String name) {
        return new ChoiceRenderer<T>(name);
    }
}

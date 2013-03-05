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

package com.cubeia.games.poker.admin.wicket.pages.tournaments.rebuy;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.tournament.configuration.RebuyConfiguration;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;

public class RebuyConfigurationPanel extends Panel {

    private static final Logger log = Logger.getLogger(RebuyConfigurationPanel.class);

    private final PropertyModel<RebuyConfiguration> model;

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;

    public RebuyConfigurationPanel(String id, PropertyModel<RebuyConfiguration> propertyModel) {
        super(id, propertyModel);
        this.model = propertyModel;
        this.<Integer>add("numberOfRebuysAllowed");
        checkBox("addOnsEnabled");
        this.<Integer>add("numberOfLevelsWithRebuys");
        this.<BigDecimal>add("rebuyCost");
        this.<Integer>add("chipsForRebuy");
        this.<BigDecimal>add("addOnCost");
        this.<Integer>add("chipsForAddOn");
        this.<Long>add("maxStackForRebuy");
        setEnabled(false);
        log.debug("Setting enabled false.");
    }

    private void checkBox(String expression) {
        CheckBox checkBox = new CheckBox(expression, model(expression));
//        checkBox.setEnabled(false);
        add(checkBox);
    }

    private <T> void add(String expression) {
        TextField<T> textField = new TextField<T>(expression, model(expression));
//        textField.setEnabled(false);
        add(textField);
    }

    private PropertyModel model(String expression) {
        return new PropertyModel(model, expression);
    }

}

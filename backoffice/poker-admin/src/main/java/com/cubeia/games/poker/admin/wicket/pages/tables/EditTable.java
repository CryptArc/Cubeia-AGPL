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

package com.cubeia.games.poker.admin.wicket.pages.tables;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.entity.TableConfigTemplate;

public class EditTable extends BasePage {

    private static final long serialVersionUID = 6896786450236805072L;

	@SpringBean(name="adminDAO")
    private AdminDAO adminDAO;
    
    private TableConfigTemplate table;
    
    public EditTable(final PageParameters parameters) {
        final Integer templateId = parameters.get("templateId").toInt();
        loadFormData(templateId);
        Form<TableConfigTemplate> tableForm = new Form<TableConfigTemplate>("tableForm", new CompoundPropertyModel<TableConfigTemplate>(table)) {
            
        	private static final long serialVersionUID = 1L;
            
        	@Override
            protected void onSubmit() {
        		TableConfigTemplate object = getModel().getObject();
                adminDAO.save(object);
                // info("Table template updated, id = " + templateId);
                setResponsePage(ListTables.class);
            }
        };
        
        tableForm.add(new RequiredTextField<String>("name", new PropertyModel<String>(this, "table.name")));
        tableForm.add(new RequiredTextField<Integer>("ante", new PropertyModel<Integer>(this, "table.ante")));
        tableForm.add(new RequiredTextField<Integer>("seatsPerTable", new PropertyModel<Integer>(this, "table.seats")));
        tableForm.add(new TextField<Integer>("minTables", new PropertyModel<Integer>(this, "table.minTables")));
        tableForm.add(new TextField<Integer>("minEmptyTables", new PropertyModel<Integer>(this, "table.minEmptyTables")));
        
        add(tableForm);

        add(new FeedbackPanel("feedback"));
    }

    private void loadFormData(final Integer templateId) {
        table = adminDAO.getItem(TableConfigTemplate.class, templateId);
    }
    
    @Override
    public String getPageTitle() {
        return "Edit Table";
    }
}

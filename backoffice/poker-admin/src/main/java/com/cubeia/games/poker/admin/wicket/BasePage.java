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

package com.cubeia.games.poker.admin.wicket;

import com.cubeia.network.shared.web.wicket.navigation.Breadcrumbs;
import com.cubeia.network.shared.web.wicket.navigation.MenuPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

public abstract class BasePage extends WebPage {

    private static final long serialVersionUID = -913606276144395037L;

    private String query;

    public BasePage(PageParameters p) {
        add(new MenuPanel("menuPanel", SiteMap.getPages(), this.getClass()));
        add(new Breadcrumbs("breadcrumb", SiteMap.getPages(), this.getClass()));
        // defer setting the title model object as the title may not be generated now
        add(new Label("title", new Model<String>()));

        /*Form form = new Form("global.searchform") {

              protected void onSubmit() {
                  if(query != null) {
                      setResponsePage(SearchPage.class, start("query", query).end());
                  }
              };
          };

          form.add(new TextField<String>("global.searchbox", new PropertyModel<String>(this, "query")));
          add(form);*/
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void renderHead(IHeaderResponse resp) {
        resp.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(BasePage.class, "jquery-1.7.2.min.js")));
        resp.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(BasePage.class, "jquery-tmpl-1.4.2.min.js")));
    }

    protected <T>ChoiceRenderer<T> choiceRenderer() {
        return choiceRenderer("name");
    }

    protected <T>ChoiceRenderer<T> choiceRenderer(String property) {
        return new ChoiceRenderer<T>(property);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        get("title").setDefaultModelObject(getPageTitle());
    }

    public abstract String getPageTitle();
}

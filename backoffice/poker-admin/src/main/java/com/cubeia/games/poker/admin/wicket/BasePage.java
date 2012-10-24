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

import static com.cubeia.games.poker.admin.wicket.util.ParamBuilder.start;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.cubeia.games.poker.admin.wicket.util.ParamBuilder;

public abstract class BasePage extends WebPage {

	private static final long serialVersionUID = -913606276144395037L;

	private String query;

	public BasePage(PageParameters p) {
		add(new MenuPanel("menuPanel", this.getClass()));
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
		resp.renderJavaScriptReference(new PackageResourceReference(BasePage.class,"jquery-1.7.2.min.js"));
		resp.renderJavaScriptReference(new PackageResourceReference(BasePage.class,"jquery-tmpl-1.4.2.min.js"));
		resp.renderCSSReference(new PackageResourceReference(BasePage.class,"style.css"));
		resp.renderCSSReference(new PackageResourceReference(BasePage.class,"bootstrap-responsive.min.css"));
		resp.renderCSSReference(new PackageResourceReference(BasePage.class,"bootstrap.min.css"));
	}
	
	@Override
	protected void onBeforeRender() {
	    super.onBeforeRender();
	    
	    get("title").setDefaultModelObject(getPageTitle());
	}
	
	public abstract String getPageTitle();
}

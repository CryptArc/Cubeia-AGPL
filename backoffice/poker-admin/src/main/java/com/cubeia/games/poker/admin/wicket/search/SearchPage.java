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

package com.cubeia.games.poker.admin.wicket.search;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.games.poker.admin.Configuration;
import com.cubeia.games.poker.admin.wicket.BasePage;

public class SearchPage extends BasePage {

    private static final int HIT_LIMIT = 5;

    @SuppressWarnings("unused")
	private static final String APPLICATION_JSON = "application/json";

    private static final long serialVersionUID = 1L;

    Logger log = LoggerFactory.getLogger(getClass());
    
    @SpringBean(name = "webConfig")
    private Configuration config;

	private HitProvider dataProvider;

    /**
     * Constructor that is invoked when page is invoked without a session.
     *
     * @param parameters Page parameters
     */
    @SuppressWarnings("serial")
	public SearchPage(PageParameters parameters) {
        super(parameters);
        
        final Model<String> searchInputModel = new Model<String>();
        dataProvider = createHitProvider();
        
        final Label resultsCount = new Label("resultsCount", new AbstractReadOnlyModel<String>() {
            @Override public String getObject() { 
                String str = "" + dataProvider.size() + " results. ";
                
                if (dataProvider.isSorting()) {
                    str += "Sorted by " + (dataProvider.isAscending() ? "ascending" : "descending") + " <i>" + dataProvider.getSortField() + "</i>.";
                } else {
                    str += "Sorted by rank.";
                }

                return str;
            }
        });
        resultsCount.setEscapeModelStrings(false);
        resultsCount.setOutputMarkupId(true);
        add(resultsCount);
        
        final WebMarkupContainer resultsContainer = new WebMarkupContainer("resultsContainer") {
            @Override protected void onConfigure() {
                super.onConfigure();
                setVisible(dataProvider.size() > 0);
            }
        };
        resultsContainer.setOutputMarkupPlaceholderTag(true);
        resultsContainer.setOutputMarkupId(true);
        add(resultsContainer);
        
        Form<String> form = new Form<String>("searchForm");
        add(form);
        
        form.add(new RequiredTextField<String>("searchInput", searchInputModel));
        form.add(new AjaxButton("searchButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                log.debug("search query: '{}'", searchInputModel.getObject());
                
                dataProvider.setQuery(searchInputModel.getObject());
                target.add(resultsContainer);
                target.add(resultsCount);
            }
        });
        
        form.add(new FeedbackPanel("feedback"));
        
        DataView<Serializable> dataView = new DataView<Serializable>("searchResult", dataProvider, HIT_LIMIT) {
			@Override
			protected void populateItem(Item<Serializable> item) {
				Serializable hit = item.getModelObject();
				
				if (hit instanceof Account) {
					item.add(new AccountPanel("value", Model.of((Account) hit)));
				} else if (hit instanceof User) {
					item.add(new UserPanel("value", Model.of((User) hit)));
				} else if (hit instanceof Transaction) {
					item.add(new TransactionPanel("value", Model.of((Transaction) hit)));
				} else {
					item.add(new Label("value", hit.toString()));
				}
			}
		};
        resultsContainer.add(dataView);
        
        resultsContainer.add(new AjaxPagingNavigator("navigator", dataView));
        resultsContainer.add(new AjaxPagingNavigator("navigator2", dataView));
    }
    
	private HitProvider createHitProvider() {
		String clusterName = config.getSearchClusterName();
		URL searchUrl;
		
        try {
			searchUrl = new URL(config.getSearchUrl());
		} catch (MalformedURLException e1) {
			throw new RuntimeException("error getting/parsing search base url from config, found: " + config.getSearchUrl());
		}
        String indexName = searchUrl.getPath().replace("/", "");
        
        log.debug("search base url: {}, index name = {}", searchUrl, indexName);
        
        return new HitProvider(clusterName, searchUrl, indexName, HIT_LIMIT);
	}

    @Override
    public String getPageTitle() {
        return "Search";
    }
    
    
}

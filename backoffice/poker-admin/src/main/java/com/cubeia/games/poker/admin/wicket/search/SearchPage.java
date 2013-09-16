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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.games.poker.admin.Configuration;
import com.cubeia.games.poker.admin.wicket.BasePage;

public class SearchPage extends BasePage {

    @SuppressWarnings("unused")
	private static final String APPLICATION_JSON = "application/json";

    private static final long serialVersionUID = 1L;

    Logger log = LoggerFactory.getLogger(getClass());
    
    @SpringBean(name = "webConfig")
    private Configuration config;

	private String indexName;

	private URL searchUrl;

	private HitProvider dataProvider;

	private String clusterName;

    /**
     * Constructor that is invoked when page is invoked without a session.
     *
     * @param parameters Page parameters
     */
    @SuppressWarnings("serial")
	public SearchPage(PageParameters parameters) {
        super(parameters);
        
		initSearch();

        final Model<String> searchInputModel = new Model<String>();
        
        Form<String> form = new Form<String>("searchForm") {
        	protected void onSubmit() {
        		log.debug("search query: '{}'", searchInputModel.getObject());
        		doSearch(searchInputModel.getObject());
        	};
        };
        form.add(new FeedbackPanel("feedback"));
        
        RequiredTextField<String> searchInput = new RequiredTextField<String>("searchInput", searchInputModel);
        form.add(searchInput);
        add(form);
        
        dataProvider = new HitProvider();
        
        DataView<Serializable> dataView = new DataView<Serializable>("searchResult", dataProvider, 20) {
			@Override
			protected void populateItem(Item<Serializable> item) {
				item.add(new Label("value", item.getModelObject().toString()));
			}
		};
        add(dataView);
        
        
    }
    
    private void doSearch(String query) {
        Client client = createClient();

//        String value = parameters.get("query");
        String[] parts = (query.isEmpty() ? new String[0] : query.toString().split(" "));

        BoolQueryBuilder root = QueryBuilders.boolQuery();

        for (String s : parts) {
            if (s.endsWith("*")) {
                s = s.substring(0, s.length() - 1).toLowerCase();
                root.must(QueryBuilders.prefixQuery("_all", s));
            } else {
                root.must(QueryBuilders.matchQuery("_all", s));
            }
        }

        SearchResponse resp;
        try {
            resp = client.prepareSearch(indexName).setQuery(root).execute().get();
            
            ObjectMapper jsonMapper = new ObjectMapper();
            
            List<Serializable> hits = new ArrayList<>();
            
			for (SearchHit h : resp.getHits().getHits()) {
				System.out.println(">>>>>>>>> ");
				System.out.println(h.sourceAsString());
				System.out.println(">>>>>>>>> ");
				
				String type = h.getType();
				if (type.equals("user")) {
					User u = jsonMapper.readValue(h.getSourceAsString(), User.class);
					hits.add(u.toString());
				} else if (type.equals("account")) {
					Account a = jsonMapper.readValue(h.getSourceAsString(), Account.class);
					hits.add(a.toString());
				} else {
					log.warn("unmapped hit type: {}", type);
				}
			}  
			
			dataProvider.setHits(hits);
            
//            List<User> users = new ArrayList<SearchPage.User>();
//
//            for (SearchHit h : resp.getHits().getHits()) {
//                System.out.println(">>>>>>>>> ");
//                System.out.println(h.sourceAsString());
//                System.out.println(">>>>>>>>> ");
//                if (h.getType().equals("user")) {
//                    users.add(new User(h));
//                }
//            }
//
//            UserProvider provider = new UserProvider(users);
//            UserView view = new UserView("userresults", provider);
//
//            add(view);

        } catch (Exception e) {
        	log.error("error searching", e);
            throw new RuntimeException(e);
        } finally {
        	client.close();
        }
        

    	
    }


	private Client createClient() {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();

        Client client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(searchUrl.getHost(), searchUrl.getPort()));
		return client;
	}

	private void initSearch() {
		clusterName = config.getSearchClusterName();
        
        try {
			searchUrl = new URL(config.getSearchUrl());
		} catch (MalformedURLException e1) {
			throw new RuntimeException("error getting/parsing search base url from config, found: " + config.getSearchUrl());
		}
        indexName = searchUrl.getPath().replace("/", "");
        
        log.debug("search base url: {}, index name = {}", searchUrl, indexName);
	}
    

    @Override
    public String getPageTitle() {
        return "Search";
    }

    private static class UserView extends DataView<User> {

        protected UserView(String id, IDataProvider<User> dataProvider) {
            super(id, dataProvider);
        }

        @Override
        protected void populateItem(Item<User> item) {
            User user = item.getModelObject();
            item.setModel(new CompoundPropertyModel<User>(user));
            item.add(new Label("username"));
            item.add(new Label("firstname"));
            item.add(new Label("lastname"));
        }
    }

    private static class HitProvider implements IDataProvider<Serializable> {
        private List<Serializable> hits = new ArrayList<>();

        private HitProvider() {
        }
        
        public void setHits(List<Serializable> hits) {
			this.hits = hits;
		}

        @Override
        public void detach() {
        }

        @Override
        public Iterator<Serializable> iterator(long first, long count) {
            return hits.subList((int)first, (int) (first + count)).iterator();
        }

        @Override
        public long size() {
            return hits.size();
        }

        @Override
        public IModel<Serializable> model(Serializable object) {
            return Model.of(object);
        }
    	
    }
    
    
    private static class UserProvider implements IDataProvider<User> {

        private final List<User> list;

        private UserProvider(List<User> list) {
            this.list = list;
        }

        @Override
        public void detach() {
        }

        @Override
        public Iterator<? extends User> iterator(long first, long count) {
            return list.subList((int)first, (int) (first + count)).iterator();
        }

        @Override
        public long size() {
            return list.size();
        }

        @Override
        public IModel<User> model(User object) {
            return Model.of(object);
        }
    }
}

package com.cubeia.games.poker.admin.wicket.search;

import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial") 
public class HitProvider implements IDataProvider<Serializable> {
    Logger log = LoggerFactory.getLogger(getClass());
    
    class Sort implements Serializable {
        String field;
        boolean ascending;
        public Sort(String field, boolean ascending) {
            this.field = field;
            this.ascending = ascending;
        }
        
        public String getField() {
            return field;
        }
        
        public boolean isAscending() {
            return ascending;
        }
        
        public boolean isNonEmpty() {
            return field != null;
        }
    }
    
    private String indexName;
    private URL searchUrl;
    private String clusterName;
    
    private List<Serializable> hits = new ArrayList<>();
    private String queryString;
    private long offset;
    private long totalHits;
    private int limit;

    private Sort sort = new Sort(null, true);
    
    HitProvider(String clusterName, URL searchUrl, String indexName, int limit) {
        this.clusterName = clusterName;
        this.searchUrl = searchUrl;
        this.indexName = indexName;
        this.limit = limit;
    }
    
    public void setQuery(String queryString) {
        this.queryString = queryString;

        sort = parseSort(queryString);
        
        offset = 0;
        totalHits = 0;
        hits = null;
        
        doSearch((int) offset, (int) limit);
    }
    
    protected Sort parseSort(String query) {
        Pattern datePatt = Pattern.compile(".* +_sort\\:([a-zA-Z]+),?(asc|desc)? +.*");
        Matcher m = datePatt.matcher(" " + query + " ");
        
        String sortField = null;
        boolean asc = true;
        
        if (m.matches()) {
            sortField = m.group(1);
            if ("desc".equals(m.group(2))) {
                asc = false;
            }
        }
        
        return new Sort(sortField, asc);
    }

    private Client createClient() {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();

        Client client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(searchUrl.getHost(), searchUrl.getPort()));
        return client;
    }
    
    private void doSearch(int offset, int limit) {
        Client client = createClient();

        log.debug("search: offset = {}, limit = {}", offset, limit);
        
        QueryStringQueryBuilder root = QueryBuilders.queryString(queryString);

        SearchResponse resp;
        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setQuery(root).setFrom(offset).setSize(limit);
            
            if (sort.isNonEmpty()) {
                searchRequestBuilder.addSort(sort.getField(), sort.isAscending() ? ASC : DESC);
            }
            
            log.debug("ES query: {}", searchRequestBuilder);
            
            resp = searchRequestBuilder.execute().get();
            
            totalHits = resp.getHits().getTotalHits();
            
            ObjectMapper jsonMapper = new ObjectMapper();
            
            List<Serializable> hits = new ArrayList<>();
            
            for (SearchHit h : resp.getHits().getHits()) {
                log.trace(">>>>>>>>> ");
                log.trace(h.sourceAsString());
                log.trace(">>>>>>>>> ");
                
                String type = h.getType();
                if (type.equals("user")) {
                    User u = jsonMapper.readValue(h.getSourceAsString(), User.class);
                    hits.add(u);
                } else if (type.equals("account")) {
                    Account a = jsonMapper.readValue(h.getSourceAsString(), Account.class);
                    hits.add(a);
                } else if (type.equals("transaction")) {
                    Transaction t = jsonMapper.readValue(h.getSourceAsString(), Transaction.class);
                    hits.add(t);
                } else {
                    log.warn("unmapped hit type: {}", type);
                }
            }  
            
            this.hits = hits;
        } catch (Exception e) {
            log.error("error searching", e);
            this.hits = null;
            this.totalHits = 0;
            throw new RuntimeException(e);
        } finally {
            client.close();
        }
        
    }
    
    @Override
    public void detach() {
    }

    @Override
    public Iterator<Serializable> iterator(long first, long count) {
        if (hits == null  ||  first != offset) {
            offset = first;
            doSearch((int) offset, (int) count);
        }             
        return hits.iterator();
    }

    @Override
    public long size() {
        return totalHits;
    }

    @Override
    public IModel<Serializable> model(Serializable object) {
        return Model.of(object);
    }

    public boolean isSorting() {
        return sort.isNonEmpty();
    }

    public String getSortField() {
        return sort.getField();
    }

    public boolean isAscending() {
        return sort.isAscending();
    }
    
}
package com.cubeia.games.poker.admin.wicket.search;

import com.cubeia.network.shared.web.wicket.search.SearchResultPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.cubeia.games.poker.admin.wicket.pages.util.LinkFactory;

@SuppressWarnings("serial")
public class AccountPanel extends SearchResultPanel<Account> {

    public AccountPanel(String id, IModel<Account> model) {
        super(id, new CompoundPropertyModel<>(model.getObject()));

        add(LinkFactory.accountDetailsLink("accountLink", model.getObject().getAccountId()));
        
        add(new Label("accountId"));
        add(LinkFactory.userDetailsLink("userLink", model.getObject().getUserId()).add(new Label("userId")));
        add(new Label("name"));
        add(new Label("status"));
        add(new Label("type"));
        add(new Label("currencyCode"));
        
        add(new AttributesPanel("attributes", Model.ofMap(model.getObject().getAttributes())));
    }

}

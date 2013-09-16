package com.cubeia.games.poker.admin.wicket.search;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class AccountPanel extends Panel {

	public AccountPanel(String id, IModel<Account> model) {
		super(id, new CompoundPropertyModel<>(model.getObject()));
		
		add(new Label("accountId"));
		add(new Label("userId"));
		add(new Label("name"));
		add(new Label("status"));
		add(new Label("type"));
		add(new Label("currencyCode"));
	}

}

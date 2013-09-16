package com.cubeia.games.poker.admin.wicket.search;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import com.cubeia.games.poker.admin.wicket.pages.util.LinkFactory;

@SuppressWarnings("serial")
public class UserPanel extends Panel {

	public UserPanel(String id, IModel<User> model) {
		super(id, new CompoundPropertyModel<>(model.getObject()));
		
        add(LinkFactory.userDetailsLink("userLink", model.getObject().getUserId()));
        add(new Label("userId"));
		add(new Label("externalUserId"));
		add(new Label("operatorId"));
		add(new Label("userName"));
		add(new Label("status"));
		add(new Label("userType"));
		
		add(new Label("userInformation.firstName"));
		add(new Label("userInformation.lastName"));
		add(new Label("userInformation.email"));
	}

}

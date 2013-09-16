package com.cubeia.games.poker.admin.wicket.search;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import com.cubeia.games.poker.admin.wicket.search.Transaction.Entry;

@SuppressWarnings("serial")
public class TransactionPanel extends Panel {

	public TransactionPanel(String id, IModel<Transaction> model) {
		super(id, new CompoundPropertyModel<>(model.getObject()));
		
		add(new Label("transactionId"));
		add(new Label("externalId"));
		add(new Label("timestamp"));
		add(new Label("comment"));
		
		ListView<Entry> entries = new ListView<Entry>("entries", model.getObject().getEntries()) {
			@Override
			protected void populateItem(ListItem<Entry> item) {
				CompoundPropertyModel<Entry> em = new CompoundPropertyModel<Entry>(item.getModel());
				item.setModel(em);
				item.add(new Label("account.accountId"));
				item.add(new Label("account.userId"));
				item.add(new Label("amount"));
			}
		};
		
		add(entries);
	}

}

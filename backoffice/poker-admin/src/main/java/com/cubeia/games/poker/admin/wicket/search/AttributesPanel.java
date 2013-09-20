package com.cubeia.games.poker.admin.wicket.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

@SuppressWarnings("serial")
public class AttributesPanel extends Panel {
    
	public AttributesPanel(String id, final IModel<Map<String, Attribute>> model) {
		super(id, new CompoundPropertyModel<>(model.getObject()));
		
		LoadableDetachableModel<List<Attribute>> valuesModel = new LoadableDetachableModel<List<Attribute>>() {
		    @Override protected List<Attribute> load() {
		        return new ArrayList<Attribute>(model.getObject().values());
		    }
        };
		
        ListView<Attribute> attributes = new ListView<Attribute>("attributes", valuesModel) {
            @Override
            protected void populateItem(ListItem<Attribute> item) {
                item.setModel(new CompoundPropertyModel<>(item.getModel()));
                item.add(new Label("key"));
                item.add(new Label("value"));
            }
        };
		add(attributes);
	}

}

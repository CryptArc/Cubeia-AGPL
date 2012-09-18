package com.cubeia.games.poker.admin.wicket.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class NavMenuItem<T> extends WebMarkupContainer {

	private static final long serialVersionUID = -5874686707122592063L;

	public NavMenuItem(String id, BookmarkablePageLink<T> link) {
		super(id);
		add(link);
		
	}

}

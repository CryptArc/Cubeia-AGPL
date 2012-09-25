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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.cubeia.games.poker.admin.wicket.component.NavMenuItem;
import com.cubeia.games.poker.admin.wicket.jmx.Clients;
import com.cubeia.games.poker.admin.wicket.pages.history.HandHistory;
import com.cubeia.games.poker.admin.wicket.pages.tables.ListTables;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.ListTournaments;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.ListSitAndGoTournaments;

public class MenuPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public MenuPanel(String id, Class<? extends BasePage> currentPageClass) {
        super(id);
        add(createNavMenuItem("home", HomePage.class, currentPageClass));
        add(createNavMenuItem("handHistory", HandHistory.class, currentPageClass));
        add(createNavMenuItem("tables", ListTables.class, currentPageClass));
        add(createNavMenuItem("sitAndGo", ListSitAndGoTournaments.class, currentPageClass));
        add(createNavMenuItem("scheduled", ListTournaments.class, currentPageClass));
        add(createNavMenuItem("clients", Clients.class, currentPageClass));
    }

    private NavMenuItem<String> createNavMenuItem(String id, Class<? extends Page> pageClass, Class<? extends BasePage> currentPageClass) {
        return createNavMenuItem(id, pageClass, currentPageClass, null);
    }

    private NavMenuItem<String> createNavMenuItem(String id, Class<? extends Page> pageClass, Class<? extends BasePage> currentPageClass, PageParameters params) {
        NavMenuItem<String> navMenuItem = new NavMenuItem<String>(id, createPageLink("link", pageClass, currentPageClass, params));
        if (pageClass.equals(currentPageClass)) {
            navMenuItem.add(AttributeModifier.replace("class", "active"));
        }
        return navMenuItem;
    }

    private BookmarkablePageLink<String> createPageLink(String id, Class<? extends Page> pageClass, Class<? extends BasePage> currentPageClass, PageParameters params) {
        BookmarkablePageLink<String> link = null;
        if (params != null) {
            link = new BookmarkablePageLink<String>(id, pageClass, params);
        } else {
            link = new BookmarkablePageLink<String>(id, pageClass);
        }
        return link;
    }
}

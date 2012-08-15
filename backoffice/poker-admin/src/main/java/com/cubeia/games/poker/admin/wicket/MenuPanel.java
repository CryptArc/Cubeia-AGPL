package com.cubeia.games.poker.admin.wicket;

import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.CreateTournament;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.ListTournaments;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.ListSitAndGoTournaments;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.cubeia.games.poker.admin.wicket.component.NavMenuItem;
import com.cubeia.games.poker.admin.wicket.jmx.Clients;
import com.cubeia.games.poker.admin.wicket.pages.history.HandHistory;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.CreateSitAndGo;

public class MenuPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public MenuPanel(String id, Class<? extends BasePage> currentPageClass) {
        super(id);
        add(createNavMenuItem("home", HomePage.class, currentPageClass));
        add(createNavMenuItem("handHistory", HandHistory.class, currentPageClass));
        add(createNavMenuItem("sitAndGo", ListSitAndGoTournaments.class, currentPageClass));
        add(createNavMenuItem("scheduled", ListTournaments.class, currentPageClass));
        add(createNavMenuItem("clients", Clients.class, currentPageClass));
    }

    private NavMenuItem<String> createNavMenuItem(String id, Class<? extends Page> pageClass,
            Class<? extends BasePage> currentPageClass) {

        return createNavMenuItem(id, pageClass, currentPageClass, null);
    }

    private NavMenuItem<String> createNavMenuItem(String id, Class<? extends Page> pageClass,
            Class<? extends BasePage> currentPageClass, PageParameters params) {

        NavMenuItem<String> navMenuItem = new NavMenuItem<String>(id, createPageLink("link", pageClass, currentPageClass, params));

        if (pageClass.equals(currentPageClass)) {
            navMenuItem.add(AttributeModifier.replace("class", "active"));
        }

        return navMenuItem;
    }


    private BookmarkablePageLink<String> createPageLink(String id, Class<? extends Page> pageClass,
            Class<? extends BasePage> currentPageClass, PageParameters params) {
        BookmarkablePageLink<String> link = null;
        if (params != null) {
            link = new BookmarkablePageLink<String>(id, pageClass, params);
        } else {
            link = new BookmarkablePageLink<String>(id, pageClass);
        }

        return link;
    }
}

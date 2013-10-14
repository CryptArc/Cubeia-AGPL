package com.cubeia.games.poker.admin.wicket;


import com.cubeia.games.poker.admin.wicket.pages.system.BroadcastMessage;
import com.cubeia.games.poker.admin.wicket.pages.system.Clients;
import com.cubeia.games.poker.admin.wicket.pages.system.SystemManagement;
import com.cubeia.network.shared.web.wicket.navigation.PageNode;

import java.util.ArrayList;
import java.util.List;

import static com.cubeia.network.shared.web.wicket.navigation.PageNodeUtils.add;
import static com.cubeia.network.shared.web.wicket.navigation.PageNodeUtils.node;

public class SiteMap {

    private final static List<PageNode> pages = new ArrayList<PageNode>();

    static {


        add(pages,"System Management", SystemManagement.class, "icon-hdd",
                node("Shutdown Management", SystemManagement.class, "icon-off"),
                node("Live Players", Clients.class, "icon-user"),
                node("Broadcast Message", BroadcastMessage.class, "icon-bullhorn"));
    }

    public static List<PageNode> getPages() {
        return pages;
    }




}

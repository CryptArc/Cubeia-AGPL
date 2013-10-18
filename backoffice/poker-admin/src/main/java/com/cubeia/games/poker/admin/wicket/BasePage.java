package com.cubeia.games.poker.admin.wicket;

import com.cubeia.network.shared.web.wicket.pages.search.SearchPage;
import com.cubeia.network.shared.web.wicket.navigation.PageNode;
import com.cubeia.network.shared.web.wicket.navigation.PageNodeUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

public abstract class BasePage extends com.cubeia.network.shared.web.wicket.BasePage {

    public BasePage() {
        this(null);
    }

    public BasePage(PageParameters p) {
        super();

    }

    @Override
    public void loadPages(List<PageNode> pages) {
        PageNodeUtils.add(pages, "Home", HomePage.class, "icon-home");
        super.loadPages(pages);
    }
}

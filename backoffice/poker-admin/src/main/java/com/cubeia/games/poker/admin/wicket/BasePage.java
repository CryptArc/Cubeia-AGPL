package com.cubeia.games.poker.admin.wicket;

import com.cubeia.games.poker.admin.wicket.search.SearchPage;
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
        final TextField<String> searchField = new TextField<String>("globalSearchInput", new Model<String>());
        Form<Void> searchForm = new Form<Void>("globalSearchForm") {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                setResponsePage(SearchPage.class, new PageParameters().add(SearchPage.PARAM_QUERY, searchField.getModelObject()));
            }
        };
        add(searchForm);
        searchForm.add(searchField);
    }

    @Override
    public void loadPages(List<PageNode> pages) {
        PageNodeUtils.add(pages, "Home", HomePage.class, "icon-home");
        super.loadPages(pages);
    }
}

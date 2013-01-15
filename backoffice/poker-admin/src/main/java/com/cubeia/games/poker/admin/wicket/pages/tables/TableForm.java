package com.cubeia.games.poker.admin.wicket.pages.tables;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.TimingProfile;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ComponentModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import static java.util.Arrays.asList;


public abstract class TableForm extends Panel {

    @SpringBean
    private AdminDAO adminDAO;

    public TableForm(String id, TableConfigTemplate tableTemplate) {
        super(id);

        Form<TableConfigTemplate>  tableForm = new Form<TableConfigTemplate>("tableForm",
                new CompoundPropertyModel<TableConfigTemplate>(tableTemplate)){
            @Override
            protected void onSubmit() {
                TableConfigTemplate config = getModel().getObject();
                TableForm.this.onSubmit(config);
            }
        };

        tableForm.add(new RequiredTextField<String>("name"));
        tableForm.add(new RequiredTextField<Integer>("ante"));
        tableForm.add(new RequiredTextField<Integer>("smallBlind"));
        tableForm.add(new RequiredTextField<Integer>("bigBlind"));
        tableForm.add(new RequiredTextField<Integer>("minBuyIn"));
        tableForm.add(new RequiredTextField<Integer>("maxBuyIn"));
        tableForm.add(new RequiredTextField<Integer>("seats"));
        tableForm.add(new TextField<Integer>("minTables"));
        tableForm.add(new TextField<Integer>("minEmptyTables"));
        tableForm.add(new DropDownChoice<BetStrategyType>("betStrategy", asList(BetStrategyType.values()), choiceRenderer()));
        tableForm.add(new DropDownChoice<TimingProfile>("timing", adminDAO.getTimingProfiles(), choiceRenderer()));
        tableForm.add(new DropDownChoice<RakeSettings>("rakeSettings", adminDAO.getRakeSettings(), choiceRenderer()));
        tableForm.add(new Button("submitButton",new Model<String>(getActionLabel())));
        add(tableForm);


    }
    protected <T>ChoiceRenderer<T> choiceRenderer() {
        return new ChoiceRenderer<T>("name");
    }

    public String getActionLabel() {
        return "Create";
    }

    protected abstract void onSubmit(TableConfigTemplate config);

}

package com.cubeia.games.poker.admin.wicket.pages.tables;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.network.NetworkClient;
import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.TimingProfile;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.apache.wicket.validation.validator.RangeValidator.minimum;


public abstract class TableForm extends Panel {

    @SpringBean
    private AdminDAO adminDAO;

    @SpringBean
    private NetworkClient networkClient;

    private Integer maxBuyIn = null;

    private static final Logger logger = LoggerFactory.getLogger(TableForm.class);

    public TableForm(String id, TableConfigTemplate tableTemplate) {
        super(id);
        maxBuyIn = tableTemplate.getMaxBuyIn();
        
        Form<TableConfigTemplate> tableForm = new Form<TableConfigTemplate>("tableForm",
                new CompoundPropertyModel<TableConfigTemplate>(tableTemplate)){
            @Override
            protected void onSubmit() {
                TableConfigTemplate config = getModelObject();
                if (maxBuyIn != null) {
                    config.setMaxBuyIn(maxBuyIn); //since int can't be an optional field
                }
                TableForm.this.onSubmit(config);
            }
        };
        final RequiredTextField<Integer> anteField = new RequiredTextField<Integer>("ante");
        final RequiredTextField<Integer> smallBlindField = new RequiredTextField<Integer>("smallBlind");
        final RequiredTextField<Integer> bigBlindField = new RequiredTextField<Integer>("bigBlind");
        final DropDownChoice<BetStrategyType> betStrategy = new DropDownChoice<BetStrategyType>("betStrategy", asList(BetStrategyType.values()), choiceRenderer());
        final RequiredTextField<Integer> minBuyIn = new RequiredTextField<Integer>("minBuyIn");
        final TextField<Integer> maxBuyIn = new TextField<Integer>("maxBuyIn", new PropertyModel<Integer>(this, "maxBuyIn"));

        tableForm.add(new RequiredTextField<String>("name"));
        tableForm.add(anteField);
        tableForm.add(smallBlindField.add(minimum(1)));
        tableForm.add(bigBlindField);
        tableForm.add(minBuyIn);
        tableForm.add(maxBuyIn);
        tableForm.add(new RequiredTextField<Integer>("seats"));
        tableForm.add(new TextField<Integer>("minTables"));
        tableForm.add(new TextField<Integer>("minEmptyTables"));
        tableForm.add(new DropDownChoice<String>("currency", networkClient.getCurrencies(), new ChoiceRenderer<String>()).setRequired(true));
        tableForm.add(betStrategy);
        tableForm.add(new DropDownChoice<TimingProfile>("timing", adminDAO.getTimingProfiles(), choiceRenderer()));
        tableForm.add(new DropDownChoice<RakeSettings>("rakeSettings", adminDAO.getRakeSettings(), choiceRenderer()));
        tableForm.add(new Button("submitButton", new Model<String>(getActionLabel())));
        tableForm.add(new AbstractFormValidator() {

            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent<?>[0];
            }

            @Override
            public void validate(Form<?> form) {
                validateAnteOrBlinds(form);
                validateBuyIns(form);
            }

            private void validateAnteOrBlinds(Form<?> form) {
                int ante = anteField.getConvertedInput();
                int smallBlind = smallBlindField.getConvertedInput();
                int bigBlind = bigBlindField.getConvertedInput();
                logger.debug("sb = " + smallBlind + " bb = " + bigBlind);

                if (ante == 0) {
                    if (smallBlind == 0 || bigBlind == 0) {
                        form.error("Blinds must be defined if ante is 0.", Collections.<String, Object>emptyMap());
                    }
                }
            }

            private void validateBuyIns(Form<?> form) {
                Integer minBuyInValue = minBuyIn.getConvertedInput();
                Integer maxBuyInValue = maxBuyIn.getConvertedInput();
                if (betStrategy.getModelObject() != BetStrategyType.FIXED_LIMIT && maxBuyInValue == null) {
                    form.error("Max Buy-in must be set for NL and PL.", Collections.<String, Object>emptyMap());
                }
                if (maxBuyInValue != null && minBuyInValue != null && maxBuyInValue < minBuyInValue) {
                    form.error("Max Buy-in must not be less than Min Buy-in.", Collections.<String, Object>emptyMap());
                }
            }
        });
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

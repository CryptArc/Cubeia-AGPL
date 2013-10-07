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

package com.cubeia.games.poker.admin.wicket.pages.user;

import static com.cubeia.backoffice.users.api.dto.UserStatus.BLOCKED;
import static com.cubeia.backoffice.users.api.dto.UserStatus.ENABLED;
import static com.cubeia.games.poker.admin.wicket.util.ParamBuilder.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backoffice.users.api.dto.Gender;
import com.cubeia.backoffice.users.api.dto.User;
import com.cubeia.backoffice.users.api.dto.UserStatus;
import com.cubeia.backoffice.users.client.UserServiceClient;
import com.cubeia.games.poker.admin.wicket.BasePage;
/**
 */
@AuthorizeInstantiation({"ROLE_ADMIN"})
public class EditUser extends BasePage {
    public static final String PARAM_USER_ID = "userId";

    private static final long serialVersionUID = 1L;
    
    private static Logger log = LoggerFactory.getLogger(EditUser.class);

    @SpringBean(name="userClient")
    private UserServiceClient userService;
    
    private User user;
    
    private String password1;
    private String password2;
    
    
    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    public EditUser(PageParameters parameters) {
        super(parameters);
    	if (!assertValidUserid(parameters)) {
    		return;
    	}
    	
        final Long userId = parameters.get(PARAM_USER_ID).toLongObject();
        loadFormData(userId);

        if (getUser() == null  ||  getUser().getStatus() == UserStatus.REMOVED) {
            log.debug("user is removed, id = " + userId);
            setInvalidUserResponsePage(userId);
            return;
        }
        
        add(createBlockActionLink(userId));
        
        add(new Link<Void>("removeActionLink") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onClick() {
                userService.setUserStatus(userId, UserStatus.REMOVED);
                setInvalidUserResponsePage(userId);
            }
        });
        
        Form<?> userForm = new Form<Void>("userForm") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSubmit() {
                userService.updateUser(user);
                info("User updated, id = " + user.getUserId());
                loadFormData(userId);
            }
        };
        
        CompoundPropertyModel<?> cpm = new CompoundPropertyModel<EditUser>(this);
        
        userForm.add(new Label(PARAM_USER_ID, cpm.bind("user.userId")));
        userForm.add(new Label("status", cpm.bind("user.status")));
        userForm.add(new TextField<Long>("operatorId", cpm.<Long>bind("user.operatorId")).setEnabled(false));
        userForm.add(new TextField<String>("externalUserId", cpm.<String>bind("user.externalUserId")));
        userForm.add(new TextField<String>("userName", cpm.<String>bind("user.userName")));
        userForm.add(new TextField<String>("firstName", cpm.<String>bind("user.userInformation.firstName")));
        userForm.add(new TextField<String>("lastName", cpm.<String>bind("user.userInformation.lastName")));
        userForm.add(new TextField<String>("email", cpm.<String>bind("user.userInformation.email")).
            add(EmailAddressValidator.getInstance()));
        userForm.add(new TextField<String>("title", cpm.<String>bind("user.userInformation.title")));
        userForm.add(new TextField<String>("city", cpm.<String>bind("user.userInformation.city")));
        userForm.add(new TextField<String>("billingAddress", cpm.<String>bind("user.userInformation.billingAddress")));
        userForm.add(new TextField<String>("fax", cpm.<String>bind("user.userInformation.fax")));
        userForm.add(new TextField<String>("cellphone", cpm.<String>bind("user.userInformation.cellphone")));
        userForm.add(new DropDownChoice<String>(
            "country",
            cpm.<String>bind("user.userInformation.country"),
            Arrays.asList(Locale.getISOCountries()),
            new IChoiceRenderer<String>() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getDisplayValue(String isoCountry) {
                    return new Locale(
                        Locale.ENGLISH.getLanguage(), 
                        (String) isoCountry).getDisplayCountry(Locale.ENGLISH);
                }

                @Override
                public String getIdValue(String isoCountry, int id) {
                    return "" + id;
                }
            }));
        
        userForm.add(new TextField<String>("zipcode", cpm.<String>bind("user.userInformation.zipcode")));
        userForm.add(new TextField<String>("state", cpm.<String>bind("user.userInformation.state")));
        userForm.add(new TextField<String>("phone", cpm.<String>bind("user.userInformation.phone")));
        userForm.add(new TextField<String>("workphone", cpm.<String>bind("user.userInformation.workphone")));
        userForm.add(new DropDownChoice<Gender>(
            "gender", 
            cpm.<Gender>bind("user.userInformation.gender"), 
            Arrays.asList(Gender.values())));
        add(userForm);
        
        
        initializeAttributeEditor();
        
        
        Form<?> pwdForm = new Form<Void>("changePasswordForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                userService.updatePassword(user.getUserId(), getPassword1());
                setPassword1(null);
                setPassword2(null);
                info("Password updated");
            }
        };
        PasswordTextField pwd1 = new PasswordTextField("password1", cpm.<String>bind("password1"));
        PasswordTextField pwd2 = new PasswordTextField("password2", cpm.<String>bind("password2"));
        pwdForm.add(new EqualPasswordInputValidator(pwd1, pwd2));
        pwdForm.add(pwd1);
        pwdForm.add(pwd2);
        add(pwdForm);
        
        
        
        add(new FeedbackPanel("feedback"));
    }

    @SuppressWarnings("serial")
    private void initializeAttributeEditor() {
        final WebMarkupContainer container = new WebMarkupContainer("attributeEditContainer");
        container.setOutputMarkupId(true);
        add(container);
        
        final FeedbackPanel attribFeedback = new FeedbackPanel("attrFeedback", new ContainerFeedbackMessageFilter(container));
        attribFeedback.setOutputMarkupId(true);
        container.add(attribFeedback);
        
        final WebMarkupContainer attributeListContainer = new WebMarkupContainer("attributeListContainer");
        attributeListContainer.setOutputMarkupId(true);
        container.add(attributeListContainer);
        attributeListContainer.add(createAttributesListView(container, attribFeedback));
        
        Form<?> addAttributeForm = new Form<Void>("addAttrForm");
        addAttributeForm.setOutputMarkupId(true);
        final Model<String> newAttribKeyModel = new Model<String>();
        final Model<String> newAttribValueModel = new Model<String>();
        
        
        addAttributeForm.add(new AjaxSubmitLink("addAttrLink", addAttributeForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if(user.getAttributes() == null) {
                    user.setAttributes(new HashMap<String, String>());
                }
                if(newAttribKeyModel.getObject() != null) {
                    user.getAttributes().put(newAttribKeyModel.getObject(), newAttribValueModel.getObject());
                    userService.updateUser(user);
                    info("Added or updated attribute");
                    newAttribKeyModel.setObject(null);
                    newAttribValueModel.setObject(null);
                }
                target.add(container);
            }
            
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(container);
            }
            
        });
        addAttributeForm.add(new RequiredTextField<String>("newAttrKey", newAttribKeyModel).setLabel(Model.of("Attribute Key")));
        addAttributeForm.add(new TextField<String>("newAttrValue", newAttribValueModel));
        
        container.add(addAttributeForm);
    }

    private boolean assertValidUserid(PageParameters params) {
    	try {
    		params.get(PARAM_USER_ID).toLongObject();
    		return true;
    	} catch (Exception e) {
    		setResponsePage(InvalidUser.class, params(InvalidUser.PARAM_USER_ID, params.get(PARAM_USER_ID).toString()));
    		return false;
    	}
	}

	private void setInvalidUserResponsePage(final Long userId) {
        setResponsePage(InvalidUser.class, params(InvalidUser.PARAM_USER_ID, userId));
    }

    private Link<?> createBlockActionLink(final Long userId) {
        Link<?> blockActionLink = new Link<Void>("blockActionLink") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onClick() {
                if ((user.getStatus() == ENABLED)) {
                    info("User blocked");
                    userService.setUserStatus(userId, BLOCKED);
                } else if ((user.getStatus() == BLOCKED)) {
                    info("User unblocked");
                    userService.setUserStatus(userId, ENABLED);
                }
                
                loadFormData(userId);
            }
        };
        
        blockActionLink.add(new Label("blockActionLabel", new Model<String>() {
            private static final long serialVersionUID = 1L;
            @Override
            public String getObject() {
                UserStatus status = getUser().getStatus();
                if ((status == ENABLED)) {
                    return "Block user";
                } else if ((status == BLOCKED)) {
                    return "Unblock user";
                } 
                throw new IllegalStateException("user should not be visible here! Id = " + userId);
            }
        }));
        return blockActionLink;
    }

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    private void loadFormData(Long userId) {
        user = userService.getUserById(userId);
    }
    
    @Override
    public String getPageTitle() {
        return "Edit user: " + user.getUserName() + " (" + user.getUserId() + ")";
    }
    
    private ListView<String[]> createAttributesListView(final WebMarkupContainer container, final FeedbackPanel attribFeedback) {
        Model<ArrayList<String[]>> attributeModel = new Model<ArrayList<String[]>>() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public ArrayList<String[]> getObject() {
                ArrayList<String[]> keyValueList = new ArrayList<String[]>();
                
                if(user.getAttributes() != null){                
	                ArrayList<String> keysSorted = new ArrayList<String>(user.getAttributes().keySet());
	                Collections.sort(keysSorted);
	                for (String key : keysSorted) {
	                	if(key != null) {
	                		keyValueList.add(new String[] {key, user.getAttributes().get(key)});
	                	}
	                }
                }
                return keyValueList;
            }
        };
        
        return new ListView<String[]>("attributes", attributeModel) {
            private static final long serialVersionUID = 1L;

			@SuppressWarnings({ "serial", "rawtypes" })
            @Override 
			protected void populateItem(ListItem<String[]> item) {
				final String[] keyValue = item.getModelObject();
				final String key = keyValue[0];
                if(key != null){
				    Form form = new Form("attribLineForm");
				    item.add(form);
				    
				    form.add(new Label("key", key));
				    final TextField<String> valueField = new TextField<String>("value", new PropertyModel<String>(EditUser.this, "user.attributes["+ key + "]"));
                    form.add(valueField);
                	
				    form.add(new AjaxButton("saveAttributeButton") {
				        protected void onSubmit(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
                            user.getAttributes().put(key, valueField.getModelObject());
                            userService.updateUser(user);
                            container.info("Attribute updated");
                            target.add(container);
				        };
                	});
				    form.add(new AjaxButton("deleteAttributeButton") {
                        @Override
                        protected void onSubmit(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
                            user.getAttributes().remove(key);
                            userService.updateUser(user);
                            container.info("Attribute deleted");
                            target.add(container);
                        }
                    });
				}
			}	
        };
    }

    
}

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

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.springframework.stereotype.Component;

import com.cubeia.games.poker.admin.wicket.login.LoginPage;
import com.cubeia.games.poker.admin.wicket.login.LogoutPage;
import com.cubeia.games.poker.admin.wicket.pages.history.HandHistory;
import com.cubeia.games.poker.admin.wicket.pages.operator.OperatorList;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.CreateTournament;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.EditTournament;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.ListTournaments;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.CreateSitAndGo;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.EditSitAndGo;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.ListSitAndGoTournaments;
import com.cubeia.games.poker.admin.wicket.pages.user.CreateUser;
import com.cubeia.games.poker.admin.wicket.pages.user.EditUser;
import com.cubeia.games.poker.admin.wicket.pages.user.UserList;
import com.cubeia.games.poker.admin.wicket.pages.user.UserSummary;

@Component("wicketApplication")
public class PokerAdminWebApplication extends AuthenticatedWebApplication {

	private static final Logger log = Logger.getLogger(AuthenticatedWebApplication.class);

	/**
	 * Constructor
	 */
	public PokerAdminWebApplication() {}

	@Override
	protected void init() {
		getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND); // For dev only, delete when ready for production.
		getMarkupSettings().setStripWicketTags(true);
		getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this));
		getSecuritySettings().setUnauthorizedComponentInstantiationListener(new IUnauthorizedComponentInstantiationListener() {

			@Override
			public void onUnauthorizedInstantiation(org.apache.wicket.Component component) {
				if (component instanceof Page) {
					throw new RestartResponseAtInterceptPageException(LoginPage.class);
				} else {
					throw new UnauthorizedInstantiationException(component.getClass());
				}

			}
		});
		// Initialize Spring
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		mountPages();
	}

	private void mountPages() {
		mountPage("/home", HomePage.class);
		mountPage("/login", LoginPage.class);
		mountPage("/logout", LogoutPage.class);
		mountPage("/hand-history", HandHistory.class);
		
		mountPage("/tournament", ListTournaments.class);
		mountPage("/tournament/create", CreateTournament.class);
		mountPage("/tournament/edit", EditTournament.class);
		
		mountPage("/sitandgo", ListSitAndGoTournaments.class);
		mountPage("/sitandgo/create", CreateSitAndGo.class);
		mountPage("/sitandgo/edit", EditSitAndGo.class);
		
		mountPage("/operator", OperatorList.class);
		
		mountPage("/user", UserList.class);
		mountPage("/user/edit", EditUser.class);
		mountPage("/user/create", CreateUser.class);
		mountPage("/user/details", UserSummary.class);
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected IConverterLocator newConverterLocator() {
		ConverterLocator converterLocator = new ConverterLocator();
		converterLocator.set(BigDecimal.class, new CustomBigDecimalConverter());
		return converterLocator;
	}

	/**
	 * NOTE: this methods is never called for some reason. I have applied a unauthorized listener above
	 * to haxxor around this.
	 */
	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		log.debug("Get authenticated web session");
		return SecureWicketAuthenticatedWebSession.class;
	}

}

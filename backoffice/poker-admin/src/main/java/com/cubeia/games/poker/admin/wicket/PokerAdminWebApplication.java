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

import com.cubeia.games.poker.admin.wicket.login.LoginPage;
import com.cubeia.network.shared.web.wicket.BaseApplication;
import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.springframework.stereotype.Component;

@Component("wicketApplication")
public class PokerAdminWebApplication extends BaseApplication {

    /**
     * Constructor
     */
    public PokerAdminWebApplication() {}

	private static final Logger log = Logger.getLogger(AuthenticatedWebApplication.class);
    /*
     private void mountPages() {

         for(AdminWebModule module : modules) {
             List<PageNode> pages = module.getPages();
             mountPages("", pages);
         }

         mountPage("/home", HomePage.class);
         mountPage("/login", LoginPage.class);
         mountPage("/logout", LogoutPage.class);
         mountPage("/search", SearchPage.class);

         mountPage("/hand-history", HandHistory.class);

         mountPage("/tournament", ListTournaments.class);
         mountPage("/tournament/create", CreateTournament.class);
         mountPage("/tournament/edit", EditTournament.class);

         mountPage("/sitandgo", ListSitAndGoTournaments.class);
         mountPage("/sitandgo/create", CreateSitAndGo.class);
         mountPage("/sitandgo/edit", EditSitAndGo.class);



         mountPage("/user", UserList.class);
         mountPage("/user/edit", EditUser.class);
         mountPage("/user/create", CreateUser.class);
         mountPage("/user/details", UserSummary.class);

         mountPage("/account", AccountList.class);
         mountPage("/account/detail", AccountDetails.class);
         mountPage("/account/create", CreateAccount.class);

         mountPage("/currency", EditCurrencies.class);

         mountPage("/transaction", TransactionList.class);
         mountPage("/transaction/create", CreateTransaction.class);
         mountPage("/transaction/info", TransactionInfo.class);

		
	}
        */

    @Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}



	/**
	 * NOTE: this methods is never called for some reason. I have applied a unauthorized listener above
	 * to haxxor around this.
	 */
	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}


}

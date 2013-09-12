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

package com.cubeia.games.poker.admin.wicket.login;

import org.apache.wicket.authroles.authentication.pages.SignOutPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.cubeia.games.poker.admin.wicket.PokerAdminWebApplication;

public class LogoutPage extends SignOutPage {

	private static final long serialVersionUID = 1L;

	public LogoutPage() {
        this(null);
    }
    
    public LogoutPage(PageParameters params) {
        super(params);
        setResponsePage(PokerAdminWebApplication.get().getHomePage());
    }
    
}

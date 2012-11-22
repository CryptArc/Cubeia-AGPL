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

package com.cubeia.games.poker;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

public class CreateUser {

	public static void main(String[] args) {
		CreateUser action = new CreateUser();
		try {
			Args.parse(action, args);
			action.execute();
		} catch(IllegalArgumentException e) {
			Args.usage(action);
		} catch(Exception e) {
			System.out.println("!!! FAILURE !!!");
			e.printStackTrace();
		}
	}
	
	@Argument(alias="us", description="user service URL, defaults to http://localhost:8080/user-service-rest/rest", required=false)
	private String userService = "http://localhost:8080/user-service-rest/rest";
	
	@Argument(alias="ws", description="wallet service URL, defaults to http://localhost:8080/wallet-service-rest/rest", required=false)
	private String walletService = "http://localhost:8080/wallet-service-rest/rest";
	
	@Argument(alias="c", description="currency, defaults to EUR", required=false)
	private String currency = "EUR";
	
	@Argument(alias="u", description="username, required", required=true)
	private String username;
	
	@Argument(alias="p", description="password, required", required=true)
	private String password;
	
	@Argument(alias="f", description="first name, optional", required=false)
	private String firstname;
	
	@Argument(alias="l", description="last name, option", required=false)
	private String lastname;
	
	@Argument(alias="i", description="initial balance, set to -1 to disable, defaults to 50000", required=false)
	private Long balance = 50000L;
	
	@Argument(alias="b", description="bank account for initial balance, defaults to -3000", required=false)
	private Long bankaccount = -3000L;
	
	public void execute() throws Exception {
		Request req = new Request(username, password);
		req.setBalance(balance);
		req.setBankaccount(bankaccount);
		req.setFirstname(firstname);
		req.setLastname(lastname);
		req.setCurrency(currency);
		Connector con = new Connector(userService, walletService);
		con.createUser(req);
	}

	@Override
	public String toString() {
		return "CreateUser [userService=" + userService + ", walletService="
				+ walletService + ", currency=" + currency + ", username="
				+ username + ", password=" + password + ", firstname="
				+ firstname + ", lastname=" + lastname + "]";
	}
}

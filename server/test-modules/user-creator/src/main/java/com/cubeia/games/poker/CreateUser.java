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

import static com.cubeia.backoffice.wallet.api.dto.Account.AccountType.STATIC_ACCOUNT;

import java.math.BigDecimal;
import java.util.UUID;

import com.cubeia.backoffice.accounting.api.Money;
import com.cubeia.backoffice.users.api.dto.CreateUserRequest;
import com.cubeia.backoffice.users.api.dto.CreateUserResponse;
import com.cubeia.backoffice.users.api.dto.CreationStatus;
import com.cubeia.backoffice.users.api.dto.User;
import com.cubeia.backoffice.users.api.dto.UserInformation;
import com.cubeia.backoffice.users.client.UserServiceClientHTTP;
import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.MetaInformation;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionEntry;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionRequest;
import com.cubeia.backoffice.wallet.api.dto.request.CreateAccountRequest;
import com.cubeia.backoffice.wallet.client.WalletServiceClientHTTP;
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
	private long balance = 50000;
	
	@Argument(alias="b", description="bank account for initial balance, defaults to -3000", required=false)
	private long bankaccount = -3000;
	
	public void execute() throws Exception {
		long userId = tryCreateUser();
		System.out.println("User " + userId + " created.");
		long accountId = tryCreateAccounts(userId);
		System.out.println("User " + userId + " get main account " + accountId);
		long transactionId = tryInitialAmount(accountId, userId);
		if(transactionId != -1) {
			System.out.println("User " + userId + " got initial balance " + balance);
		}
	}
	
	private long tryInitialAmount(long accountId, long userId) throws Exception {
		if(balance > 0) {
			WalletServiceClientHTTP client = new WalletServiceClientHTTP(walletService);
			TransactionRequest req = new TransactionRequest();
			Money credit = new Money(currency, 2, new BigDecimal(String.valueOf(balance)));
			req.getEntries().add(new TransactionEntry(accountId, credit));
			Account acc = client.getAccount(bankaccount, "EUR");
			req.getEntries().add(new TransactionEntry(acc.getId(), credit.negate()));
			req.setComment("initial balance for user " + userId);
			return client.doTransaction(req).getTransactionId();
		} else {
			return -1;
		}
	}

	private long tryCreateAccounts(long userId) throws Exception {
		CreateAccountRequest req = new CreateAccountRequest();
		req.setNegativeBalanceAllowed(false);
		req.setRequestId(UUID.randomUUID());
		req.setUserId(userId);
		req.setCurrencyCode(currency);
		req.setType(STATIC_ACCOUNT);
		MetaInformation inf = new MetaInformation();
		inf.setName("User " + userId + " Main Account");
		req.setInformation(inf);
		WalletServiceClientHTTP client = new WalletServiceClientHTTP(walletService);
		return client.createAccount(req).getAccountId();
	}

	private long tryCreateUser() throws Exception {
		User u = new User();
		u.setUserName(username);
		UserInformation ui = new UserInformation();
		ui.setFirstName(firstname);
		ui.setLastName(lastname);
		u.setUserInformation(ui);
		u.setOperatorId(0L);
		u.setExternalUserId("");
		// System.out.println(userService);
		UserServiceClientHTTP userClient = new UserServiceClientHTTP(userService);
		CreateUserResponse resp = userClient.createUser(new CreateUserRequest(u, password));
		if(resp.getStatus() == CreationStatus.OK) {
			return resp.getUser().getUserId();
		} else {
			throw new IllegalStateException("Failed to create user: " + resp.getStatus());
		}
	}

	@Override
	public String toString() {
		return "CreateUser [userService=" + userService + ", walletService="
				+ walletService + ", currency=" + currency + ", username="
				+ username + ", password=" + password + ", firstname="
				+ firstname + ", lastname=" + lastname + "]";
	}
}

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
import com.cubeia.backoffice.users.api.dto.UserQueryResult;
import com.cubeia.backoffice.users.client.UserServiceClientHTTP;
import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.MetaInformation;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionEntry;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionRequest;
import com.cubeia.backoffice.wallet.api.dto.request.CreateAccountRequest;
import com.cubeia.backoffice.wallet.client.WalletServiceClientHTTP;

public class Connector {
	
	private WalletServiceClientHTTP walletClient;
	private UserServiceClientHTTP userClient;

	public Connector(String userServiceUrl, String walletServiceUrl) {
		walletClient = new WalletServiceClientHTTP(walletServiceUrl);
		userClient = new UserServiceClientHTTP(userServiceUrl);
	}
	
	public void topupUser(Request req) throws Exception {
		long userId = tryGetUser(req);
		System.out.println("User " + userId + " found.");
		long transactionId = tryTopupAmount(userId, req);
		if(transactionId != -1) {
			System.out.println("User " + userId + " got his balance toppued up with " + req.getBalance());
		}
	}

	public void createUser(Request req) throws Exception {
		long userId = tryCreateUser(req);
		System.out.println("User " + userId + " created.");
		long accountId = tryCreateAccounts(userId, req);
		System.out.println("User " + userId + " get main account " + accountId);
		long transactionId = tryInitialAmount(accountId, userId, req);
		if(transactionId != -1) {
			System.out.println("User " + userId + " got initial balance " + req.getBalance());
		}
	}
	
	private long tryTopupAmount(long userId, Request tmp) {
		Account account = walletClient.getAccount(userId, tmp.getCurrency());
		TransactionRequest req = new TransactionRequest();
		Money credit = new Money(tmp.getCurrency(), 2, new BigDecimal(String.valueOf(tmp.getBalance())));
		req.getEntries().add(new TransactionEntry(account.getId(), credit));
		Account acc = walletClient.getAccount(tmp.getBankaccount(), "EUR");
		req.getEntries().add(new TransactionEntry(acc.getId(), credit.negate()));
		req.setComment("toput balance for user " + userId);
		return walletClient.doTransaction(req).getTransactionId();
	}

	private long tryGetUser(Request req) {
		UserQueryResult res = userClient.findUsers(null, req.getUsername(), 0, 1, null, false);
		if(res.getTotalQueryResultSize() == 0) {
			throw new IllegalStateException("Could not find user: " + req.getUsername());
		} else {
			return res.getUsers().get(0).getUserId();
		}
	}

	private long tryInitialAmount(long accountId, long userId, Request tmp) throws Exception {
		if(tmp.getBalance() > 0) {
			TransactionRequest req = new TransactionRequest();
			Money credit = new Money(tmp.getCurrency(), 2, new BigDecimal(String.valueOf(tmp.getBalance())));
			req.getEntries().add(new TransactionEntry(accountId, credit));
			Account acc = walletClient.getAccount(tmp.getBankaccount(), "EUR");
			req.getEntries().add(new TransactionEntry(acc.getId(), credit.negate()));
			req.setComment("initial balance for user " + userId);
			return walletClient.doTransaction(req).getTransactionId();
		} else {
			return -1;
		}
	}

	private long tryCreateAccounts(long userId, Request tmp) throws Exception {
		CreateAccountRequest req = new CreateAccountRequest();
		req.setNegativeBalanceAllowed(false);
		req.setRequestId(UUID.randomUUID());
		req.setUserId(userId);
		req.setCurrencyCode(tmp.getCurrency());
		req.setType(STATIC_ACCOUNT);
		MetaInformation inf = new MetaInformation();
		inf.setName("User " + userId + " Main Account");
		req.setInformation(inf);
		return walletClient.createAccount(req).getAccountId();
	}

	private long tryCreateUser(Request req) throws Exception {
		User u = new User();
		u.setUserName(req.getUsername());
		UserInformation ui = new UserInformation();
		ui.setFirstName(req.getFirstname());
		ui.setLastName(req.getLastname());
		u.setUserInformation(ui);
		u.setOperatorId(0L);
		u.setExternalUserId("");
		u.getAttributes().put("user.userName", req.getUsername());
		u.getAttributes().put("user.firstName", req.getFirstname());
		u.getAttributes().put("user.lastName", req.getLastname());
		// System.out.println(userService);
		CreateUserResponse resp = userClient.createUser(new CreateUserRequest(u, req.getPassword()));
		if(resp.getStatus() == CreationStatus.OK) {
			return resp.getUser().getUserId();
		} else {
			throw new IllegalStateException("Failed to create user: " + resp.getStatus());
		}
	}
}

package com.cubeia.games.poker.common.accounts;

import com.cubeia.backoffice.wallet.api.config.AccountRole;
import com.cubeia.backoffice.wallet.client.WalletServiceClient;
import com.cubeia.backoffice.wallet.client.WalletServiceClientHTTP;

public class RunnableClient {
	
	public static void main(String[] args) {
		WalletServiceClient walletClient = new WalletServiceClientHTTP("http://localhost:9091/wallet-service-rest/rest");
		AccountLookup accounts = new AccountLookup(walletClient);
		
		long id = accounts.lookupOperatorAccount(1L, "EUR", AccountRole.RAKE);
		System.out.println("Operator[1] Rake Account ID (EUR): "+id);
		
		id = accounts.lookupOperatorAccount(1L, "EUR", AccountRole.RAKE);
		System.out.println("Operator[1] Currency[EUR] Rake Account ID: "+id);
		
		id = accounts.lookupSystemAccount("EUR", AccountRole.MAIN);
		System.out.println("System Currency[EUR] Role[MAIN]: "+id);
		id = accounts.lookupSystemAccount("EUR", AccountRole.MAIN);
		System.out.println("System Currency[EUR] Role[MAIN]: "+id);
		
		id = accounts.lookupPlayerAccount(11121L, "EUR", AccountRole.MAIN);
		System.out.println("Player[1] Currency[EUR] Role[Main]: "+id);
		
		id = accounts.lookupPlayerAccount(11121L, "EUR", AccountRole.BONUS);
		System.out.println("Player[1] Currency[EUR] Role[Bonus]: "+id);
		id = accounts.lookupPlayerAccount(11121L, "EUR", AccountRole.BONUS);
		System.out.println("Player[1] Currency[EUR] Role[Bonus]: "+id);
	}
	
}	

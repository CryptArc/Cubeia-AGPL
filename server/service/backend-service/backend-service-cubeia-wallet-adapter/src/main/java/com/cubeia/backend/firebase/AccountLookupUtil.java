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

package com.cubeia.backend.firebase;

import static com.cubeia.backoffice.wallet.api.dto.Account.AccountType.STATIC_ACCOUNT;
import static com.cubeia.backoffice.wallet.api.dto.Account.AccountType.SYSTEM_ACCOUNT;
import static java.util.Arrays.asList;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backoffice.accounting.api.NoSuchAccountException;
import com.cubeia.backoffice.wallet.api.config.AccountAttributes;
import com.cubeia.backoffice.wallet.api.config.AccountRole;
import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountStatus;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountType;
import com.cubeia.backoffice.wallet.api.dto.AccountQueryResult;
import com.cubeia.backoffice.wallet.api.dto.exception.TooManyAccountsFoundException;
import com.cubeia.backoffice.wallet.api.dto.request.AccountQuery;
import com.cubeia.backoffice.wallet.api.dto.request.ListAccountsRequest;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class AccountLookupUtil {

	Logger log = LoggerFactory.getLogger(getClass());
	
	
	private LoadingCache<AccountKey, Long> accountIdCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<AccountKey, Long>() {
                public Long load(AccountKey key) {
                    return lookupAccountId(key);
                }
            });


	private final WalletServiceContract walletService;
	
    public AccountLookupUtil(WalletServiceContract walletService) {
		this.walletService = walletService;
	}

	public long lookupPromotionsAccountId(WalletServiceContract walletService, String currencyCode) throws SystemException {
        return lookupSystemAccount(walletService, currencyCode, CashGamesBackendAdapter.PROMOTIONS_ACCOUNT_USER_ID);
    }

	public long lookupRakeAccountId(WalletServiceContract walletService, String currency) throws SystemException {
    	log.debug("Lookup rake account for currency["+currency+"] and userId["+currency+"]");
        return lookupSystemAccount(walletService, currency, CashGamesBackendAdapter.RAKE_ACCOUNT_USER_ID);
    }

    private long lookupSystemAccount(WalletServiceContract walletService, String currency, Long accountUserId) throws SystemException {
        ListAccountsRequest request = new ListAccountsRequest();
        request.setStatus(AccountStatus.OPEN);
        request.setTypes(asList(SYSTEM_ACCOUNT));
        request.setUserId(accountUserId);
        request.setLimit(100);
        AccountQueryResult accounts = walletService.listAccounts(request);
        for (Account account : accounts.getAccounts()) {
            if (account.getCurrencyCode().equals(currency)) {
                return account.getId();
            }
        }
        throw new SystemException("Error getting rake account for currency " + currency + ". Looked for account matching: " + request);
    }

    /**
     * Gets the account id for the account with the given playerId and currency code.
     *
     * @param walletService the service to use for doing the remote call
     * @param playerId the id of the player who owns the account
     * @param currency the currency code that the account should have
     * @return the accountId of the matching account, or -1 if none found
     */
    public long lookupAccountIdForPlayerAndCurrency(WalletServiceContract walletService, long playerId, String currency) {
        ListAccountsRequest request = new ListAccountsRequest();
        request.setStatus(AccountStatus.OPEN);
        request.setTypes(asList(STATIC_ACCOUNT));
        request.setUserId(playerId);
        request.setLimit(100);
        AccountQueryResult accounts = walletService.listAccounts(request);
        if (accounts.getAccounts() == null || accounts.getAccounts().size() < 1) {
            return -1;
        }

        for (Account account : accounts.getAccounts()) {
            if (account.getCurrencyCode().equals(currency)) {
                return account.getId();
            }
        }
        return -1;
    }
    
    /**
     * Look up rake account for operator.
     * 
     * @param walletService
     * @param operatorId
     * @param currencyCode
     * @return Account id
     * @throws NoSuchAccountException if no account found
     * @throws RuntimeException if multiple accounts found for the query
     */
    public long lookupOperatorRakeAccountId(long operatorId, String currencyCode) {
    	log.debug("Lookup Operator Rake account. Operator["+operatorId+"] currency["+currencyCode+"]");
    	long accountId = lookupUniqueRakeAccountId(operatorId, null, currencyCode, AccountType.OPERATOR_ACCOUNT);
		log.debug("Lookup Operator Rake account. Operator["+operatorId+"] currency["+currencyCode+"], Result: "+accountId);
		return accountId;
    }

    /**
     * Lookup any account with ROLE=RAKE
     * 
     * @param walletService
     * @param operatorId
     * @param userId
     * @param currencyCode
     * @param type
     * @return Account id
     * @throws NoSuchAccountException if no account found
     * @throws RuntimeException if multiple accounts found for the query
     */
    public long lookupUniqueRakeAccountId(Long operatorId, Long userId, String currencyCode, AccountType type) {
    	AccountKey key = new AccountKey();
    	key.currencyCode = currencyCode;
    	key.operatorId = operatorId;
    	key.type = type;
    	key.userId = userId;
    	try {
			return accountIdCache.get(key);
		} catch (Exception e) {
			Throwables.propagateIfPossible(e.getCause(), NoSuchAccountException.class, TooManyAccountsFoundException.class);
		    throw new IllegalStateException(e);
		}
    }
    
    
    protected Long lookupAccountId(AccountKey key) {
    	AccountQuery query = new AccountQuery();
    	query.setOperatorId(key.operatorId);
    	query.setCurrency(key.currencyCode);
    	query.setUserId(key.userId);
    	query.setType(key.type.name());
    	query.getAttributes().put(AccountAttributes.ROLE.name(), AccountRole.RAKE.name());
		Account account = walletService.findUniqueAccount(query);
		if (account == null) {
			throw new NoSuchAccountException("No account matches the query: "+query);
		}
		return account.getId();
	}

    
    private class AccountKey {
    	
    	public Long operatorId;
    	public Long userId;
    	public String currencyCode;
    	public AccountType type;
    	
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
			result = prime * result + ((operatorId == null) ? 0 : operatorId.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((userId == null) ? 0 : userId.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AccountKey other = (AccountKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (currencyCode == null) {
				if (other.currencyCode != null)
					return false;
			} else if (!currencyCode.equals(other.currencyCode))
				return false;
			if (operatorId == null) {
				if (other.operatorId != null)
					return false;
			} else if (!operatorId.equals(other.operatorId))
				return false;
			if (type != other.type)
				return false;
			if (userId == null) {
				if (other.userId != null)
					return false;
			} else if (!userId.equals(other.userId))
				return false;
			return true;
		}
		
		private AccountLookupUtil getOuterType() {
			return AccountLookupUtil.this;
		}
    	
    }
    
}

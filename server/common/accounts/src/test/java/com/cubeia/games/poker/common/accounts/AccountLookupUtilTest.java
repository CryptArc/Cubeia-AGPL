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

package com.cubeia.games.poker.common.accounts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.backoffice.accounting.api.NoSuchAccountException;
import com.cubeia.backoffice.wallet.api.config.AccountAttributes;
import com.cubeia.backoffice.wallet.api.config.AccountRole;
import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountType;
import com.cubeia.backoffice.wallet.api.dto.exception.TooManyAccountsFoundException;
import com.cubeia.backoffice.wallet.api.dto.request.AccountQuery;
import com.cubeia.backoffice.wallet.client.WalletServiceClient;

public class AccountLookupUtilTest {
    @Mock
    private WalletServiceClient walletClient;

    AccountLookup lookup;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        lookup = new AccountLookup(walletClient);
    }
    
    @Test
    public void testHashcodeAccountQuery() {
    	AccountQuery q1 = new AccountQuery();
    	q1.setOperatorId(1L);
    	q1.getAttributes().put("zoo", "apa");
    	
    	AccountQuery q2 = new AccountQuery();
    	q2.setOperatorId(1L);
    	q2.getAttributes().put("zoo", "apa");
    	
    	AccountQuery q3 = new AccountQuery();
    	q3.setOperatorId(1L);
    	q3.getAttributes().put("zoo", "banan");
    	
    	AccountQuery q4 = new AccountQuery();
    	q4.setOperatorId(1L);
    	
    	AccountQuery q5 = new AccountQuery();
    	q5.setOperatorId(1L);
    	
    	assertTrue(q1.hashCode() == q2.hashCode());
    	assertTrue(q1.equals(q2));
    	
    	assertFalse(q1.hashCode() == q3.hashCode());
    	
    	assertTrue(q4.hashCode() == q5.hashCode());
    	assertTrue(q4.equals(q5));
    	
    	assertFalse(q4.hashCode() == q1.hashCode());
    	assertFalse(q4.equals(q1));
    }

    @Test
    public void testLookupOperatorRakeAccount() {
    	ArgumentCaptor<AccountQuery> requestCaptor = ArgumentCaptor.forClass(AccountQuery.class);
    	
    	Account account = new Account();
    	account.setId(22L);
		when(walletClient.findUniqueAccount(requestCaptor.capture())).thenReturn(account );
    	long id = lookup.lookupOperatorAccount(1L, "EUR", AccountRole.RAKE);
    	assertThat(id, is(22L));
    	
    	AccountQuery query = requestCaptor.getValue();
    	assertThat(query.getCurrency(), is("EUR"));
    	assertThat(query.getOperatorId(), is(1L));
    	assertThat(query.getType(), is(AccountType.OPERATOR_ACCOUNT.name()));
    	assertThat(query.getAttributes().get(AccountAttributes.ROLE.name()), is(AccountRole.RAKE.name()));
    }
    
    @Test (expected=NoSuchAccountException.class)
    public void testLookupOperatorRakeAccountNotFound() {
    	when(walletClient.findUniqueAccount(Mockito.any(AccountQuery.class))).thenThrow(new NoSuchAccountException("not found"));
    	lookup.lookupOperatorAccount(1L, "EUR", AccountRole.RAKE);
    }
    
    @Test (expected=TooManyAccountsFoundException.class)
    public void testLookupOperatorRakeAccountNotUnique() {
    	when(walletClient.findUniqueAccount(Mockito.any(AccountQuery.class))).thenThrow(new TooManyAccountsFoundException("more than one"));
    	lookup.lookupOperatorAccount(1L, "EUR", AccountRole.RAKE);
    }
    
    @Test
    public void testLookupPlayerAccount() {
    	ArgumentCaptor<AccountQuery> requestCaptor = ArgumentCaptor.forClass(AccountQuery.class);
    	Account account = new Account();
    	account.setId(22L);
    	when(walletClient.findUniqueAccount(requestCaptor.capture())).thenReturn(account);
    	
    	long id = lookup.lookupPlayerAccount(123L, "XCC", AccountRole.BONUS);
    	assertThat(id, is(22L));
    	
    	AccountQuery query = requestCaptor.getValue();
    	assertThat(query.getCurrency(), is("XCC"));
    	assertThat(query.getOperatorId(), nullValue());
    	assertThat(query.getUserId(), is(123L));
    	assertThat(query.getType(), is(AccountType.STATIC_ACCOUNT.name()));
    	assertThat(query.getAttributes().get(AccountAttributes.ROLE.name()), is(AccountRole.BONUS.name()));
    }
    
    @Test
    public void testLookupSystemAccount() {
    	ArgumentCaptor<AccountQuery> requestCaptor = ArgumentCaptor.forClass(AccountQuery.class);
    	Account account = new Account();
    	account.setId(22L);
    	when(walletClient.findUniqueAccount(requestCaptor.capture())).thenReturn(account);
    	
    	long id = lookup.lookupSystemAccount("XCC", AccountRole.RAKE);
    	assertThat(id, is(22L));
    	
    	AccountQuery query = requestCaptor.getValue();
    	assertThat(query.getCurrency(), is("XCC"));
    	assertThat(query.getOperatorId(), nullValue());
    	assertThat(query.getUserId(), nullValue());
    	assertThat(query.getType(), is(AccountType.SYSTEM_ACCOUNT.name()));
    	assertThat(query.getAttributes().get(AccountAttributes.ROLE.name()), is(AccountRole.RAKE.name()));
    }
}


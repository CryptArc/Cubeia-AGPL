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

import static com.cubeia.backoffice.wallet.api.dto.Account.AccountStatus.OPEN;
import static com.cubeia.backoffice.wallet.api.dto.Account.AccountType.SYSTEM_ACCOUNT;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cubeia.backoffice.wallet.api.config.AccountAttributes;
import com.cubeia.backoffice.wallet.api.config.AccountRoles;
import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountStatus;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountType;
import com.cubeia.backoffice.wallet.api.dto.AccountQueryResult;
import com.cubeia.backoffice.wallet.api.dto.request.AccountQuery;
import com.cubeia.backoffice.wallet.api.dto.request.ListAccountsRequest;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;

public class AccountLookupUtilTest {
    @Mock
    private WalletServiceContract walletService;

    AccountLookupUtil lookup;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        lookup = new AccountLookupUtil();
    }

    @Test
    public void testLookupRakeAccountId() throws SystemException {
        AccountLookupUtil acl = new AccountLookupUtil();

        ArgumentCaptor<ListAccountsRequest> requestCaptor = ArgumentCaptor.forClass(ListAccountsRequest.class);

        AccountQueryResult accountQueryResult = mock(AccountQueryResult.class);
        Account rakeAccount = mock(Account.class);
        Long rakeAccountId = -2000L;
        when(rakeAccount.getId()).thenReturn(rakeAccountId);
        when(rakeAccount.getCurrencyCode()).thenReturn("EUR");
        when(accountQueryResult.getAccounts()).thenReturn(asList(rakeAccount));
        when(walletService.listAccounts(requestCaptor.capture())).thenReturn(accountQueryResult);

        long lookupRakeAccountId = acl.lookupRakeAccountId(walletService, "EUR");
        assertThat(lookupRakeAccountId, is(rakeAccountId));

        ListAccountsRequest lar = requestCaptor.getValue();
        assertThat(lar.getStatuses(), is((Collection<AccountStatus>) asList(OPEN)));
        assertThat(lar.getTypes(), is((Collection<AccountType>) asList(SYSTEM_ACCOUNT)));
        assertThat(lar.getUserId(), is(CashGamesBackendAdapter.RAKE_ACCOUNT_USER_ID));
    }

    @Test
    public void testLookupOperatorRakeAccount() {
    	ArgumentCaptor<AccountQuery> requestCaptor = ArgumentCaptor.forClass(AccountQuery.class);
    	
    	Account account = new Account();
    	account.setId(22L);
		when(walletService.findUniqueAccount(requestCaptor.capture())).thenReturn(account );
    	long id = lookup.lookupOperatorRakeAccountId(walletService, 1L, "EUR");
    	assertThat(id, is(22L));
    	
    	AccountQuery query = requestCaptor.getValue();
    	assertThat(query.getCurrency(), is("EUR"));
    	assertThat(query.getOperatorId(), is(1L));
    	assertThat(query.getType(), is(AccountType.OPERATOR_ACCOUNT.name()));
    	assertThat(query.getAttributes().get(AccountAttributes.ROLE.name()), is(AccountRoles.RAKE.name()));
    }
    
}

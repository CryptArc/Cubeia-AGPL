package com.cubeia.backend.firebase;

import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountStatus;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountType;
import com.cubeia.backoffice.wallet.api.dto.AccountQueryResult;
import com.cubeia.backoffice.wallet.api.dto.request.ListAccountsRequest;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;

import static com.cubeia.backoffice.wallet.api.dto.Account.AccountStatus.OPEN;
import static com.cubeia.backoffice.wallet.api.dto.Account.AccountType.SYSTEM_ACCOUNT;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountLookupUtilTest {
    @Mock
    private WalletServiceContract walletService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLookupRakeAccountId() throws SystemException {
        AccountLookupUtil acl = new AccountLookupUtil();

        ArgumentCaptor<ListAccountsRequest> requestCaptor = ArgumentCaptor.forClass(ListAccountsRequest.class);

        AccountQueryResult accountQueryResult = mock(AccountQueryResult.class);
        Account rakeAccount = mock(Account.class);
        Long rakeAccountId = -2000L;
        when(rakeAccount.getId()).thenReturn(rakeAccountId);
        when(accountQueryResult.getAccounts()).thenReturn(asList(rakeAccount));
        when(walletService.listAccounts(requestCaptor.capture())).thenReturn(accountQueryResult);

        long lookupRakeAccountId = acl.lookupRakeAccountId(walletService);
        assertThat(lookupRakeAccountId, is(rakeAccountId));

        ListAccountsRequest lar = requestCaptor.getValue();
        assertThat(lar.getLimit(), is(1));
        assertThat(lar.getStatuses(), is((Collection<AccountStatus>) asList(OPEN)));
        assertThat(lar.getTypes(), is((Collection<AccountType>) asList(SYSTEM_ACCOUNT)));
        assertThat(lar.getUserId(), is(CashGamesBackendAdapter.RAKE_ACCOUNT_USER_ID));
    }

}

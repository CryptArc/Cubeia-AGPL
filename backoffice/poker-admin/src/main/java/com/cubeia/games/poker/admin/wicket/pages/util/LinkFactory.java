package com.cubeia.games.poker.admin.wicket.pages.util;

import static com.cubeia.network.web.wallet.AccountDetails.PARAM_ACCOUNT_ID;
import static com.cubeia.network.web.wallet.TransactionInfo.PARAM_TX_ID;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.cubeia.network.web.user.UserSummary;
import com.cubeia.network.web.wallet.AccountDetails;
import com.cubeia.network.web.wallet.TransactionInfo;

public class LinkFactory {

    public static BookmarkablePageLink<Void> accountDetailsLink(String wicketId, Long accountId) {
        return new BookmarkablePageLink<>(wicketId, AccountDetails.class, 
            new PageParameters().add(PARAM_ACCOUNT_ID, accountId));
    }
    
    public static BookmarkablePageLink<Void> userDetailsLink(String wicketId, Long userId) {
        return new BookmarkablePageLink<>(wicketId, UserSummary.class, 
            new PageParameters().add(UserSummary.PARAM_USER_ID, userId));
    }
 
    public static BookmarkablePageLink<Void> transactionDetailsLink(String wicketId, Long txId) {
        return new BookmarkablePageLink<>(wicketId, TransactionInfo.class, 
            new PageParameters().add(PARAM_TX_ID, txId));
    }
    
}

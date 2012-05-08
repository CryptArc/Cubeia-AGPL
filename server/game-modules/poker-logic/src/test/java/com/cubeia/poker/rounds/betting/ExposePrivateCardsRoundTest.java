package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.IPokerState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.rounds.dealing.Dealer;
import com.cubeia.poker.rounds.dealing.ExposePrivateCardsRound;
import com.cubeia.poker.variant.telesina.Telesina;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ExposePrivateCardsRoundTest {


    @Test
    public void testExposePrivateCardsRound() {

        Dealer dealer = mock(Dealer.class);

        new ExposePrivateCardsRound(dealer);
        verify(dealer).exposeShowdownCards();
    }

}

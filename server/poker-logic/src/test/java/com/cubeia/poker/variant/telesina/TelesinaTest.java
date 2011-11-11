package com.cubeia.poker.variant.telesina;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cubeia.poker.MockServerAdapter;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.betting.BettingRound;
import com.cubeia.poker.rounds.betting.DefaultPlayerToActCalculator;
import com.cubeia.poker.rounds.betting.PlayerToActCalculator;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;

public class TelesinaTest {

	@Mock private PokerState pokerState;
	private SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();

	@Test
	public void testSendAllNonFoldedPlayersBestHand() {
		MockitoAnnotations.initMocks(this);
		
		PokerPlayer dealer=createAndAddPlayer(1, true);
		createAndAddPlayer(2, false);
		createAndAddPlayer(3, false);

		TelesinaDeckFactory deckFactory = new TelesinaDeckFactory();
		TelesinaForTesting telesina = new TelesinaForTesting(null, pokerState, deckFactory, null);
				
		when(pokerState.getCurrentHandSeatingMap()).thenReturn(playerMap);
		when(pokerState.getTimingProfile()).thenReturn(new DefaultTimingProfile());
		when(pokerState.getServerAdapter()).thenReturn(new MockServerAdapter());

		PlayerToActCalculator playerToActCalculator=new DefaultPlayerToActCalculator();
		new BettingRound(telesina, dealer.getSeatId(), playerToActCalculator);

		assertEquals(2, telesina.getNumberOfSentBestHands());
	}

	private DefaultPokerPlayer createAndAddPlayer(int playerId, boolean folded) {
		DefaultPokerPlayer p = new DefaultPokerPlayer(playerId);
		p.setHasFolded(folded);
		playerMap.put(playerId, p);
		return p;
	}
}

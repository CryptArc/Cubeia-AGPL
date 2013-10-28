package com.cubeia.poker.variant.turkish;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.variant.turkish.hand.TurkishFirstRoundToActCalulator;

public class TurkishFirstRoundToActTest {

	@Mock
	private PokerContext context;
	
	@Mock
    private PokerSettings settings;

	private TreeMap<Integer, PokerPlayer> seatingMap;
	private PokerPlayer player1 = new DefaultPokerPlayer(1001);
    private PokerPlayer player2 = new DefaultPokerPlayer(1002);
    
    TurkishFirstRoundToActCalulator actCalculator;

	private List<Card> communityCards;
    
	@Before
    public void setup() {
        initMocks(this);
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        player1.addChips(new BigDecimal(10000));
        player2.addChips(new BigDecimal(10000));
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        when(context.getSettings()).thenReturn(settings);
	}
	
	
	@Test
	public void openWithPairOfKingsPlayer2() {
		actCalculator = new TurkishFirstRoundToActCalulator(1, Rank.NINE);
		player1.addPocketCard(new Card("KS"), false);
		player1.addPocketCard(new Card("9S"), false);
		player1.addPocketCard(new Card("TS"), false);
		player1.addPocketCard(new Card("JS"), false);
		player1.addPocketCard(new Card("TC"), false);
		player1.setHasActed(false);
		player1.setHasFolded(false);
		
		player2.addPocketCard(new Card("KD"), false);
		player2.addPocketCard(new Card("KC"), false);
		player2.addPocketCard(new Card("TD"), false);
		player2.addPocketCard(new Card("JH"), false);
		player2.addPocketCard(new Card("JC"), false);
		player2.setHasActed(false);
		player2.setHasFolded(false);

		PokerPlayer firstPlayerToAct = actCalculator.getFirstPlayerToAct(seatingMap, communityCards);
		assertEquals(firstPlayerToAct.getId(), 1002);
		
		
	}

	
	@Test
	public void openWithPairOfKingsPlayer1() {
		actCalculator = new TurkishFirstRoundToActCalulator(1, Rank.NINE);
		player1.addPocketCard(new Card("KD"), false);
		player1.addPocketCard(new Card("KC"), false);
		player1.addPocketCard(new Card("TD"), false);
		player1.addPocketCard(new Card("JH"), false);
		player1.addPocketCard(new Card("JC"), false);
		player1.setHasActed(false);
		player1.setHasFolded(false);
		
		player2.addPocketCard(new Card("KS"), false);
		player2.addPocketCard(new Card("9S"), false);
		player2.addPocketCard(new Card("TS"), false);
		player2.addPocketCard(new Card("JS"), false);
		player2.addPocketCard(new Card("TC"), false);
		player2.setHasActed(false);
		player2.setHasFolded(false);

		PokerPlayer firstPlayerToAct = actCalculator.getFirstPlayerToAct(seatingMap, communityCards);
		assertEquals(firstPlayerToAct.getId(), 1001);
	}

	
	@Test
	public void impossibleToOpen() {
		actCalculator = new TurkishFirstRoundToActCalulator(1, Rank.NINE);
		player1.addPocketCard(new Card("KD"), false);
		player1.addPocketCard(new Card("QC"), false);
		player1.addPocketCard(new Card("TD"), false);
		player1.addPocketCard(new Card("JH"), false);
		player1.addPocketCard(new Card("JC"), false);
		player1.setHasActed(false);
		player1.setHasFolded(false);
		
		player2.addPocketCard(new Card("KS"), false);
		player2.addPocketCard(new Card("9S"), false);
		player2.addPocketCard(new Card("TS"), false);
		player2.addPocketCard(new Card("JS"), false);
		player2.addPocketCard(new Card("TC"), false);
		player2.setHasActed(false);
		player2.setHasFolded(false);

		PokerPlayer firstPlayerToAct = actCalculator.getFirstPlayerToAct(seatingMap, communityCards);
		assertEquals(firstPlayerToAct, null);
	}
	
	@Test
	public void nextPlayerId() {
		actCalculator = new TurkishFirstRoundToActCalulator(1, Rank.NINE);
		player1.addPocketCard(new Card("KS"), false);
		player1.addPocketCard(new Card("9S"), false);
		player1.addPocketCard(new Card("TS"), false);
		player1.addPocketCard(new Card("JS"), false);
		player1.addPocketCard(new Card("TC"), false);
		
		player1.setHasActed(false);
		player1.setHasFolded(false);
		
		player2.addPocketCard(new Card("KD"), false);
		player2.addPocketCard(new Card("KC"), false);
		player2.addPocketCard(new Card("TD"), false);
		player2.addPocketCard(new Card("JH"), false);
		player2.addPocketCard(new Card("JC"), false);
		player2.setHasActed(false);
		player2.setHasFolded(false);

		PokerPlayer firstPlayerToAct = actCalculator.getFirstPlayerToAct(seatingMap, communityCards);
		assertEquals(firstPlayerToAct.getId(), 1002);
		
		player2.setHasActed(true);
		PokerPlayer nextPlayerToAct = actCalculator.getNextPlayerToAct(1, seatingMap);
		assertEquals(nextPlayerToAct.getId(), 1001);
		
	}
}

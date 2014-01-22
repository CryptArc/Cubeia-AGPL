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
import com.cubeia.poker.variant.turkish.hand.TurkishSecondRoundToActCalulator;

public class TurkishSecondRoundToActTest {

	@Mock
	private PokerContext context;
	
	@Mock
    private PokerSettings settings;

	private TreeMap<Integer, PokerPlayer> seatingMap;
	private PokerPlayer player1 = new DefaultPokerPlayer(1001);
    private PokerPlayer player2 = new DefaultPokerPlayer(1002);
    private PokerPlayer player3 = new DefaultPokerPlayer(1003);
    
    TurkishSecondRoundToActCalulator actCalculator;

	private List<Card> communityCards;
    
	@Before
    public void setup() {
        initMocks(this);
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        player1.addChips(new BigDecimal(10000));
        player2.addChips(new BigDecimal(10000));
        player3.addChips(new BigDecimal(10000));
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        when(context.getSettings()).thenReturn(settings);
		actCalculator = new TurkishSecondRoundToActCalulator(Rank.NINE);
	}
	
	
	@Test
	public void player2HasOpened() {
		player1.setHasActed(false);
		player1.setHasFolded(false);
		
		player2.setHasActed(false);
		player2.setHasFolded(false);
		player2.setHasOpened(true);

		player3.setHasActed(false);
		player3.setHasFolded(false);

		PokerPlayer firstPlayerToAct = actCalculator.getFirstPlayerToAct(seatingMap, communityCards);
		assertEquals(firstPlayerToAct.getId(), 1002);

		PokerPlayer nextPlayerToAct = actCalculator.getNextPlayerToAct(1, seatingMap);
		assertEquals(nextPlayerToAct.getId(), 1003);

		nextPlayerToAct = actCalculator.getNextPlayerToAct(2, seatingMap);
		assertEquals(nextPlayerToAct.getId(), 1001);

	}


	@Test
	public void player3HasOpened() {
		player1.setHasActed(false);
		player1.setHasFolded(false);
		
		player2.setHasActed(false);
		player2.setHasFolded(false);
		
		player3.setHasActed(false);
		player3.setHasFolded(false);
		player3.setHasOpened(true);

		PokerPlayer firstPlayerToAct = actCalculator.getFirstPlayerToAct(seatingMap, communityCards);
		assertEquals(firstPlayerToAct.getId(), 1003);

		PokerPlayer nextPlayerToAct = actCalculator.getNextPlayerToAct(2, seatingMap);
		assertEquals(nextPlayerToAct.getId(), 1001);

		nextPlayerToAct = actCalculator.getNextPlayerToAct(0, seatingMap);
		assertEquals(nextPlayerToAct.getId(), 1002);

	}

	
	@Test
	public void player1HasOpened() {
		player1.setHasActed(false);
		player1.setHasFolded(false);
		player1.setHasOpened(true);
		
		player2.setHasActed(false);
		player2.setHasFolded(false);
		
		player3.setHasActed(false);
		player3.setHasFolded(false);

		PokerPlayer firstPlayerToAct = actCalculator.getFirstPlayerToAct(seatingMap, communityCards);
		assertEquals(firstPlayerToAct.getId(), 1001);

		PokerPlayer nextPlayerToAct = actCalculator.getNextPlayerToAct(0, seatingMap);
		assertEquals(nextPlayerToAct.getId(), 1002);

		nextPlayerToAct = actCalculator.getNextPlayerToAct(1, seatingMap);
		assertEquals(nextPlayerToAct.getId(), 1003);

	}
	
	
}

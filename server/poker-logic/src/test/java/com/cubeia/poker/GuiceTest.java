package com.cubeia.poker;

import java.util.LinkedList;
import java.util.List;

import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.Timings;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import junit.framework.TestCase;

public abstract class GuiceTest extends TestCase {

	protected Injector injector;

	protected MockServerAdapter mockServerAdapter;
	
	protected PokerState game;

	protected TestUtils testUtils = new TestUtils();
	
	@Override
	protected void setUp() throws Exception {
		List<Module> list = new LinkedList<Module>();
		list.add(new PokerGuiceModule());
		injector = Guice.createInjector(list);
		setupDefaultGame();
	}
	
	protected void setupDefaultGame() {
		mockServerAdapter = new MockServerAdapter();
		game = injector.getInstance(PokerState.class);
		game.setAnteLevel(100);
		game.setServerAdapter(mockServerAdapter);
		game.setTimingProfile(TimingFactory.getRegistry().getTimingProfile(Timings.MINIMUM_DELAY));	
	}
}

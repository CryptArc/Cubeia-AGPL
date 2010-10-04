package mock;

import com.cubeia.poker.MockGame;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.Timings;

public class MockTableFactory {

	public static MockTable create() {
		MockTable table = new MockTable();
		PokerState pokerState = new PokerState();
		pokerState.setGameType(new MockGame());
		pokerState.setTimingProfile(TimingFactory.getRegistry().getTimingProfile(Timings.MINIMUM_DELAY));
		table.getGameState().setState(pokerState);
		return table;
	}
	
}

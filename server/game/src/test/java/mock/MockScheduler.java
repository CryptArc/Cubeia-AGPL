package mock;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.cubeia.firebase.api.action.Action;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.game.table.TableScheduler;
import com.cubeia.firebase.api.util.UnmodifiableSet;

public class MockScheduler implements TableScheduler {

	@SuppressWarnings("unused")
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	public void cancelScheduledAction(UUID arg0) {
		// TODO Auto-generated method stub

	}
	
	public void cancelAllScheduledActions() {
		// TODO Auto-generated method stub
		
	}

	public UnmodifiableSet<UUID> getAllScheduledGameActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Action getScheduledGameAction(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getScheduledGameActionDelay(UUID arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasScheduledGameAction(UUID arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public UUID scheduleAction(GameAction arg0, long arg1) {
		return null;
	}

}

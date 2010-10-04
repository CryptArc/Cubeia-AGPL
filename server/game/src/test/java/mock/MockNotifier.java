package mock;

import java.util.ArrayList;
import java.util.Collection;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.SystemMessageAction;
import com.cubeia.firebase.api.game.GameNotifier;

public class MockNotifier implements GameNotifier {

	private Collection<GameAction> cachedActions = new ArrayList<GameAction>();
	
	public void clear() {
		cachedActions.clear();
	}
	
	public Collection<GameAction> getActions() {
		return cachedActions;
	}
	
	public void broadcast(SystemMessageAction arg0) {
		
	}

	public void notifyAllPlayers(GameAction action) {
		cachedActions.add(action);
	}

	public void notifyAllPlayers(Collection<? extends GameAction> actions) {
		cachedActions.addAll(actions);
	}

	public void notifyAllPlayers(GameAction action, boolean arg1) {
		cachedActions.add(action);
	}

	public void notifyAllPlayers(Collection<? extends GameAction> actions, boolean arg1) {
		cachedActions.addAll(actions);
	}

	public void notifyAllPlayersExceptOne(GameAction action, int arg1) {
		cachedActions.add(action);
	}

	public void notifyAllPlayersExceptOne(Collection<? extends GameAction> actions, int arg1) {
		cachedActions.addAll(actions);
	}

	public void notifyAllPlayersExceptOne(GameAction action, int arg1,boolean arg2) {
		cachedActions.add(action);
	}

	public void notifyAllPlayersExceptOne(Collection<? extends GameAction> actions, int arg1, boolean arg2) {
		cachedActions.addAll(actions);
	}

	public void notifyPlayer(int arg0, GameAction action) {
		cachedActions.add(action);
	}

	public void notifyPlayer(int arg0, Collection<? extends GameAction> actions) {
		cachedActions.addAll(actions);
	}

	public void sendToClient(int arg0, GameAction action) {
		cachedActions.add(action);
	}

	public void sendToClient(int arg0, Collection<? extends GameAction> actions) {
		cachedActions.addAll(actions);
	}

}

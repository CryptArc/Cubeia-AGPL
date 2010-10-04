package mock;

import com.cubeia.firebase.api.game.table.TableGameState;

public class MockTableGameState implements TableGameState {
	
	private Object state;

	public Object getState() {
		return state;
	}

	public void setState(Object state) {
		this.state = state;
	}

}

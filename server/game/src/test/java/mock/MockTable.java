package mock;

import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.TournamentNotifier;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.ExtendedDetailsProvider;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableGameState;
import com.cubeia.firebase.api.game.table.TableInterceptor;
import com.cubeia.firebase.api.game.table.TableListener;
import com.cubeia.firebase.api.game.table.TableMetaData;
import com.cubeia.firebase.api.game.table.TablePlayerSet;
import com.cubeia.firebase.api.game.table.TableScheduler;
import com.cubeia.firebase.api.game.table.TableWatcherSet;

public class MockTable implements Table {

	private TableGameState state = new MockTableGameState();
	
	private GameNotifier notifier = new MockNotifier();
	
	private TableScheduler scheduler = new MockScheduler();
	
	public GameNotifier getNotifier() {
		return notifier;
	}
	
	public TableGameState getGameState() {
		return state;
	}
	
	public TableScheduler getScheduler() {
		return scheduler;
	}

	
	
	
	public ExtendedDetailsProvider getExtendedDetailsProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public TableInterceptor getInterceptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public TableListener getListener() {
		// TODO Auto-generated method stub
		return null;
	}

	public TableMetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	

	public TablePlayerSet getPlayerSet() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public TournamentNotifier getTournamentNotifier() {
		// TODO Auto-generated method stub
		return null;
	}

	public TableWatcherSet getWatcherSet() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

    public LobbyTableAttributeAccessor getAttributeAccessor() {
        // TODO Auto-generated method stub
        return null;
    }

}

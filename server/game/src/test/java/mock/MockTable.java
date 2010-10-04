/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

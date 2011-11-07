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

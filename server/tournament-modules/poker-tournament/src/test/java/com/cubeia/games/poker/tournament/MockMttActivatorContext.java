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

package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.mtt.activator.ActivatorContext;
import com.cubeia.firebase.api.routing.ActivatorRouter;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.util.ConfigSource;
import com.cubeia.firebase.api.util.ConfigSourceListener;

public class MockMttActivatorContext implements ActivatorContext {

    public ConfigSource getConfigSource() {
        return null;
    }

    public ActivatorRouter getActivatorRouter() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getMttId() {
        return 0;
    }

    public ServiceRegistry getServices() {
        return null;
    }

    public void setConfigSourceListener(ConfigSourceListener arg0) {

    }

}

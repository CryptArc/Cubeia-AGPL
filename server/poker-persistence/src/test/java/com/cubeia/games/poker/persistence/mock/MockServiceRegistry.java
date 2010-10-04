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

package com.cubeia.games.poker.persistence.mock;

import com.cubeia.firebase.api.service.Contract;
import com.cubeia.firebase.api.service.ServiceInfo;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.service.persistence.PublicPersistenceService;

public class MockServiceRegistry implements ServiceRegistry {

    @SuppressWarnings("unchecked")
    public <T extends Contract> T getServiceInstance(Class<T> serviceClass) {
        if (serviceClass == PublicPersistenceService.class) {
            return (T) new MockPersistenceService();
        } else {
            return null;
        }
    }

    public ServiceInfo getServiceInfo(String arg0) {
        return null;
    }

    public <T extends Contract> ServiceInfo getServiceInfo(Class<T> arg0, String arg1) {
        return null;
    }

    public Contract getServiceInstance(String arg0) {
        return null;
    }

    public <T extends Contract> T getServiceInstance(Class<T> arg0, String arg1) {
        return null;
    }

}

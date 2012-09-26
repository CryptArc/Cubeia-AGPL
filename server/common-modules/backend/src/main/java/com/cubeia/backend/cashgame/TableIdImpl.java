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

package com.cubeia.backend.cashgame;

import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("serial")
public class TableIdImpl implements TableId {

    private static final AtomicLong idGenerator = new AtomicLong(0);

    private final long id;

    public TableIdImpl() {
        this.id = idGenerator.incrementAndGet();
    }

    public TableIdImpl(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TableIdImpl)) {
            return false;
        }

        return id == ((TableIdImpl) obj).id;
    }

    @Override
    public String toString() {
        return "TableId(" + id + ")";
    }
}

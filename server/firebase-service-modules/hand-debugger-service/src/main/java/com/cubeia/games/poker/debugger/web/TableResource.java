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

package com.cubeia.games.poker.debugger.web;

import com.cubeia.games.poker.debugger.cache.Event;
import com.cubeia.games.poker.debugger.cache.TableEventCache;
import com.cubeia.games.poker.debugger.json.HandEvent;
import com.cubeia.games.poker.debugger.json.HandHistory;
import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/table")
@Produces({MediaType.APPLICATION_JSON})
public class TableResource {

    @Inject
    TableEventCache cache;

    @GET
    @Path("id/{tableId}")
    public HandHistory getHandHistory(@PathParam("tableId") int tableId) {
        List<Event> events = cache.getEvents(tableId);
        return createHandHistory(events);
    }

    @GET
    @Path("previous/id/{tableId}")
    public HandHistory getPreviousHandHistory(@PathParam("tableId") int tableId) {
        List<Event> events = cache.getPreviousEvents(tableId);
        return createHandHistory(events);
    }

    private HandHistory createHandHistory(List<Event> events) {
        HandHistory handHistory = new HandHistory();
        if (events != null) {
            for (Event entry : events) {
                HandEvent event = new HandEvent();
                event.description = entry.toString();
                event.type = entry.getType();
                handHistory.events.add(event);
            }
            return handHistory;

        } else {
            return new HandHistory();
        }
    }
}

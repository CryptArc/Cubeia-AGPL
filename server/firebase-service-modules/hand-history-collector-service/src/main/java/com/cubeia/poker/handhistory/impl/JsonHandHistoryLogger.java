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

package com.cubeia.poker.handhistory.impl;

import com.cubeia.firebase.guice.inject.Log4j;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HandHistoryPersister;
import com.cubeia.poker.handhistory.api.HandIdentification;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.google.gson.*;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;

@Singleton
public class JsonHandHistoryLogger implements HandHistoryPersister {

    @Log4j
    private Logger log;

    @Override
    public HistoricHand retrieve(HandIdentification id) {
        log.warn("Operation 'retrieve' not supported by JSON logger");
        return null;
    }

    @Override
    public void persist(HistoricHand hand) {
        Gson gson = createGson();
        String json = gson.toJson(hand);
        log.info(json);
    }


    // --- PRIVATE METHODS --- //

    private Gson createGson() {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapter(HandHistoryEvent.class, new EventSerializer());
        b.setPrettyPrinting();
        return b.create();
    }


    // --- PRIVATE CLASSES --- //

    private static class EventSerializer implements JsonSerializer<HandHistoryEvent> {

        @Override
        public JsonElement serialize(HandHistoryEvent src, Type typeOfSrc, JsonSerializationContext context) {
            Class<? extends HandHistoryEvent> cl = src.getClass();
            return context.serialize(src, cl);
        }
    }
}

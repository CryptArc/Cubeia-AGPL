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

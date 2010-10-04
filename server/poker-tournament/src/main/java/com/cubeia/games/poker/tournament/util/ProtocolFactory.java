package com.cubeia.games.poker.tournament.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.cubeia.firebase.api.action.mtt.MttDataAction;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;

public class ProtocolFactory {
    
    /** We only need writing so no injected factory is needed */
    private static StyxSerializer serializer = new StyxSerializer(null);
    
    public static MttDataAction createMttAction(ProtocolObject packet, int playerId, int mttId) {
        try {
            MttDataAction action = new MttDataAction(mttId, playerId);
            ByteBuffer buffer;
            buffer = serializer.pack(packet);
            action.setData(buffer);
            return action;
        } catch (IOException e) {
            throw new RuntimeException("Could not serialize game packet ["+packet+"]", e);
        }
    }
    
}

package com.cubeia.game.poker.bot;

import com.cubeia.firebase.bot.Bot;
import com.cubeia.firebase.bot.ai.MttAI;
import com.cubeia.firebase.io.protocol.GameTransportPacket;
import com.cubeia.firebase.io.protocol.MttTransportPacket;

public class TournamentPokerBot extends MttAI {

    private GameHandler handler;
    
    public TournamentPokerBot(Bot bot) {
        super(bot);
        handler = new GameHandler(this);
        bot.logDebug("Tournament Poker Bot created");
    }
    
    @Override
    public void handleGamePacket(GameTransportPacket packet) {
        handler.handleGamePacket(packet);
    }
    
    @Override
    public void handleTournamentPacket(MttTransportPacket packet) {
        handler.handleTournamentPacket(packet);
    }
    
}

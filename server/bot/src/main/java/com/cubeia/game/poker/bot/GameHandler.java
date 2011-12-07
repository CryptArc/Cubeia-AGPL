package com.cubeia.game.poker.bot;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import se.jadestone.dicearena.game.poker.network.protocol.BestHand;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoRequest;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInInfoResponse;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInRequest;
import se.jadestone.dicearena.game.poker.network.protocol.BuyInResponse;
import se.jadestone.dicearena.game.poker.network.protocol.CardToDeal;
import se.jadestone.dicearena.game.poker.network.protocol.DealPrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.DealPublicCards;
import se.jadestone.dicearena.game.poker.network.protocol.DealerButton;
import se.jadestone.dicearena.game.poker.network.protocol.DeckInfo;
import se.jadestone.dicearena.game.poker.network.protocol.Enums.PlayerTableStatus;
import se.jadestone.dicearena.game.poker.network.protocol.ErrorPacket;
import se.jadestone.dicearena.game.poker.network.protocol.ExposePrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.ExternalSessionInfoPacket;
import se.jadestone.dicearena.game.poker.network.protocol.GameCard;
import se.jadestone.dicearena.game.poker.network.protocol.HandCanceled;
import se.jadestone.dicearena.game.poker.network.protocol.HandEnd;
import se.jadestone.dicearena.game.poker.network.protocol.PacketVisitor;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerBalance;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerPokerStatus;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitinRequest;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerSitoutRequest;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerState;
import se.jadestone.dicearena.game.poker.network.protocol.Pot;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfer;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfers;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;
import se.jadestone.dicearena.game.poker.network.protocol.RakeInfo;
import se.jadestone.dicearena.game.poker.network.protocol.RequestAction;
import se.jadestone.dicearena.game.poker.network.protocol.StartHandHistory;
import se.jadestone.dicearena.game.poker.network.protocol.StartNewHand;
import se.jadestone.dicearena.game.poker.network.protocol.StopHandHistory;
import se.jadestone.dicearena.game.poker.network.protocol.TakeBackUncalledBet;
import se.jadestone.dicearena.game.poker.network.protocol.TournamentOut;

import com.cubeia.firebase.bot.BotState;
import com.cubeia.firebase.bot.action.Action;
import com.cubeia.firebase.bot.ai.AbstractAI;
import com.cubeia.firebase.bot.ai.MttAI;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.firebase.io.protocol.GameTransportPacket;
import com.cubeia.firebase.io.protocol.MttTransportPacket;
import com.cubeia.game.poker.util.Arithmetic;

public class GameHandler implements PacketVisitor {
    
    private static transient Logger log = Logger.getLogger(GameHandler.class);
    
    private static final StyxSerializer styxDecoder = new StyxSerializer(new ProtocolObjectFactory());
   
    private static Random rng = new Random();
    
    private final AbstractAI bot;
    
    private AtomicBoolean historicActionsAreBeingSent = new AtomicBoolean(false);
    
    public GameHandler(AbstractAI bot) {
        this.bot = bot;
    }
    
    public void handleGamePacket(GameTransportPacket packet) {       
        // Create the user packet
        ProtocolObject gamePacket;
        try {
            gamePacket = styxDecoder.unpack(ByteBuffer.wrap(packet.gamedata));
            gamePacket.accept(this);
        } catch (IOException e) {
            log.error("Could not unpack gamedata", e);
        }
    }
    
    public void handleTournamentPacket(MttTransportPacket packet) {
        ProtocolObject gamePacket;
        try {
            gamePacket = styxDecoder.unpack(ByteBuffer.wrap(packet.mttdata));
            gamePacket.accept(this);
        } catch (IOException e) {
            log.error("Could not unpack mttdata", e);
        }
    }
    
    public ProtocolObject unpack(GameTransportPacket packet) {       
        // Create the user packet
        ProtocolObject gamePacket = null;
        try {
            gamePacket = styxDecoder.unpack(ByteBuffer.wrap(packet.gamedata));
        } catch (IOException e) {
            log.error("Could not unpack gamedata", e);
        }
        return gamePacket;
    }
    
    
    public void visit(StartNewHand packet) {
        bot.getBot().logDebug("New Hand starting. Dealer seat: "+packet.dealerSeatId);
    }
    
    @SuppressWarnings("static-access")
    public void visit(final RequestAction request) {
        if (request.player == bot.getBot().getPid() && !historicActionsAreBeingSent.get()) {
            Action action = new Action(bot.getBot()) {
                public void run() {
                	try {
	                    PlayerAction playerAction = Strategy.getAction(request.allowedActions);
	                    PerformAction response = new PerformAction();
	                    response.seq = request.seq;
	                    response.player = bot.getBot().getPid();
	                    response.action = playerAction;
	                    response.betAmount = getRandomBetAmount(playerAction);
	                    
	                    // Sanity check
	                    if (response.betAmount > playerAction.maxAmount) {
	                    	bot.getBot().logWarn("I am betting too much. Max["+playerAction.maxAmount+"] myBet["+response.betAmount+"]");
	                    }
	                    
	                    // bot.getBot().logInfo("Request("+request+") -> Response("+response+")");
	                    bot.getBot().sendGameData(bot.getTable().getId(), bot.getBot().getPid(), response);
                	} catch (Throwable th) {
                		th.printStackTrace();
                	}
                }
				
            };
            
            int wait = 0;
            if (Strategy.useDelay(request.allowedActions)) {
                int expected = request.timeToAct/6;
                int deviation = request.timeToAct/3;
                wait = Arithmetic.gaussianAverage(expected,deviation);
                wait = wait < 0 ? 0 : wait;
            }
            
            bot.executor.schedule(action, wait, TimeUnit.MILLISECONDS);
        }
    }
    
    private int getRandomBetAmount(PlayerAction playerAction) {
    	if (playerAction.maxAmount <= 0) {
    		return 0;
    	}
    	
    	//return playerAction.minAmount;
    	
    	// 10% chance of min bet
    	if (rng.nextInt(100) < 10) {
    		return playerAction.minAmount;
    	}
    	
    	// 5% chance of all in
    	if (rng.nextInt(100) < 5) {
    		return playerAction.maxAmount;
    	}
    	
    	
    	// Use min amount as minimum betting step
    	int increment = playerAction.minAmount == 0 ? playerAction.maxAmount/10 : playerAction.minAmount;
    	int maxLevel = playerAction.maxAmount % increment;
    	
    	if (maxLevel < 2) {
    		return playerAction.minAmount;
    	} else {
    		// Randomize how many min amount bets we will bet
    		int bets = 2+rng.nextInt(maxLevel);
    		int betThis = playerAction.minAmount * bets;
    		int cappedBet = Math.min(betThis, playerAction.maxAmount);
    		if (cappedBet < playerAction.minAmount) {
    			cappedBet = playerAction.minAmount; // FIXME: This is a bug that occurs
    		}
    		return cappedBet;
    	}
	}
    
    public void visit(StartHandHistory packet) {
        historicActionsAreBeingSent.set(true);
    }

    public void visit(StopHandHistory packet) {
        historicActionsAreBeingSent.set(false);
    }
    
    
    public void visit(TournamentOut packet) {
        bot.getBot().logDebug("I was out from tournament. Position: "+packet.position);
        if (bot instanceof MttAI) {
            bot.getBot().setState(BotState.MTT_OUT);
        }
    }
    
    public void visit(PlayerPokerStatus packet) {
    	if (packet.player == bot.getBot().getPid() && packet.status.equals(PlayerTableStatus.SITOUT)) {
    		// I am in sitout state, schedule a sitin again
    		int wait = 20;
    		bot.getBot().logDebug("I am sitting out. Scheduling sitin in "+wait+" seconds.");
    		Action action = new Action(bot.getBot()) {
                public void run() {
                	PlayerSitinRequest sitin = new PlayerSitinRequest();
                    bot.getBot().sendGameData(bot.getTable().getId(), bot.getBot().getPid(), sitin);
                }
            };
    		AbstractAI.executor.schedule(action, wait, TimeUnit.SECONDS);
    	}
	}
    
    /**
     * Handle buy in
     */
    public void visit(BuyInInfoResponse packet) {
    	bot.getBot().logDebug("I will make a buy in with the maximum amout of "+packet.maxAmount);
		BuyInRequest request = new BuyInRequest();
		request.amount = packet.maxAmount;
		request.sitInIfSuccessful = true;
		bot.getBot().sendGameData(bot.getTable().getId(), bot.getBot().getPid(), request);
    }
    
    public void visit(TakeBackUncalledBet packet) {}
    public void visit(GameCard packet) {}
    public void visit(BestHand packet) {}
    public void visit(DealPublicCards packet) {}
    public void visit(DealPrivateCards packet) {}
    public void visit(ExposePrivateCards packet) {}
    public void visit(HandEnd packet) {}
    public void visit(PlayerState packet) {}
    public void visit(PerformAction packet) {}
    public void visit(PlayerAction packet) {}
    public void visit(DealerButton packet) {}
    public void visit(PlayerBalance packet) {}
    public void visit(Pot packet) {}
	public void visit(PlayerSitinRequest packet) {}
	public void visit(PlayerSitoutRequest arg0) {}
	public void visit(CardToDeal packet) {}
	public void visit(ExternalSessionInfoPacket packet) {}

	public void visit(HandCanceled packet) {}

	public void visit(BuyInInfoRequest packet) {}

	public void visit(BuyInRequest packet) {}

	public void visit(BuyInResponse packet) {}

	public void visit(PotTransfer packet) {}

	public void visit(PotTransfers packet) {}

	public void visit(RakeInfo packet) {}

	public void visit(DeckInfo packet) {}

	public void visit(ErrorPacket packet) {}
    
}

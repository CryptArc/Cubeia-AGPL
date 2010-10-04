package com.cubeia.poker.states;

import org.apache.log4j.Logger;

import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;

public class WaitingToStartState extends AbstractPokerGameState {

	private static final long serialVersionUID = -4837159720440582936L;
	
	private static transient Logger log = Logger.getLogger(WaitingToStartState.class);

	public String toString() {
	    return "WaitingToStartState";
	}
	
	@Override
	public void timeout(PokerState context) {
		if (!context.isTournamentTable()) {
			context.setHandFinished(false);
			
			// FIXME: Don't automatically sit in all players
			context.sitInAllPlayers();
			
			if (context.countSittingInPlayers() > 1) {
				
				// FIXME: Resetting all low to zero balances since we have no wallet yet
				for (PokerPlayer pp : context.getSeatedPlayers()) {
					if (pp.getBalance() < context.getAnteLevel()) {
						log.debug("Resetting player balance. Player["+pp.getId()+"] -> 10000");
						((DefaultPokerPlayer)pp).setBalance(10000);
						context.notifyPlayerBalance(pp.getId());
					}
				}
				
				context.startHand();
			} else {
				context.setHandFinished(true);
				context.setState(PokerState.NOT_STARTED);
				log.info("WILL NOT START NEW HAND, TOO FEW PLAYERS SEATED: " + context.countSittingInPlayers() + " sitting in of " + context.getSeatedPlayers().size());
				context.cleanupPlayers(); // Will remove disconnected and leaving players
			}
		} else {
			log.debug("Ignoring timeout in waiting to start state, since tournament hands are started by the tournament manager.");
		}
	}
	
	public void act(PokerAction action, PokerState pokerGame) {
		log.info("Discarding out of order action: "+action);
	}

}

package poker.gs.server.blinds;

import java.util.ArrayList;
import java.util.List;

import poker.gs.server.blinds.utils.MockPlayer;

public class Fixtures {

	public static List<BlindsPlayer> players(int ... seatIds) {
		return players(true, seatIds);
	}
	
	public static List<BlindsPlayer> players(boolean hasPosted, int ... seatIds) {
		List<BlindsPlayer> players = new ArrayList<BlindsPlayer>();
		for (int seatId : seatIds) {
			players.add(player(seatId, hasPosted));
		}
		return players;
	}		
	
	public static MockPlayer player(int seatId, boolean hasPostedEntry, boolean sittingIn) {
		MockPlayer player = new MockPlayer(seatId);
		player.setHasPostedEntryBet(hasPostedEntry);
		player.setSittingIn(sittingIn);		
		return player;		
	}
	
	public static MockPlayer player(int seatId, boolean hasPostedEntry) {
		return player(seatId, hasPostedEntry, true);
	}

	public static BlindsInfo blindsInfo(int dealer, int small, int big) {
		BlindsInfo blinds = new BlindsInfo();
		blinds.setDealerSeatId(dealer);
		blinds.setSmallBlindSeatId(small);
		blinds.setSmallBlindPlayerId(small);
		blinds.setBigBlindSeatId(big);
		blinds.setBigBlindPlayerId(big);
		return blinds;
	}
	
}

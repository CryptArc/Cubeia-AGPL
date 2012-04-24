package poker.gs.server.blinds.utils;

import poker.gs.server.blinds.BlindsPlayer;
import poker.gs.server.blinds.MissedBlindsStatus;

public class MockPlayer implements BlindsPlayer {

	private final int playerId;
	private boolean hasPostedEntry;
	private MissedBlindsStatus missedBlindsStatus = MissedBlindsStatus.NOT_ENTERED_YET;
	private boolean isSittingIn;
	
	public MockPlayer(int playerId) {
		super();
		this.playerId = playerId;
	}

	public long getPlayerId() {
		return playerId;
	}

	public int getSeatId() {
		return playerId;
	}

	public boolean isSittingIn() {
		return isSittingIn;
	}
	
	public void setSittingIn(boolean isSittingIn) {
		this.isSittingIn = isSittingIn;
	}

	public boolean hasPostedEntryBet() {
		return hasPostedEntry;
	}

	public void setHasPostedEntryBet(boolean hasPostedEntry) {
		this.hasPostedEntry = hasPostedEntry;
	}
	
	public void setMissedBlindsStatus(MissedBlindsStatus status) {
		missedBlindsStatus = status;
	}

	public MissedBlindsStatus getMissedBlindsStatus() {
		return missedBlindsStatus;
	}
	
	@Override
	public String toString() {
		return "seatId=" + playerId;
	}
	
}

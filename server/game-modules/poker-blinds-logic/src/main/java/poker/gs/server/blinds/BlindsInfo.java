package poker.gs.server.blinds;

/**
 * Class for describing the blinds info of a hand.
 * 
 * @author viktor
 *
 */
public class BlindsInfo {

	private int dealerSeatId;
	
	private int smallBlindSeatId;
	
	private long smallBlindPlayerId;
	
	private int bigBlindSeatId;

	private long bigBlindPlayerId;

	public BlindsInfo() {
		// Empty constructor.
	}
	
	public BlindsInfo(int dealerSeatId, int smallBlindSeatId, int bigBlindSeatId) {
		this.dealerSeatId = dealerSeatId;
		this.smallBlindSeatId = smallBlindSeatId;
		this.bigBlindSeatId = bigBlindSeatId;
		
	}

	public int getDealerSeatId() {
		return dealerSeatId;
	}
	
	/**
	 * Gets the small blind seat id of this hand. Note that every hand has a small blind seat id, 
	 * even if the small blind was dead.
	 * @return
	 */
	public int getSmallBlindSeatId() {
		return smallBlindSeatId;
	}
	
	public int getBigBlindSeatId() {
		return bigBlindSeatId;
	}
	
	public boolean isHeadsUpLogic() {
		return smallBlindSeatId == dealerSeatId;
	}
	
	public boolean isDefined() {
		// If dealer, sb and bb are 0, the blinds are undefined.
		return !(smallBlindSeatId == 0 && bigBlindSeatId == 0 && dealerSeatId == 0);
	}

	public void setDealerSeatId(int seatId) {
		dealerSeatId = seatId;
	}

	public void setSmallBlindSeatId(int seatId) {
		smallBlindSeatId = seatId;
	}

	public void setBigBlindSeatId(int seatId) {
		bigBlindSeatId = seatId;
	}
	
	public long getSmallBlindPlayerId() {
		return smallBlindPlayerId;
	}	
	
	@Override
	public String toString() {
		return String.format("dealer=%s small=%s big=%s " + "smallpid=%s bigpid=%s", dealerSeatId, smallBlindSeatId, bigBlindSeatId, smallBlindPlayerId, bigBlindPlayerId);
	}

	public void setSmallBlindPlayerId(long playerId) {
		this.smallBlindPlayerId = playerId;
	}

	public long getBigBlindPlayerId() {
		return bigBlindPlayerId;
	}
	
	public void setBigBlindPlayerId(long bigBlindPlayerId) {
		this.bigBlindPlayerId = bigBlindPlayerId;
	}

	public boolean handCanceled() {
		return dealerSeatId != 0 && (smallBlindSeatId == 0 || bigBlindSeatId == 0);
	}
}

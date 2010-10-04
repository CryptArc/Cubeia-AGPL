package com.cubeia.games.poker.activator;

import com.cubeia.firebase.api.game.GameDefinition;
import com.cubeia.firebase.api.game.activator.DefaultCreationParticipant;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.lobby.LobbyPath;
import com.cubeia.games.poker.FirebaseState;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.gametypes.TexasHoldem;
import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.timing.Timings;
import com.google.inject.Injector;


/**
 * Table Creator.
 * 
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class PokerParticipant extends DefaultCreationParticipant {
	
	private static final int GAME_ID = 7;

	/** Number of seats at the table */
	private int seats = 10;
	
	/** Dummy domain for tesing lobby tree */
	private String domain = "A";

    private TimingProfile timingProfile = TimingFactory.getRegistry().getDefaultTimingProfile();

    private Timings timing = Timings.DEFAULT;

	private final int anteLevel;
	
	// FIXME: This is not good IoC practice *cough*
	private Injector injector;
	
	public PokerParticipant(int seats, String domain, int anteLevel, Timings timing) {
		super();
		this.seats = seats;
		this.domain = domain;
        this.timing = timing;
		this.anteLevel = anteLevel;
        this.timingProfile  = TimingFactory.getRegistry().getTimingProfile(timing);
	}
	

	public LobbyPath getLobbyPath() {
		LobbyPath path = new LobbyPath(GAME_ID, domain);
		return path;
	}
	
	public void setInjector(Injector injector) {
		this.injector = injector;
	}
	
	/* (non-Javadoc)
	 * @see com.cubeia.firebase.api.game.activator.DefaultCreationParticipant#getLobbyPathForTable(com.cubeia.firebase.api.game.table.Table)
	 */
	@Override 
	public LobbyPath getLobbyPathForTable(Table table) {
		LobbyPath path = getLobbyPath();
		path.setObjectId(table.getId());
		return path;
	}
	
	@Override
	public void tableCreated(Table table, LobbyTableAttributeAccessor acc) {
		super.tableCreated(table, acc);
		PokerState pokerState = injector.getInstance(PokerState.class);
		
		//pokerState.setGameType(new TexasHoldem(pokerState));
		
		pokerState.setTimingProfile(timingProfile);
		pokerState.setAnteLevel(anteLevel);
		pokerState.setAdapterState(new FirebaseState());
		pokerState.setId(table.getId());
		table.getGameState().setState(pokerState);
		
		acc.setStringAttribute("SPEED", timing.name());
		acc.setIntAttribute("ANTE", anteLevel);
	}

	@Override
	public String getTableName(GameDefinition def, Table t) {
		return "Poker<"+t.getId()+">";
	}
	
	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	
}

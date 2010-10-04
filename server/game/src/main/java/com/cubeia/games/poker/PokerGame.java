package com.cubeia.games.poker;

import java.util.Collections;
import java.util.List;

import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.game.table.ExtendedDetailsProvider;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.util.ParameterUtil;
import com.cubeia.firebase.guice.game.Configuration;
import com.cubeia.firebase.guice.game.GuiceGame;
import com.cubeia.firebase.io.protocol.Param;
import com.cubeia.game.server.service.user.UserServiceContract;
import com.cubeia.game.server.service.user.domain.User;
import com.cubeia.games.poker.jmx.PokerStats;
import com.google.inject.Module;

public class PokerGame extends GuiceGame implements ExtendedDetailsProvider {
    	
    public final static int POKER_GAME_ID = 7;
    
    public static final int LICENSEE_ID = "Cubeia".hashCode();
    
    private ServiceRegistry services;

	/*------------------------------------------------
	 
		LIFECYCLE METHODS

		The constructor and methods will be called 
		by Firebase which manages the life cycle of
		the Game.
		
	 ------------------------------------------------*/
	public PokerGame() {
	    // Trigger poker stats creation and binding to JMX
	    PokerStats.getInstance();
	}
	
	public void init(GameContext con) throws SystemException {
    	super.init(con);
        services = con.getServices();
    }
	
	 public void destroy() {}
	 
	 
	/*------------------------------------------------
	 
		GUICE IoC METHODS
		
		Adapter methods for Guice support.

	 ------------------------------------------------*/
	
	 @Override
	 public Configuration getConfigurationHelp() {
		 return new IntegrationGuiceConfig();
	 }

	 @Override
	 protected void preInjectorCreation(List<Module> modules) {
		 modules.add(new IntegrationGuiceModule());
	 }
     
	 /*------------------------------------------------
 	  
 	 	END OF GUICE IoC METHODS
 	 
 	 ------------------------------------------------*/
	
    
	
	@Override
	public List<Param> getExtendedDetails(Table table, int playerId, boolean fromLobby) {
		if (true) {
			// FIXME: Getting classnotfound on UserService
			return Collections.emptyList();
		}
		
		UserServiceContract serv = services.getServiceInstance(UserServiceContract.class);
		User user = serv.getUser(playerId);
		String externalId = user.getDetails().getExternalId();
		if(externalId != null) {
			Param param = ParameterUtil.createParam("externalId", externalId);
			return Collections.singletonList(param);
		} else {
			return Collections.emptyList();
		}
	}
    
}

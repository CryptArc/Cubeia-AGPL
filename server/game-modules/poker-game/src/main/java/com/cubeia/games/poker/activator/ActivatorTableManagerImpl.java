package com.cubeia.games.poker.activator;

import java.util.List;

import org.apache.log4j.Logger;

import com.cubeia.firebase.guice.inject.Log4j;
import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ActivatorTableManagerImpl implements ActivatorTableManager {

	@Inject
	private TableConfigTemplateProvider provider;
	
	@Inject
	private LobbyTableInspector inspector;
	
	@Inject
	private TableActionHandler handler;
	
	@Log4j
	private Logger log;
	
	@Override 
	public void run() {
		log.trace("Table manager executing.");
		List<TableConfigTemplate> templs = provider.getTemplates();
		log.debug("Found " + templs + " templates.");
		List<TableModifierAction> actions = inspector.match(templs);
		log.debug("Inspector resports " + actions.size() + " actions.");
		for (TableModifierAction a : actions) {
			handler.handleAction(a);
		}
	}
}

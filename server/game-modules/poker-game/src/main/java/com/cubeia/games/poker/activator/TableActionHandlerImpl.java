package com.cubeia.games.poker.activator;

import org.apache.log4j.Logger;

import com.cubeia.backend.cashgame.dto.CloseTableRequest;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.activator.TableFactory;
import com.cubeia.firebase.api.routing.ActivatorRouter;
import com.cubeia.firebase.guice.inject.Log4j;
import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TableActionHandlerImpl implements TableActionHandler {

	@Inject
	private TableFactory tables;
	
	@Inject
	private ParticipantFactory participants;
	
	@Log4j
	private Logger log;
	
	@Inject
	private ActivatorRouter router;
	
	@Override
	public void handleAction(TableModifierAction action) {
		switch(action.getType()) {
			case CLOSE : {
				doClose(action.getTableId());
				break;
			}
			case CREATE : {
				doCreate(action.getTemplate());
				break;
			}
			case DESTROY : {
				doDestroy(action.getTableId());
				break;
			}
		}
	}

	
	// --- PRIVATE METHODS --- //
	
	private void doCreate(TableConfigTemplate template) {
		log.debug("Creating table for template: " + template.getId());
		tables.createTable(template.getSeats(), participants.createParticipantFor(template));
	}

	private void doDestroy(int tableId) {
		log.debug("Remove lobby attribute is set for table[" + tableId + "] so it will be destroyed.");
		tables.destroyTable(tableId, true);
	}

	private void doClose(int tableId) {
		log.debug("Table[" + tableId + "] is elegible for closure, sending close request.");
		GameObjectAction action = new GameObjectAction(tableId);
		action.setAttachment(new CloseTableRequest());
		router.dispatchToGame(tableId, action);
	}
}

package com.cubeia.games.poker.activator;

import com.cubeia.games.poker.entity.TableConfigTemplate;

public interface ParticipantFactory {

	public PokerParticipant createParticipantFor(TableConfigTemplate template);
	
}

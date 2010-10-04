package com.cubeia.games.poker.persistence.history;

import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.games.poker.persistence.AbstractDAO;

public class HandHistoryDAO extends AbstractDAO {

    public HandHistoryDAO(ServiceRegistry registry) {
        super(registry);
    }

}

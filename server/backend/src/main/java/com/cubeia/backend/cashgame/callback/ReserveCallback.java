package com.cubeia.backend.cashgame.callback;

import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveResponse;

public interface ReserveCallback {

	void requestSucceded(ReserveResponse response);
	
	void requestFailed(ReserveFailedResponse response);
}

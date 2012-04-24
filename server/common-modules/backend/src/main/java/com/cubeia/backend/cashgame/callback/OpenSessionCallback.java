package com.cubeia.backend.cashgame.callback;

import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;

public interface OpenSessionCallback {

	void requestSucceeded(OpenSessionResponse response);
	
	void requestFailed(OpenSessionFailedResponse response);
}

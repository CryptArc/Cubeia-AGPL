package com.cubeia.backend.cashgame;

import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveResponse;

public class ReserveCallbackHandler extends CallbackHandler implements ReserveCallback {

	@Override
	public void requestSucceded(ReserveResponse response) {
		setResponse(response);
	}

	@Override
	public void requestFailed(ReserveFailedResponse response) {
		setResponse(response);
	}
}

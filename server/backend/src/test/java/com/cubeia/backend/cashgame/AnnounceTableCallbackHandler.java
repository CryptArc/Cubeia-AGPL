package com.cubeia.backend.cashgame;

import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;

class AnnounceTableCallbackHandler extends CallbackHandler implements AnnounceTableCallback {
	@Override
	public void requestSucceded(AnnounceTableResponse response) {
		setResponse(response);
	}

	@Override
	public void requestFailed(AnnounceTableFailedResponse response) {
		setResponse(response);
	}
}
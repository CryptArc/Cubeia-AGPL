package com.cubeia.backend.cashgame;


import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;

public class OpenSessionCallbackHandler extends CallbackHandler implements OpenSessionCallback {
    @Override
    public void requestFailed(OpenSessionFailedResponse response) {
        setResponse(response);
    }

    @Override
    public void requestSucceeded(OpenSessionResponse response) {
        setResponse(response);
    }
}

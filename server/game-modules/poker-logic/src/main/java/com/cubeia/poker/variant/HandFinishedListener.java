package com.cubeia.poker.variant;

import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.result.HandResult;

public interface HandFinishedListener {

    /**
     * Invoked when a hand has finished.
     */
    public void handFinished(HandResult result, HandEndStatus status);
}

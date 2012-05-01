package com.cubeia.games.poker.adapter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A sequence generator for actions. This class should be a singleton at least per table.
 * Sequence numbers are used to check the validity of responses from the client.
 * <p/>
 * This class is thread safe.
 *
 * @author w
 */
public class ActionSequenceGenerator {

    private AtomicInteger sequenceCounter = new AtomicInteger();

    public int next() {
        int seq = sequenceCounter.incrementAndGet();
        seq = resetSequenceIfWrappedAround(seq);
        return seq;
    }

    private int resetSequenceIfWrappedAround(int seq) {
        if (seq < 0) {
            // This is not thread safe in the respect that we might set
            // the counter to 0 multiple times. However, this should be
            // fine since we will not be able to this on the same table.
            seq = 0;
            sequenceCounter.set(seq);
        }
        return seq;
    }

}

package com.cubeia.backend.cashgame.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class BatchHandResponse implements Serializable {

    private final List<BalanceUpdate> resultingBalances;

    public BatchHandResponse() {
        this(new LinkedList<BalanceUpdate>());
    }

    public BatchHandResponse(List<BalanceUpdate> resultingBalances) {
        this.resultingBalances = resultingBalances;
    }

    public void addResultEntry(BalanceUpdate balanceUpdate) {
        resultingBalances.add(balanceUpdate);
    }

    public List<BalanceUpdate> getResultingBalances() {
        return new ArrayList<BalanceUpdate>(resultingBalances);
    }
}

package com.cubeia.poker.states;

import com.cubeia.poker.adapter.ServerAdapter;

public class ServerAdapterHolder {

    private transient ServerAdapter serverAdapter;

    public ServerAdapter get() {
        return serverAdapter;
    }

    public void set(ServerAdapter serverAdapter) {
        this.serverAdapter = serverAdapter;
    }
}

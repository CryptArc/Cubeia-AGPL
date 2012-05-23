package com.cubeia.poker.adapter;

import com.cubeia.poker.adapter.ServerAdapter;

import java.io.Serializable;

public class ServerAdapterHolder implements Serializable {

    private transient ServerAdapter serverAdapter;

    public ServerAdapter get() {
        return serverAdapter;
    }

    public void set(ServerAdapter serverAdapter) {
        this.serverAdapter = serverAdapter;
    }
}

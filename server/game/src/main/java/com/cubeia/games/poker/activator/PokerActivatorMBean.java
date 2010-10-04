package com.cubeia.games.poker.activator;

public interface PokerActivatorMBean {

    public int getMultiplier();
    
    public void createTable(String domain, int seats, int level);
    
    public void setMultiplier(int multiplier);
    
    public void destroyTable(int id);
}

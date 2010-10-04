package com.cubeia.games.poker.jmx;

import java.util.Date;

public interface PokerStatsMBean {
    
    public int getHandsPerHour();
    
    public int getHandsPerMinute();
    
    public String getCurrentState(int tableId);
    
    public Date getLastChangeDate(int tableId);
    
    public void setStateTrackingEnabled(boolean enabled);
    
    public boolean isStateTrackingEnabled();
}

package com.cubeia.games.poker.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cubeia.firebase.api.action.AbstractPlayerAction;
import com.cubeia.firebase.api.action.LeaveAction;
import com.cubeia.firebase.api.action.WatchResponseAction;
import com.cubeia.firebase.api.game.GameNotifier;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableListener;
import com.cubeia.firebase.api.game.table.TablePlayerSet;
import com.cubeia.firebase.api.game.table.TableWatcherSet;

public class PlayerUnseaterTest {

    @Mock private Table table;
    @Mock private TablePlayerSet tablePlayerSet;
    @Mock private TableListener tableListener;
    @Mock private GameNotifier gameNotifier;
    @Mock private TableWatcherSet tableWatcherSet;
    private int playerId = 34;
    private PlayerUnseater playerUnseater;
    

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        playerUnseater = new PlayerUnseater();
        
        when(table.getPlayerSet()).thenReturn(tablePlayerSet);
        when(table.getListener()).thenReturn(tableListener);
        when(table.getNotifier()).thenReturn(gameNotifier);
        when(table.getWatcherSet()).thenReturn(tableWatcherSet);
    }

    @Test
    public void unseatPlayer() {
        playerUnseater.unseatPlayer(table, playerId, false);

        verify(tablePlayerSet).unseatPlayer(playerId);
        verify(tableListener).playerLeft(table, playerId);
        verify(tableListener, never()).watcherJoined(table, playerId);
    }

    @Test
    public void unseatPlayerSeatAsWatcher() {
        playerUnseater.unseatPlayer(table, playerId, true);
        
        verify(tablePlayerSet).unseatPlayer(playerId);
        verify(tableListener).playerLeft(table, playerId);
        verify(tableWatcherSet).addWatcher(playerId);
        verify(tableListener).watcherJoined(table, playerId);
        
        ArgumentCaptor<AbstractPlayerAction> actionCaptor = ArgumentCaptor.forClass(AbstractPlayerAction.class);
        verify(gameNotifier, times(2)).sendToClient(Mockito.eq(playerId), actionCaptor.capture());
        List<AbstractPlayerAction> actions = actionCaptor.getAllValues();
        assertThat(actions.get(0), is(LeaveAction.class));
        assertThat(actions.get(1), is(WatchResponseAction.class));
    }
}

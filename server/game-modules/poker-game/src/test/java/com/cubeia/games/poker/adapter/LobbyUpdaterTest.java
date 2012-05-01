package com.cubeia.games.poker.adapter;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.games.poker.state.FirebaseState;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LobbyUpdaterTest {

    @Mock
    private FirebaseState fbState;
    @Mock
    private Table table;
    @Mock
    private LobbyTableAttributeAccessor lobbyTableAttributeAccessor;

    @Test
    public void testUpdateLobby() {
        initMocks(this);
        when(table.getAttributeAccessor()).thenReturn(lobbyTableAttributeAccessor);
        int handCount = 234;
        when(fbState.getHandCount()).thenReturn(handCount);
        LobbyUpdater lobbyUpdater = new LobbyUpdater();

        lobbyUpdater.updateLobby(fbState, table);

        ArgumentCaptor<AttributeValue> attributeCaptor = ArgumentCaptor.forClass(AttributeValue.class);
        verify(lobbyTableAttributeAccessor).setAttribute(Mockito.eq("handcount"), attributeCaptor.capture());
        AttributeValue attributeValue = attributeCaptor.getValue();
        assertThat(attributeValue.getIntValue(), is(handCount));
    }

}

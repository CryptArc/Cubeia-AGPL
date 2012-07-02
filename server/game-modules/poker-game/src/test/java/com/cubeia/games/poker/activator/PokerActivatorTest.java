/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.activator;

import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableGameState;
import com.cubeia.firebase.api.game.table.TablePlayerSet;
import com.cubeia.firebase.api.game.table.TableSeatingMap;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.games.poker.tournament.activator.TournamentTableSettings;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.variant.GameType;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PokerActivatorTest {

    PokerActivator activator;

    @Mock
    private Injector injector;

    @Mock
    private Table table;

    @Mock
    private LobbyAttributeAccessor accessor;

    @Mock
    private PokerState pokerState;

    @Mock
    private TablePlayerSet playerSet;

    @Mock
    private TableSeatingMap seatingMap;

    @Mock
    private TableGameState gameState;

    @Before
    public void setup() {
        initMocks(this);
        this.activator = new TestablePokerActivator(injector);
        when(injector.getInstance(PokerState.class)).thenReturn(pokerState);
        when(table.getPlayerSet()).thenReturn(playerSet);
        when(playerSet.getSeatingMap()).thenReturn(seatingMap);
        when(seatingMap.getNumberOfSeats()).thenReturn(6);
        when(table.getGameState()).thenReturn(gameState);
    }

    @Test
    public void testBlindsForTournamentTable() {
        TournamentTableSettings settings = new TournamentTableSettings(10, 20);
        activator.mttTableCreated(table, 1, settings, accessor);

        ArgumentCaptor<PokerSettings> captor = ArgumentCaptor.forClass(PokerSettings.class);
        verify(pokerState).init(isA(GameType.class), captor.capture());
        PokerSettings pokerSettings = captor.getValue();
        assertEquals(10, pokerSettings.getSmallBlindAmount());
        assertEquals(20, pokerSettings.getBigBlindAmount());
    }
}

package com.cubeia.games.poker.activator;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.firebase.api.game.table.Table;

public class MapTableNameManagerTest {

    @Test
    public void testAddAndRemove() {
        TableNameManager man = new MapTableNameManager("test_table_names.txt", false);
        // test 3 first
        assertEquals("A", man.tableCreated(table(1)));
        assertEquals("B", man.tableCreated(table(2)));
        assertEquals("C", man.tableCreated(table(3)));
        // new 3 more
        assertEquals("A 2", man.tableCreated(table(4)));
        assertEquals("B 2", man.tableCreated(table(5)));
        assertEquals("C 2", man.tableCreated(table(6)));
        // remove and readd
        man.tableDestroyed(5); // B 2
        assertEquals("B 2", man.tableCreated(table(7)));
    }

    @Test
    public void testFindDefaults() {
        new MapTableNameManager();
    }


    // --- PRIVATE METHODS --- //

    private Table table(int tableId) {
        Table t = Mockito.mock(Table.class);
        Mockito.when(t.getId()).thenReturn(tableId);
        return t;
    }
}

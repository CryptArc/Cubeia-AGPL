package com.cubeia.games.poker.activator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.game.table.Table;
import com.google.inject.Singleton;

@Singleton
public class MapTableNameManager implements TableNameManager {

    private static final String DEFAULT_TABLE_NAMES = "default_table_names.txt";

    private final Map<Integer, Name> tableToName = new HashMap<Integer, Name>();
    private final List<Name> orderList = new ArrayList<Name>();

    private final Logger log = Logger.getLogger(getClass());
    private final Random random = new Random();

    private final String listName;
    private final boolean randomName;

    public MapTableNameManager() {
        this("table_names.txt", true);
    }

    public MapTableNameManager(String listName, boolean randomName) {
        this.listName = listName;
        this.randomName = randomName;
        initNamesFromClassPath();
        resort();
    }

    @Override
    public String tableCreated(Table table) {
        Name name = selectName();
        String tmp = name.get();
        tableToName.put(table.getId(), name);
        name.count++;
        resort();
        return tmp;
    }

    @Override
    public void tableDestroyed(int tableId) {
        Name name = tableToName.remove(tableId);
        if (name != null) {
            name.count--;
            resort();
        }
    }


    // --- PRIVATE METHODS --- //

    private Name selectName() {
        if (!randomName) {
            return orderList.get(0);
        } else {
            int index = 0;
            int count = orderList.get(0).count;
            for (Name n : orderList) {
                index++;
                if (n.count > count) {
                    break;
                }
            }
            index = random.nextInt(index);
            return orderList.get(index);
        }
    }

    private void resort() {
        Collections.sort(orderList);
    }

    private void initNamesFromClassPath() {
        log.info("Trying to read table names from file on class path: " + listName);
        List<String> names = tryRead(listName, false);
        if (names == null) {
            log.info("Could not find '" + listName + "' falling back on default names");
            names = tryRead(DEFAULT_TABLE_NAMES, true);
        }
        int count = 0;
        for (String name : names) {
            orderList.add(new Name(name));
            count++;
        }
        log.info("Initiated with " + count + " table names");
    }

    private List<String> tryRead(String name, boolean required) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(name);
        if (in == null) {
            if (required) {
                throw new IllegalStateException("Could not find default table file");
            } else {
                return null;
            }
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                List<String> list = new LinkedList<String>();
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                return list;
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read name list", e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }


    // --- PRIVATE CLASSES --- //

    private static class Name implements Comparable<Name> {

        private final String name;
        private int count = 1;

        public Name(String name) {
            this.name = name;
        }

        public String get() {
            if (count > 1) {
                return name + " " + count;
            } else {
                return name;
            }
        }

        @Override
        public int compareTo(Name o) {
            if (count == o.count) {
                return name.compareTo(o.name);
            } else {
                return count < o.count ? -1 : 1;
            }
        }
    }
}

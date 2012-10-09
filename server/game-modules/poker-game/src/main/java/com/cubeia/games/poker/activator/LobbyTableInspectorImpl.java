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

import static com.cubeia.firebase.api.game.lobby.DefaultTableAttributes._LAST_MODIFIED;
import static com.cubeia.firebase.api.game.lobby.DefaultTableAttributes._SEATED;
import static com.cubeia.games.poker.lobby.PokerLobbyAttributes.TABLE_TEMPLATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.activator.TableFactory;
import com.cubeia.firebase.api.game.lobby.LobbyTable;
import com.cubeia.firebase.guice.inject.Log4j;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.game.poker.config.api.PokerConfigurationService;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.cubeia.games.poker.lobby.PokerLobbyAttributes;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LobbyTableInspectorImpl implements LobbyTableInspector {

    @Log4j
    private Logger log;

    @Inject
    private TableFactory factory;

    @Inject
    private SystemTime time;

    @Service
    private PokerConfigurationService config;

    @Override
    public List<TableModifierAction> match(List<TableConfigTemplate> templates) {
        List<TableModifierAction> result = new ArrayList<TableModifierAction>();
        List<LobbyTable> allTables = getAllTables();
        // first pass: check for destruction
        checkDestruction(allTables, result);
        // divide into groups by template
        Map<Integer, List<LobbyTable>> tables = partitionTables(allTables);
        log.debug("Found " + allTables.size() + " tables in " + tables.size() + " templates");
        // now check all templates for closure or creation
        checkTemplates(templates, tables, result);
        // now check if any templates are missing
        checkMissingTemplates(templates, allTables, tables, result);
        return result;
    }

	// --- PRIVATE METHODS --- //
    
    private List<LobbyTable> getAllTables() {
    	LobbyTable[] arr = factory.listTables();
    	List<LobbyTable> list = new LinkedList<LobbyTable>();
    	for (LobbyTable t : arr) {
    		list.add(t);
    	}
		return list;
	}

    private void checkMissingTemplates(List<TableConfigTemplate> templates, List<LobbyTable> allTables, Map<Integer, List<LobbyTable>> tables, List<TableModifierAction> result) {
        Set<Integer> templateIds = collectTemplateIds(templates);
        for (Integer id : tables.keySet()) {
            if (!templateIds.contains(id)) {
                // missing template, check if empty and close immediately if so
                for (LobbyTable t : tables.get(id)) {
                    boolean b = isEmpty(t);
                    log.debug("Table[" + t.getTableId() + "] is registered on missing template " + id + ", will close if empty: " + b);
                    if (b) {
                        result.add(TableModifierAction.close(t.getTableId()));
                    }
                }
            }
        }
    }


    private Set<Integer> collectTemplateIds(List<TableConfigTemplate> templates) {
        Set<Integer> templateIds = new HashSet<Integer>(templates.size());
        for (TableConfigTemplate t : templates) {
            templateIds.add(t.getId());
        }
        return templateIds;
    }

    private void checkTemplates(List<TableConfigTemplate> templates, Map<Integer, List<LobbyTable>> tables, List<TableModifierAction> result) {
        // for each template
        for (TableConfigTemplate config : templates) {
            List<LobbyTable> list = tables.get(config.getId());
            // create a list if it doesn't exist
            if (list == null) {
                list = new ArrayList<LobbyTable>();
                tables.put(config.getId(), list);
            }
            /*
             * Now, if "all" is less than minimum, add. If "empty
             * is less then minimum empty, add. Otherwise, check for
             * closure.
             */
            int all = list.size();
            int empty = countEmptyTables(list);
            int min = config.getMinTables();
            int minEmpty = config.getMinEmptyTables();
            if (all < min) {
                createTables(config, result, min - all);
            } else if (empty < minEmpty) {
                createTables(config, result, minEmpty - empty);
            } else {
                if (empty >= min) {
                    // remove all above "min"
                    checkClosure(config, list, result, empty - min);
                } else if (empty > minEmpty) {
                    // remove all above "minEmpty"
                    checkClosure(config, list, result, empty - minEmpty);
                }
            }
        }
    }

    private void createTables(TableConfigTemplate config, List<TableModifierAction> result, int num) {
        for (int i = 0; i < num; i++) {
            log.debug("Adding new table for config: " + config.getId());
            result.add(TableModifierAction.create(config));
        }
    }

    private int countEmptyTables(List<LobbyTable> list) {
        int i = 0;
        for (LobbyTable t : list) {
            if (isEmpty(t)) {
                i++;
            }
        }
        return i;
    }


    /*
     * Check for tables to be destroyed, regardless of template, and return all other tables
     */
    private void checkDestruction(List<LobbyTable> allTables, List<TableModifierAction> result) {
        for (Iterator<LobbyTable> it = allTables.iterator(); it.hasNext(); ) {
        	LobbyTable table = it.next();
            if (isClosed(table)) {
                log.debug("Table[" + table.getTableId() + "] is closed, will be destroyed.");
                result.add(TableModifierAction.destroy(table.getTableId()));
                it.remove();
            }
        }
    }

    private boolean isClosed(LobbyTable table) {
        AttributeValue val = table.getAttributes().get(PokerLobbyAttributes.TABLE_READY_FOR_CLOSE.name());
        boolean b = (val != null && val.getIntValue() > 0);
        if (log.isTraceEnabled()) {
            log.trace("Table " + table.getTableId() + " is closed: " + b + " (attribute: " + (val == null ? "null" : val.getIntValue()) + ")");
        }
        return b;
    }

    private void checkClosure(TableConfigTemplate config, List<LobbyTable> list, List<TableModifierAction> result, int maxRemove) {
        if (maxRemove < 1) {
            return; // NOTHING TO DO
        }
        int check = 0;
        for (Iterator<LobbyTable> it = list.iterator(); it.hasNext(); ) {
            LobbyTable table = it.next();
            if (isEmpty(table) && isStale(table, config)) {
                check++;
                log.debug("Table[" + table.getTableId() + "] is empty, will send close request.");
                result.add(TableModifierAction.close(table.getTableId()));
                it.remove(); // do not do anything else on table
                if (check == maxRemove) {
                    log.debug("Short-cutting the closure process, have marked " + check + " tables");
                    break;
                }
            }
        }
    }

    private boolean isStale(LobbyTable table, TableConfigTemplate templ) {
        String tmp = table.getAttributes().get(_LAST_MODIFIED.name()).getStringValue();
        long timestamp = Long.valueOf(tmp);
        long ttl = templ.getTTL();
        if (ttl <= 0) {
            // fall back on configuration
            ttl = config.getActivatorConfig().getDefaultTableTTL();
        }
        boolean b = (time.now() - timestamp) > ttl;
        if (log.isTraceEnabled()) {
            log.trace("Table " + table.getTableId() + " is stale: " + b);
        }
        return b;
    }

    private boolean isEmpty(LobbyTable table) {
        boolean b = table.getAttributes().get(_SEATED.name()).getIntValue() == 0;
        if (log.isTraceEnabled()) {
            log.trace("Table " + table.getTableId() + " is empty: " + b);
        }
        return b;
    }

    private Map<Integer, List<LobbyTable>> partitionTables(List<LobbyTable> tables) {
        Map<Integer, List<LobbyTable>> map = new HashMap<Integer, List<LobbyTable>>();
        for (LobbyTable t : tables) {
            int template = getTemplateId(t);
            List<LobbyTable> list = map.get(template);
            if (list == null) {
                list = new ArrayList<LobbyTable>();
                map.put(template, list);
            }
            list.add(t);
        }
        return map;
    }

	private int getTemplateId(LobbyTable t) {
		Map<String, AttributeValue> map = t.getAttributes();
		return (map.containsKey(TABLE_TEMPLATE.name()) ? map.get(TABLE_TEMPLATE.name()).getIntValue() : -1);
	}
}

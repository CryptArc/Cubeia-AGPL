"use strict";
var Poker = Poker || {};
/**
 * Handles the lobby UI
 * @type {Poker.LobbyManager}
 */
Poker.LobbyManager = Class.extend({
    lobbyTableData:null,
    listItemTemplate:null,
    currentScroll:null,
    lobbyLayoutManager : null,
    /**
     * @constructor
     */
    init: function () {
        this.lobbyTableData = [];
        var templateManager = new Poker.TemplateManager();
        this.lobbyLayoutManager = Poker.AppCtx.getLobbyLayoutManager();

    },

    handleTableSnapshotList : function (tableSnapshotList) {
        for (var i = 0; i < tableSnapshotList.length; i++) {
            this.handleTableSnapshot(tableSnapshotList[i]);
        }
        this.lobbyLayoutManager.createTableList(this.lobbyTableData);

    },
    handleTableSnapshot : function (tableSnapshot) {
        console.log("tableSnapshot:")
        console.log(tableSnapshot);
        if (this.findTable(tableSnapshot.tableid) === null) {

            var showInLobby = Poker.ProtocolUtils.readParam("VISIBLE_IN_LOBBY",tableSnapshot.params);
            console.log("handle table snapshot show in lobby = " + showInLobby)
            if(parseInt(showInLobby) == 0 ) {
                return;
            }
            var data = Poker.ProtocolUtils.extractTableData(tableSnapshot);
            var i = this.lobbyTableData.push(data);

        } else {
            console.log("duplicate found - tableid: " + tableSnapshot.tableid);
        }
    },
    handleSitAndGoSnapshotList : function(sitAndGoSnapshotList) {
        for (var i = 0; i < sitAndGoSnapshotList.length; i++) {
            this.handleTournamentSnapshot(sitAndGoSnapshotList[i]);
        }
        this.lobbyLayoutManager.createTournamentList(this.lobbyTableData);
    },
    handleTournamentSnapshotList:function (sitAndGoSnapshotList) {
        for (var i = 0; i < sitAndGoSnapshotList.length; i++) {
            this.handleTournamentSnapshot(sitAndGoSnapshotList[i]);
        }
        this.lobbyLayoutManager.createTournamentList(this.lobbyTableData);
    },
    handleTournamentSnapshot:function (snapshot) {
        if (this.findSitAndGo(snapshot.mttid) === null) {
            var data = Poker.ProtocolUtils.extractTournamentData(snapshot);
            var i = this.lobbyTableData.push(data);

        } else {
            console.log("duplicate found - mttid: " + snapshot.mttid);
        }
    },

    handleTournamentUpdates:function (tournamentUpdateList) {
        console.log("Received tournament updates");
        for (var i = 0; i < tournamentUpdateList.length; i++) {
            this.handleTournamentUpdate(tournamentUpdateList[i]);
        }

    },

    handleTournamentUpdate:function (tournamentUpdate) {
        console.log("Updating tournament: " + tournamentUpdate.mttid);
        var tournamentData = this.findTournament(tournamentUpdate.mttid);
        if (tournamentData) {
            var registered = Poker.ProtocolUtils.readParam("REGISTERED", tournamentUpdate.params);
            if (tournamentData.seated == registered) {
                //console.log("on update, registered players the same, skipping update");
                return;
            }
            if (registered != undefined) tournamentData.seated = registered;
            var status = Poker.ProtocolUtils.readParam("STATUS", tournamentUpdate.params);
            this.lobbyLayoutManager.updateTournamentItem(tournamentData);
            console.log("Tournament " + tournamentData.id + "  updated, registered = " + tournamentData.seated);
        } else {
            console.log("Ignored tournament update, mtt not found: " + tournamentUpdate.mttid);
        }
    },

    getTableStatus:function (seated, capacity) {
        if (seated == capacity) {
            return "full";
        }
        return "open";
    },
    getBettingModel:function (model) {
        if (model == "NO_LIMIT") {
            return "NL"
        } else if (model == "POT_LIMIT") {
            return "PL";
        } else if (model == "FIXED_LIMIT") {
            return "FL";
        }
        return model;
    },
    handleTableUpdateList:function (tableUpdateList) {
        for (var i = 0; i < tableUpdateList.length; i++) {
            this.handleTableUpdate(tableUpdateList[i]);
        }
    },
    handleTableUpdate:function (tableUpdate) {
        var showInLobby = Poker.ProtocolUtils.readParam("VISIBLE_IN_LOBBY",tableUpdate.params);

        if(showInLobby!=null && parseInt(showInLobby) == 0 ) {

            this.handleTableRemoved(tableUpdate.tableid);
            return;
        }
        var self = this;
        var tableData = this.findTable(tableUpdate.tableid);
        if (tableData) {
            console.log("updating table " + tableData.name);
            if (tableData.seated == tableUpdate.seated) {
                //console.log("on update, seated players the same, skipping update");
                return;
            }
            tableData.seated = tableUpdate.seated;

            this.lobbyLayoutManager.updateTableItem(tableData);
            console.log("table " + tableData.id + "  updated, seated = " + tableData.seated);
        }
    },

    handleTableRemoved : function (tableid) {
        this.removeTable(tableid);
        this.lobbyLayoutManager.tableRemoved(tableid);

    },
    removeTable : function (tableid) {
        for (var i = 0; i < this.lobbyTableData.length; i++) {
            var object = this.lobbyTableData[i];
            if (object.id == tableid) {
                this.lobbyTableData.splice(i, 1);
                return;
            }
        }
    },

    findTable:function (tableid) {
        for (var i = 0; i < this.lobbyTableData.length; i++) {
            var object = this.lobbyTableData[i];
            if (object.id == tableid) {
                return object;
            }
        }
        return null;
    },

    findTournament:function (tournamentId) {
        for (var i = 0; i < this.lobbyTableData.length; i++) {
            var object = this.lobbyTableData[i];
            if (object.id == tournamentId) {
                return object;
            }
        }
        return null;
    },

    findSitAndGo:function (tournamentId) {
        for (var i = 0; i < this.lobbyTableData.length; i++) {
            var object = this.lobbyTableData[i];
            if (object.id == tournamentId) {
                return object;
            }
        }
        return null;
    },
    reSort : function () {

    },
    clearLobby : function () {
        this.lobbyTableData = [];
        $("#tableListItemContainer").empty();
    },

    createClickFunction:function (tables, data) {
        var self = this;
        console.log("Creating click function. Tables?: " + tables);
        var comHandler = Poker.AppCtx.getCommunicationManager();
        var click = function(e) {
            if (tables) {
                comHandler.openTable(data.id, data.capacity,self.getTableDescription(data));
            } else {
                comHandler.openTournamentLobby(data.id,data.name);

            }
        }
        return click;
    },
    getCapacity:function (id) {
        var tableData = this.findTable(id);
        return tableData.capacity;
    }

});

Poker.LobbyFilter = Class.extend({
    enabled:false,
    id:null,
    filterFunction:null,
    lobbyLayoutManager:null,
    init : function (id, enabled, filterFunction, lobbyLayoutManager) {
        this.enabled = Poker.Utils.loadBoolean(id, true);

        this.id = id;
        this.filterFunction = filterFunction;
        this.lobbyLayoutManager = lobbyLayoutManager;
        var self = this;

        $("#" + id).click(function () {
            self.enabled = !self.enabled;
            $(this).toggleClass("active");
            Poker.Utils.store(self.id, self.enabled);
            self.filterUpdated();

        });
        if (this.enabled == true) {
            $("#" + this.id).addClass("active");
        } else {
            $("#" + this.id).removeClass("active");
        }
    },
    filterUpdated:function () {
        this.lobbyLayoutManager.createTableList(Poker.AppCtx.getLobbyManager().lobbyTableData);
    },
    /**
     * Returns true if it should be included in the lobby and
     * false if it shouldn't
     * @param lobbyData
     * @return {boolean} if it should be included
     */
    filter:function (lobbyData) {
        return this.filterFunction(this.enabled, lobbyData);
    }
});

Poker.PropertyMinMaxFilter = Poker.LobbyFilter.extend({
    min:-1,
    max:-1,
    property:null,
    init:function (id, enabled, lobbyLayoutManager, property, min, max) {
        this.min = min;
        this.max = max;
        this.property = property;
        var self = this;
        this._super(id, enabled, function (enabled, lobbyData) {
            return self.doFilter(enabled, lobbyData);
        }, lobbyLayoutManager);

    },
    doFilter:function (enabled, lobbyData) {
        var p = lobbyData[this.property];
        if (typeof(p) != "undefined" && !this.enabled) {
            if (this.max != -1 && this.min != -1) {
                return p > this.max || p < this.min;
            } else if (this.max != -1) {
                return p > this.max;
            } else if (this.min != -1) {
                return p < this.min;
            } else {
                console.log("PropertyFilter: neither min or max is defined");
                return true;
            }
        } else {
            return true;
        }
    }
});

Poker.PropertyStringFilter = Poker.LobbyFilter.extend({
    str:null,
    property:null,
    init:function (id, enabled, lobbyLayoutManager, property, str) {
        this.property = property;
        this.str = str;
        var self = this;
        this._super(id, enabled, function (enabled, lobbyData) {
            return self.doFilter(enabled, lobbyData);
        }, lobbyLayoutManager);

    },
    doFilter : function (enabled, lobbyData) {
        var p = lobbyData[this.property];
        if (typeof(p) != "undefined" && !this.enabled) {
            if (p !== this.str) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
});






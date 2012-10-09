"use strict";
var Poker = Poker || {};
/**
 * Handles the lobby UI
 * @type {Poker.LobbyLayoutManager}
 */
Poker.LobbyLayoutManager = Class.extend({
    lobbyData:[],
    listItemTemplate:null,
    filters:[],
    currentScroll:null,
    registeredTournaments:[],
    /**
     * @constructor
     */
    init:function () {
        var templateManager = new Poker.TemplateManager();
        this.listItemTemplate = templateManager.getTemplate("tableListItemTemplate");
        var self = this;

        $("#cashGameMenu").click(function (e) {
            $(".main-menu a.selected").removeClass("selected");
            $(this).addClass("selected");
            comHandler.subscribeToCashGames();
        });
        $("#sitAndGoMenu").click(function (e) {
            $(".main-menu .selected").removeClass("selected");
            $(this).addClass("selected");
            comHandler.subscribeToSitAndGos();
        });
        $("#tournamentMenu").click(function (e) {
            $(".main-menu .selected").removeClass("selected");
            $(this).addClass("selected");
            comHandler.subscribeToTournaments();
        });

        $(".show-filters").click(function () {
            $(this).toggleClass("selected");
            $(".table-filter").toggle();
        });
        this.initFilters();
    },
    initFilters:function () {
        var fullTablesFilter = new Poker.LobbyFilter("fullTables", true,
            function (enabled, lobbyData) {
                if (!enabled) {
                    return lobbyData.seated < lobbyData.capacity;
                } else {
                    return true;
                }
            }, this);
        this.filters.push(fullTablesFilter);
        var emptyTablesFilter = new Poker.LobbyFilter("emptyTables", true,
            function (enabled, lobbyData) {
                if (!enabled) {
                    return lobbyData.seated > 0;
                } else {
                    return true;
                }

            }, this);

        this.filters.push(emptyTablesFilter);

        var noLimit = new Poker.PropertyStringFilter("noLimit", true, this, "type", "NL");
        this.filters.push(noLimit);

        var potLimit = new Poker.PropertyStringFilter("potLimit", true, this, "type", "PL");
        this.filters.push(potLimit);

        var fixedLimit = new Poker.PropertyStringFilter("fixedLimit", true, this, "type", "FL");
        this.filters.push(fixedLimit);

        var highStakes = new Poker.PropertyMinMaxFilter("highStakes", true, this, "smallBlind", 1000, -1);

        this.filters.push(highStakes);

        var mediumStakes = new Poker.PropertyMinMaxFilter("mediumStakes", true, this, "smallBlind", 50, 1000);
        this.filters.push(mediumStakes);

        var lowStakes = new Poker.PropertyMinMaxFilter("lowStakes", true, this, "smallBlind", -1, 50);
        this.filters.push(lowStakes);
    },
    handleTableSnapshotList:function (tableSnapshotList) {
        for (var i = 0; i < tableSnapshotList.length; i++) {
            this.handleTableSnapshot(tableSnapshotList[i]);
        }
        this.createGrid(true);
    },
    handleTableSnapshot:function (tableSnapshot) {
        console.log("tableSnapshot:")
        console.log(tableSnapshot);
        if (this.findTable(tableSnapshot.tableid) === null) {

            var showInLobby = this.readParam("VISIBLE_IN_LOBBY",tableSnapshot.params);
            if(parseInt(showInLobby) == 0 ) {
                return;
            }

            var speedParam = this.readParam("SPEED", tableSnapshot.params);
            //var variant = this.readParam("VARIANT", tableSnapshot.params);
            var bettingModel = this.readParam("BETTING_GAME_BETTING_MODEL", tableSnapshot.params);
            var ante = this.readParam("BETTING_GAME_ANTE", tableSnapshot.params);
            var smallBlind = this.readParam("SMALL_BLIND", tableSnapshot.params);
            var bigBlind = this.readParam("BIG_BLIND", tableSnapshot.params);


            var data = {
                id:tableSnapshot.tableid,
                name:tableSnapshot.name,
                speed:speedParam,
                capacity:tableSnapshot.capacity,
                seated:tableSnapshot.seated,
                blinds:(Poker.Utils.formatBlinds(smallBlind) + "/" + Poker.Utils.formatBlinds(bigBlind)),
                type:this.getBettingModel(bettingModel),
                tableStatus:this.getTableStatus(tableSnapshot.seated, tableSnapshot.capacity),
                smallBlind:smallBlind
            };
            var i = this.lobbyData.push(data);

        } else {
            console.log("duplicate found - tableid: " + tableSnapshot.tableid);
        }
    },
    handleTournamentSnapshotList:function (sitAndGoSnapshotList) {
        for (var i = 0; i < sitAndGoSnapshotList.length; i++) {
            this.handleTournamentSnapshot(sitAndGoSnapshotList[i]);
        }
        this.createGrid(false);
    },
    handleTournamentSnapshot:function (snapshot) {
        if (this.findSitAndGo(snapshot.mttid) === null) {

            var speedParam = "N/A";
            var bettingModel = "N/A";
            var ante = "N/A";
            var params = snapshot.params;

            var data = {id:snapshot.mttid,
                name:this.readParam("NAME", params),
                speed:speedParam,
                capacity:this.readParam("CAPACITY", params),
                seated:this.readParam("REGISTERED", params),
                blinds:"N/A",
                type:"NL",
                tableStatus:"open",
                ante:ante
            };
            var i = this.lobbyData.push(data);

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
            var registered = this.readParam("REGISTERED", tournamentUpdate.params);
            if (tournamentData.seated == registered) {
                //console.log("on update, registered players the same, skipping update");
                return;
            }
            if (registered != undefined) tournamentData.seated = registered;
            var status = this.readParam("STATUS", tournamentUpdate.params);
            console.log("New mtt status: " + status);
            //it might be filtered out
            var item = $("#tableItem" + tournamentData.id);
            if (item.length > 0) {
                item.unbind().replaceWith(this.getTableItemHtml(tournamentData));
                var item = $("#tableItem" + tournamentData.id);  //need to pick it up again to be able to bind to it
                item.click(this.createClickFunction(false, tournamentData));
            }
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
        console.log("table update");
        console.log(tableUpdate);

        var showInLobby = this.readParam("VISIBLE_IN_LOBBY",tableUpdate.params);
        if(showInLobby!=null && parseInt(showInLobby) == 0 ) {
            this.handleTableRemoved(tableUpdate.tableid);
            return;
        }

        var self = this;
        var tableData = this.findTable(tableUpdate.tableid);
        if (tableData) {
            if (tableData.seated == tableUpdate.seated) {
                //console.log("on update, seated players the same, skipping update");
                return;
            }
            tableData.seated = tableUpdate.seated;
            //it might be filtered out
            var item = $("#tableItem" + tableData.id);
            if (item.length > 0) {
                item.unbind().replaceWith(this.getTableItemHtml(tableData));
                var item = $("#tableItem" + tableData.id);  //need to pick it up again to be able to bind to it
                item.click(self.createClickFunction(true, tableData));
            }
            console.log("table " + tableData.id + "  updated, seated = " + tableData.seated);
        }
    },

    handleTableRemoved:function (tableid) {
        console.debug("removing table " + tableid);
        this.removeTable(tableid);
        $("#tableItem" + tableid).remove();

    },
    removeTable:function (tableid) {
        for (var i = 0; i < this.lobbyData.length; i++) {
            var object = this.lobbyData[i];
            if (object.id == tableid) {
                this.lobbyData.splice(i, 1);
                return;
            }
        }
    },

    findTable:function (tableid) {
        for (var i = 0; i < this.lobbyData.length; i++) {
            var object = this.lobbyData[i];
            if (object.id == tableid) {
                return object;
            }
        }
        return null;
    },

    findTournament:function (tournamentId) {
        for (var i = 0; i < this.lobbyData.length; i++) {
            var object = this.lobbyData[i];
            if (object.id == tournamentId) {
                return object;
            }
        }
        return null;
    },

    findSitAndGo:function (tournamentId) {
        for (var i = 0; i < this.lobbyData.length; i++) {
            var object = this.lobbyData[i];
            if (object.id == tournamentId) {
                return object;
            }
        }
        return null;
    },

    reSort:function () {

    },
    clearLobby:function () {
        this.lobbyData = [];
        $("#tableListItemContainer").empty();
    },

    createGrid:function (tables) {
        $('#lobby').show();
        $("#tableListItemContainer").empty();

        var self = this;
        var count = 0;
        $.each(this.lobbyData, function (i, data) {
            if (self.includeData(data)) {
                count++;
                $("#tableListItemContainer").append(self.getTableItemHtml(data));
                $("#tableItem" + data.id).click(self.createClickFunction(tables, data));
            }
        });
        if (count == 0) {
            $("#tableListItemContainer").append($("<div/>").addClass("no-tables").html("Currently no tables matching your criteria"));
        }
        console.debug("grid created");
    },

    createClickFunction:function (tables, data) {
        var self = this;
        console.log("Creating click function. Tables?: " + tables);
        var click = function(e) {
            if (tables) {
                $("#tableListItemContainer").empty();
                comHandler.openTable(data.id, data.capacity,data.name);
            } else {
                console.log("On click. Register / unregister.");
                if (self.registeredTournaments[data.id]) {
                    console.log("Already registered to tournament " + data.id + ". Unregistering");
                    comHandler.unregisterFromTournament(data.id);
                } else {
                    comHandler.registerToTournament(data.id);
                }
            }
        }
        return click;
    },

    includeData:function (tableData) {
        for (var i = 0; i < this.filters.length; i++) {
            var filter = this.filters[i];
            if (filter.filter(tableData) == false) {
                return false;
            }
        }
        return true;
    },
    getTableItemHtml:function (t) {
        var item = Mustache.render(this.listItemTemplate, t);
        return item;
    },

    readParam:function (key, params) {

        for (var i = 0; i < params.length; i++) {
            var object = params[i];

            if (object.key == key) {
                //console.log("'"+object.key+"' val = " + object.value);
                //var valueArray = FIREBASE.ByteArray.fromBase64String(object);
                //console.log(object);

                var p = null;
                var valueArray = FIREBASE.ByteArray.fromBase64String(object.value);
                var byteArray = new FIREBASE.ByteArray(valueArray);
                if (object.type == 1) {
                    p = byteArray.readInt();
                } else {
                    p = byteArray.readString();
                }

                //shouldn't this work?
                //  var p =  FIREBASE.Styx.readParam(object);
                return p;
            }
        }
        return null;
    },

    getCapacity:function (id) {
        var tableData = this.findTable(id);
        return tableData.capacity;
    },

    showLogin:function () {
        $('#dialog1').fadeIn(1000);
    },

    notifyRegisteredToTournament:function(mttid) {
        this.registeredTournaments[mttid] = true;
    },

    notifyUnregisteredFromTournament:function(mttid) {
        this.registeredTournaments[mttid] = null;
    }

});

Poker.LobbyFilter = Class.extend({
    enabled:false,
    id:null,
    filterFunction:null,
    lobbyLayoutManager:null,
    init:function (id, enabled, filterFunction, lobbyLayoutManager) {
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
        // TODO: Need to know if this is tournaments.
        this.lobbyLayoutManager.createGrid(true);
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
    doFilter:function (enabled, lobbyData) {
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






"use strict";
var Poker = Poker || {};
Poker.LobbyLayoutManager = Class.extend({
    templateManager : null,
    filters:null,
    init : function() {
        this.templateManager = Poker.AppCtx.getTemplateManager();
        this.filters = [];
        $("#cashGameMenu").click(function (e) {
            $(".main-menu a.selected").removeClass("selected");
            $(this).addClass("selected");
            new Poker.LobbyRequestHandler().subscribeToCashGames();
        });
        $("#sitAndGoMenu").click(function (e) {
            $(".main-menu .selected").removeClass("selected");
            $(this).addClass("selected");
            new Poker.LobbyRequestHandler().subscribeToSitAndGos();
        });
        $("#tournamentMenu").click(function (e) {
            $(".main-menu .selected").removeClass("selected");
            $(this).addClass("selected");
            new Poker.LobbyRequestHandler().subscribeToTournaments();
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
    includeData:function (tableData) {
        for (var i = 0; i < this.filters.length; i++) {
            var filter = this.filters[i];
            if (filter.filter(tableData) == false) {
                return false;
            }
        }
        return true;
    },
    createTableList : function(tables) {
        var self = this;
        this.createLobbyList(tables,"tableListItemTemplate", this.getTableItemCallback());

    },
    createTournamentList : function(tournaments) {
        var self = this;
        this.createLobbyList(tournaments,"tableListItemTemplate", this.getTournamentItemCallback());
    },

    getTableItemCallback : function() {
        var self = this;
        var comHandler = Poker.AppCtx.getCommunicationManager();
        var func = function(listItem){
            comHandler.openTable(listItem.id, listItem.capacity, self.getTableDescription(listItem));
        }
        return func;
    },
    getTournamentItemCallback  : function() {
        var self = this;
        var comHandler = Poker.AppCtx.getCommunicationManager();
        var func = function(listItem){
            comHandler.openTournamentLobby(listItem.id,listItem.name);
        };
        return func;
    },
    getTableDescription : function(data) {
        return data.name  + " " + data.blinds + " " + data.type + " " + data.capacity;
    },
    tableRemoved : function(tableId) {
        console.log("remove = " + tableId);
        //$("#tableItem" + tableId).remove();
    },
    updateListItem : function(listItem, callbackFunction) {
        var self = this;
        var item = $("#tableItem" + listItem.id);
        if (item.length > 0) {
            item.unbind().replaceWith(this.getTableItemHtml("tableListItemTemplate",listItem));
            var item = $("#tableItem" + listItem.id);  //need to pick it up again to be able to bind to it
            item.click(function(){
                callbackFunction(listItem);
            });
        }
        console.log("update complete");
    },
    updateTableItem : function(listItem) {
        this.updateListItem(listItem,this.getTableItemCallback());
    },
    updateTournamentItem : function(listItem) {
        this.updateListItem(listItem,this.getTournamentItemCallback());
    },
    createLobbyList : function(listItems, listItemTemplateId, listItemCallback) {
        $('#lobby').show();
        $("#tableListItemContainer").empty();

        var self = this;
        var count = 0;
        $.each(listItems, function (i, item) {
            if(self.includeData(item)) {
                count++;
                var html = self.getTableItemHtml(listItemTemplateId,item);
                $("#tableListItemContainer").append(html);
                $("#tableItem" + item.id).click(function(){
                    listItemCallback(item);
                });
            }


        });
        if (count == 0) {
            $("#tableListItemContainer").append($("<div/>").addClass("no-tables").html("Currently no tables matching your criteria"));
        }
    },
    getTableItemHtml:function (templateId, data) {
        var listItemTemplate = this.templateManager.getTemplate(templateId);
        var item = Mustache.render(listItemTemplate, data);
        return item;
    }
});
"use strict";
var Poker = Poker || {};

Poker.LobbyLayoutManager = Class.extend({
    lobbyData : [],
    listItemTemplate : null,
    filters : [],
    init : function() {
        var templateManager = new Poker.TemplateManager();
        this.listItemTemplate = templateManager.getTemplate("tableListItemTemplate");
        var self = this;
        var fullTablesFilter = new Poker.LobbyFilter("fullTables",true,
            function(enabled,lobbyData){
                if(!enabled) {
                    return lobbyData.seated<lobbyData.capacity;
                } else {
                    return true;
                }
            },this);
        this.filters.push(fullTablesFilter);
        var emptyTablesFilter = new Poker.LobbyFilter("emptyTables",true,
            function(enabled,lobbyData){
                if(!enabled) {
                    return lobbyData.seated>0;
                } else {
                    return true;
                }

            },this);

        this.filters.push(emptyTablesFilter);

        var noLimitFilter = new Poker.LobbyFilter("noLimit",true,
            function(enabled,lobbyData){
                if(!enabled) {
                    return lobbyData.type != "NL";
                } else {
                    return true;
                }
            },this);
        this.filters.push(noLimitFilter);

        var noLimitFilter = new Poker.LobbyFilter("potLimit",true,
            function(enabled,lobbyData){
                if(!enabled) {
                    return lobbyData.type != "PL";
                } else {
                    return true;
                }
            },this);
        this.filters.push(noLimitFilter);

        var noLimitFilter = new Poker.LobbyFilter("fixedLimit",true,
            function(enabled,lobbyData){
                if(!enabled) {
                    return lobbyData.type != "FL";
                } else {
                    return true;
                }
            },this);
        this.filters.push(noLimitFilter);

    },
    handleTableSnapshotList: function(tableSnapshotList) {
        for (var i = 0; i < tableSnapshotList.length; i ++) {
            this.handleTableSnapshot(tableSnapshotList[i]);
        }
        this.createGrid();
    },
    handleTableSnapshot : function(tableSnapshot) {
        if (this.findTable(tableSnapshot.tableid) === null) {

            var speedParam = this.readParam("SPEED", tableSnapshot.params);
            //var variant = this.readParam("VARIANT", tableSnapshot.params);
            var bettingModel = this.readParam("BETTING_GAME_BETTING_MODEL",tableSnapshot.params);
            var ante = this.readParam("BETTING_GAME_ANTE",tableSnapshot.params);

            var data = {id:tableSnapshot.tableid,
                name:tableSnapshot.name,
                speed:speedParam,
                capacity: tableSnapshot.capacity,
                seated: tableSnapshot.seated,
                blinds : (Poker.Utils.formatBlinds(ante)+"/"+Poker.Utils.formatBlinds(ante*2)),
                type : this.getBettingModel(bettingModel),
                tableStatus : this.getTableStatus(tableSnapshot.seated,tableSnapshot.capacity)
            };
            var i = this.lobbyData.push(data);

        } else {
            console.debug("duplicate found - tableid: " + tableSnapshot.tableid);
        }
    },
    getTableStatus : function(seated,capacity) {
        if(seated == capacity) {
            return "full";
        }
        return "open";
    },
    getBettingModel : function(model) {
       if(model == "NO_LIMIT") {
           return "NL"
       } else if (model == "POT_LIMIT"){
           return "PL";
       } else if(model == "FIXED_LIMIT") {
           return "FL";
       }
       return model;
    },
    handleTableUpdateList : function(tableUpdateList) {
        for (var i = 0; i < tableUpdateList.length; i ++) {
            this.handleTableUpdate(tableUpdateList[i]);
        }

    },
    handleTableUpdate : function(tableUpdate) {
        var tableData = this.findTable(tableUpdate.tableid);
        if (tableData) {
            tableData.seated = tableUpdate.seated;
            //it might be filtered out
            var item = $("#tableItem"+tableData.id);
            if(item.length>0) {
                item.unbind().replaceWith(this.getTableItemHtml(tableData));
                item.click(function(e){
                    comHandler.openTable(tableData.id,tableData.capacity);
                });
            }
            console.log("table updated, seated = "+ tableUpdate.seated);
        }
    },
    handleTableRemoved : function(tableid) {
        console.debug("removing table " + tableid);
        this.removeTable(tableid);
        $("#tableItem"+tableid).remove();

    },
    removeTable : function(tableid) {
        for (var i = 0; i < this.lobbyData.length; i ++) {
            var object = this.lobbyData[i];
            if (object.id == tableid) {
                this.lobbyData.splice(i, 1);
                return;
            }
        }
    },

    findTable : function(tableid) {
        for (var i = 0; i < this.lobbyData.length; i ++) {
            var object = this.lobbyData[i];
            if (object.id == tableid) {
                return object;
            }
        }
        return null;
    },
    reSort : function() {

    },
    createGrid : function() {

        $('#lobby').show();
        $("#tableListItemContainer").empty();

        var self = this;

        $.each(this.lobbyData,function(i,data){
            if(self.includeData(data)) {
                $("#tableListItemContainer").append(self.getTableItemHtml(data));
                $("#tableItem"+data.id).click(function(e){
                    $("#tableListItemContainer").empty();
                    comHandler.openTable(data.id,data.capacity);
                });
            }
        });
        setTimeout(function(){
            $("#tableListContainerWrapper").niceScroll("#tableListContainer",
                {cursorcolor:"#555", scrollspeed:50, bouncescroll : false, cursorwidth : 8, cursorborder : "none"});
        },400);

        console.debug("grid created");
    },
    includeData : function(tableData) {
      for(var i = 0; i<this.filters.length; i++) {
          var filter = this.filters[i];
          if(filter.filter(tableData) == false) {
              return false;
          }
      }
      return true;
    },
    getTableItemHtml : function(t) {
        var item =  Mustache.render(this.listItemTemplate,t);
        return item;
    },
    readParam : function(key, params) {

        for (var i = 0; i < params.length; i ++) {
            var object = params[i];

            if (object.key == key) {
                //console.log("'"+object.key+"' val = " + object.value);
                //var valueArray = FIREBASE.ByteArray.fromBase64String(object);
                //console.log(object);

                var p = null;
                var valueArray =  FIREBASE.ByteArray.fromBase64String(object.value);
                var byteArray = new FIREBASE.ByteArray(valueArray);
                if ( object.type == 1 ) {
                    p =  byteArray.readInt();
                } else {
                    p =  byteArray.readString();
                }

                //shouldn't this work?
                //  var p =  FIREBASE.Styx.readParam(object);
                return p;
            }
        }
    },
    getCapacity : function(id) {
        var tableData = this.findTable(id);
        return tableData.capacity;
    },
    showLogin : function() {
        $('#dialog1').fadeIn(1000);

    }
});

Poker.LobbyFilter = Class.extend({
    enabled : false,
    id : null,
    filterFunction : null,
    lobbyLayoutManager : null,
    init : function(id,enabled,filterFunction,lobbyLayoutManager) {
        this.enabled = enabled;
        this.id = id;
        this.filterFunction = filterFunction;
        this.lobbyLayoutManager = lobbyLayoutManager;
        var self = this;

        $("#"+id).click(function(){
            self.enabled=!self.enabled;
            $(this).toggleClass("active");
            self.filterUpdated();
        });
        if(this.enabled==true) {
            $("#"+id).addClass("active");
        }
    },
    filterUpdated : function(){
        this.lobbyLayoutManager.createGrid();
    },
    /**
     * Returns true if it should be included in the lobby and
     * false if it shouldn't
     * @param lobbyData
     * @return {boolean} if it should be included
     */
    filter : function(lobbyData) {
        return this.filterFunction(this.enabled,lobbyData);
    }
});





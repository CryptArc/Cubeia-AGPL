"use strict";
var Poker = Poker || {};

Poker.LobbyLayoutManager = Class.extend({
    lobbyData : [],
    init : function() {
    },
    handleTableSnapshotList: function(tableSnapshotList) {
        for (var i = 0; i < tableSnapshotList.length; i ++) {
            this.handleTableSnapshot(tableSnapshotList[i]);
        }
        jQuery("#list4").trigger("reloadGrid");
    },
    handleTableSnapshot : function(tableSnapshot) {
        if (this.findTable(tableSnapshot.tableid) === null) {
            var speedParam = this.readParam("SPEED", tableSnapshot.params);
            var i = this.lobbyData.push({id:tableSnapshot.tableid, name:tableSnapshot.name, speed:speedParam, capacity:tableSnapshot.capacity,seated:tableSnapshot.seated});
            console.debug("tableid: " + tableSnapshot.tableid);
            jQuery("#list4").jqGrid('addRowData', tableSnapshot.tableid, this.lobbyData[i - 1]);
        } else {
            console.debug("duplicate found - tableid: " + tableSnapshot.tableid);
        }
    },
    handleTableUpdateList : function(tableUpdateList) {
        for (var i = 0; i < tableUpdateList.length; i ++) {
            this.handleTableUpdate(tableUpdateList[i]);
        }
        jQuery("#list4").trigger("reloadGrid");
    },
    handleTableUpdate : function(tableUpdate) {
        var tableData = this.findTable(tableUpdate.tableid);
        if (tableData) {
            tableData.seated = tableUpdate.seated;
            jQuery("#list4").jqGrid('setRowData', tableUpdate.tableid, {seated:tableData.seated});
        }
    },
    handleTableRemoved : function(tableid) {
        console.debug("removing table " + tableid);
        this.removeTable(tableid);
        jQuery("#list4").jqGrid('delRowData', tableid);
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
        var lastsort = jQuery("#list4").jqGrid('getGridParam', 'lastsort');
        if (lastsort == 3) {
            jQuery("#list4").jqGrid('sortGrid', 'seated', true);
        }
    },
    createGrid : function() {

        $('#lobby').fadeIn(1000);

        var self = this;
        jQuery("#list4").jqGrid({
            datatype: "local",
            data: self.lobbyData,
            height: 504,
            colNames:['Name', 'Speed', 'Capacity', 'Seated', ''],
            colModel:[
                {name:'name',index:'name', width:250, sorttype:"string"},
                {name:'speed',index:'speed', width:150, sorttype:"string"},
                {name:'capacity',index:'capacity', width:110, sorttype:"int"},
                {name:'seated',index:'seated', width:110, sorttype:"int"},
                {name:'act',index:'act', width:100}
            ],
            caption: "Cash Games",
            scroll: true,
            multiselect: false,
            gridComplete: function() {
                var ids = jQuery("#list4").jqGrid('getDataIDs');
                $.each(ids,function(i,cl){
                    var pb = $("<div/>").append($("<div/>").attr("id","open-"+cl).addClass("open-button").html("Open"));
                    jQuery("#list4").jqGrid('setRowData', cl, {act:pb.html()});
                    $("#open-"+cl).click(
                        function(){
                            comHandler.openTable(cl,self.getCapacity(cl));
                        });
                });


            },
            cellSelect: function() {
            }

        });
        console.debug("grid created");
    },
    readParam : function(key, params) {

        for (var i = 0; i < params.length; i ++) {


            var object = params[i];
            if (object.key == key) {
                var valueArray = FIREBASE.ByteArray.fromBase64String(object.value);
                return FIREBASE.Styx.readParam(valueArray);
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





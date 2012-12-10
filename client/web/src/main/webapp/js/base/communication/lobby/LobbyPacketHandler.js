"use strict";
var Poker = Poker || {};
Poker.LobbyPacketHandler = Class.extend({
    lobbyManager : null,
    init : function() {
        this.lobbyManager = Poker.AppCtx.getLobbyManager();
    },
    handleTableSnapshotList : function(snapshots) {
        this.lobbyManager.handleTableSnapshotList(snapshots);
    },
    handleTableUpdateList : function(updates) {
        this.lobbyManager.handleTableUpdateList(updates);
    },
    handleTableRemoved : function(tableId) {
        this.lobbyManager.handleTableRemoved(tableId);
    },
    handleTournamentSnapshotList : function(snapshots){
        if(snapshots.length>0 && snapshots[0].address.indexOf("/sitandgo")!=-1) {
            this.lobbyManager.handleSitAndGoSnapshotList(snapshots);
        } else {
            this.lobbyManager.handleTournamentSnapshotList(snapshots);
        }
    },
    handleTournamentUpdates : function(updates) {
        this.lobbyManager.handleTournamentUpdates(updates);
    }

});
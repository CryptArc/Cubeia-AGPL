"use strict";
var Poker = Poker || {};
Poker.ActionSender = Class.extend({
    init : function() {
    },
    sendGameTransportPacket :function(tableId,gamedata) {
        var connector = Poker.AppCtx.getConnector();
        connector.sendStyxGameData(0, tableId, gamedata);
    //   console.log("package sent to table " + tableId);
     //   console.log(gamedata);

    },
    sendAction : function(tableId,seq, actionType, betAmount, raiseAmount) {

        var performAction = new com.cubeia.games.poker.io.protocol.PerformAction();
        performAction.player = Poker.MyPlayer.id;
        performAction.action = new com.cubeia.games.poker.io.protocol.PlayerAction();
     //   console.log("sending action type=" + actionType);
        performAction.action.type = actionType;
        performAction.action.minAmount = 0;
        performAction.action.maxAmount = 0;
        performAction.betAmount = betAmount;
        performAction.raiseAmount = raiseAmount || 0;
        performAction.timeOut = 0;
        performAction.seq = seq;
     //   console.log("sending action table id = " + tableId);
        this.sendGameTransportPacket(tableId,performAction);
    }
});
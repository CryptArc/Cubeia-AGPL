var Poker = Poker || {};

Poker.AbstractConnectorHandler = Class.extend({
    connector : null,
    tableId : -1,
    init : function() {

    },
    sendGameTransportPacket :function(tableId,gamedata) {
        this.connector.sendStyxGameData(0, tableId, gamedata);
    },
    sendAction : function(tableId,seq, actionType, betAmount, raiseAmount) {
        var performAction = new com.cubeia.games.poker.io.protocol.PerformAction();
        performAction.player = Poker.MyPlayer.id;
        performAction.action = new com.cubeia.games.poker.io.protocol.PlayerAction();
        console.log("sending action type=" + actionType);
        performAction.action.type = actionType;
        performAction.action.minAmount = 0;
        performAction.action.maxAmount = 0;
        performAction.betAmount = betAmount;
        performAction.raiseAmount = raiseAmount || 0;
        performAction.timeOut = 0;
        performAction.seq = seq;

        this.sendGameTransportPacket(tableId,performAction);
    }
});
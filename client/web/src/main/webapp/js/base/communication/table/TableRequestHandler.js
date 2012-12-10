"use strict";
var Poker = Poker || {};

/**
 * Handles all table related requests
 *
 * Usage:
 *
 *  new Poker.TableRequestHandler(tableId).joinTable();
 *
 * @type {Poker.TableRequestHandler}
 */
Poker.TableRequestHandler = Class.extend({
    connector : null,
    pokerProtocolHandler : null,
    actionSender : null,
    tableId : null,
    init : function(tableId){
        if(typeof(tableId) == "undefined") {
            throw "Poker.TableRequestHandler Table id must be set"
        }
        this.tableId = tableId;
        this.connector = Poker.AppCtx.getConnector();
        this.actionSender = new Poker.ActionSender();
    },
    joinTable : function() {
        this.connector.joinTable(this.tableId, -1);
    },

    onMyPlayerAction : function (actionType, amount) {
        console.log("ON my player action");
        console.log(actionType);
        console.log(amount);
        if (actionType.id == Poker.ActionType.JOIN.id) {
           this.joinTable();
        } else if (actionType.id == Poker.ActionType.LEAVE.id) {
            if (this.isSeated) {
                this.leaveTable();
            } else {
                this.unwatchTable();
            }
        } else if (actionType.id == Poker.ActionType.SIT_IN.id) {
            this.sitIn();
        } else if (actionType.id == Poker.ActionType.SIT_OUT.id) {
            this.sitOut();
        } else {
            if (actionType.id == Poker.ActionType.RAISE.id) {
                this.sendAction(Poker.ProtocolUtils.getActionEnumType(actionType), amount, 0);
            } else {
                this.sendAction(Poker.ProtocolUtils.getActionEnumType(actionType), amount, 0);
            }
        }
    },
    leaveTable:function () {
        this.connector.leaveTable(this.tableId);
    },
    unwatchTable:function () {
        var comHandler = Poker.AppCtx.getCommunicationManager();
        var unwatchRequest = new FB_PROTOCOL.UnwatchRequestPacket();
        unwatchRequest.tableid = this.tableId;
        this.connector.sendProtocolObject(unwatchRequest);
        new Poker.LobbyRequestHandler().subscribeToCashGames();
    },
    sitOut:function () {
        var sitOut = new com.cubeia.games.poker.io.protocol.PlayerSitoutRequest();
        sitOut.player = Poker.MyPlayer.id;
        this.sendGameTransportPacket(sitOut);
    },
    sitIn:function () {
        var sitIn = new com.cubeia.games.poker.io.protocol.PlayerSitinRequest();
        sitIn.player = Poker.MyPlayer.id;
        this.sendGameTransportPacket(sitIn);
    },
    buyIn : function(amount) {
        var buyInRequest = new com.cubeia.games.poker.io.protocol.BuyInRequest();
        buyInRequest.amount = amount;

        buyInRequest.sitInIfSuccessful = true;
        this.sendGameTransportPacket(buyInRequest);
    },
    sendAction : function(actionType, betAmount, raiseAmount) {
      this.actionSender.sendAction(this.tableId,Poker.PokerSequence.getSequence(this.tableId),
          actionType, betAmount, raiseAmount);
    },
    sendGameTransportPacket : function(gameData) {
        this.actionSender.sendGameTransportPacket(this.tableId,gameData)
    }
});
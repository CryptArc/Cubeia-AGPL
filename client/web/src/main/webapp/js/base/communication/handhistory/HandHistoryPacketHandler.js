"use strict";
var Poker = Poker || {};
Poker.HandHistoryPacketHandler = Class.extend({
    /**
     * @type {Poker.HandHistoryManager}
     */
    handHistoryManager : null,
    init: function () {
        this.handHistoryManager = Poker.AppCtx.getHandHistoryManager();
    },
    /**
     * @param {Object} packet
     */
    handleServiceTransportPacket: function (packet) {
        var byteArray =  FIREBASE.ByteArray.fromBase64String(packet.servicedata);
        var message = utf8.fromByteArray(byteArray);

        var jsonData  = JSON.parse(message);
        console.log(jsonData);
        if(jsonData.packetType == "hand_ids") {
            this.handleHandIds(jsonData.tableId, jsonData.value);
        } else if(jsonData.packetType == "hands") {
            this.handleHands(jsonData.tableId,jsonData.value);
        } else if(jsonData.packetType == "hand") {
            this.handleHand(jsonData.value[0]);
        }

    },
    /**
     * @param {Object[]} handIds
     */
    handleHandIds : function(tableId,handIds){
        this.handHistoryManager.showHandIds(tableId,handIds);
    },
    handleHands : function(tableId,hands) {

    },
    handleHand : function(hand) {
        this.handHistoryManager.showHand(hand);
    }


});

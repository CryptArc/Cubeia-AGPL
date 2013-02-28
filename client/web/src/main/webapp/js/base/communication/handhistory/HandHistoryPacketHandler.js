"use strict";
var Poker = Poker || {};
Poker.HandHistoryPacketHandler = Class.extend({

    init: function () {

    },
    /**
     * @param {Object} packet
     */
    handleServiceTransportPacket: function (packet) {
        var byteArray =  FIREBASE.ByteArray.fromBase64String(packet.servicedata);
        var message = utf8.fromByteArray(byteArray);

        var jsonData  = JSON.parse(message);

        if(jsonData.packetType == "hand_ids") {
            this.handleHandIds(jsonData.value);
        } else if(jsonData.packetType == "hands") {
            this.handleHands(jsonData.value);
        } else if(jsonData.packetType == "hand") {
            this.handleHand(jsonData.packetType);
        }

    },
    /**
     * @param {Object[]} handIds
     */
    handleHandIds : function(handIds){

    },
    handleHands : function(hands) {

    },
    handleHand : function(hand) {

    }


});

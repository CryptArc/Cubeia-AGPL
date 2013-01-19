"use strict";
var Poker = Poker || {};
Poker.HandHistoryPacketHandler = Class.extend({

    init: function () {

    },
    handleServiceTransportPacket: function (packet) {
        var byteArray =  FIREBASE.ByteArray.fromBase64String(packet.servicedata);
        var message = utf8.fromByteArray(byteArray);
        alert("Hand history: " + message);
    }

});

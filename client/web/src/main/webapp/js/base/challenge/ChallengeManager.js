"use strict";
var Poker = Poker || {};

Poker.ChallengeManager = Class.extend({
    init : function() {

    },
    challengePlayer : function(playerId) {
        console.log("Challenge Player = " + playerId);
        var connector = Poker.AppCtx.getConnector();
        var pack = new FB_PROTOCOL.ServiceTransportPacket();
        pack.pid = Poker.MyPlayer.id;
        pack.seq = 0;
        pack.idtype = 0; // namespace
        pack.service = "com.cubeia.game.poker.challenge:challenge-service";
       // pack.service = "com.cubeia.game.poker.challenge.api.ChallengeService";
        var cr = new com.cubeia.games.challenge.io.protocol.ChallengeRequest();
        cr.playerId = playerId;
        pack.servicedata = FIREBASE.ByteArray.toBase64String(cr.save().createGameDataArray(cr.classId()));
        connector.sendProtocolObject(pack);
        console.log("pack sent");
        console.log(pack);
    },
    acceptChallenge : function(challengeId) {
        var connector = Poker.AppCtx.getConnector();
        var pack = new FB_PROTOCOL.ServiceTransportPacket();
        pack.pid = Poker.MyPlayer.id;
        pack.seq = 0;
        pack.idtype = 0; // namespace
        pack.service = "com.cubeia.game.poker.challenge:challenge-service";
        var cr = new com.cubeia.games.challenge.io.protocol.AcceptChallengeRequest();
        cr.challengeId = challengeId;
        pack.servicedata = FIREBASE.ByteArray.toBase64String(cr.save().createGameDataArray(cr.classId()));
        connector.sendProtocolObject(pack);
        console.log("pack sent");
        console.log(pack);
    },
    challengeReceived : function(challengeId,challengerScreenName) {
        console.log("challenge received from " + challengerScreenName);
        var notification = $.gritter.add({
            title: 'Challenge Received!',
            text: challengerScreenName+' has challenged you for a heads up! <a href="#" id="accept-'+challengeId+'">Accept</a> ',
            position:'top-right',
            time : 5000,
            class_name: 'gritter-light'
        });
        console.log(notification);
        var self = this;
        $("#accept-"+challengeId).click(function(){
            console.log("clicking accept")
            self.acceptChallenge(challengeId);

        });

    }
});

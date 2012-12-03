"use strict";
var Poker = Poker || {};

Poker.ChallengeManager = Class.extend({
    init : function() {

    },
    challengePlayer : function(playerId,configId) {
        console.log("Challenge Player = " + playerId);
        var connector = Poker.AppCtx.getConnector();
        var pack = this.getServicePacket();
       // pack.service = "com.cubeia.game.poker.challenge.api.ChallengeService";
        var cr = new com.cubeia.games.challenge.io.protocol.ChallengeRequest();
        cr.challengedPlayerId = playerId;
        cr.configurationId = configId;
        pack.servicedata = FIREBASE.ByteArray.toBase64String(cr.save().createGameDataArray(cr.classId()));
        connector.sendProtocolObject(pack);
        console.log("pack sent");
        console.log(pack);
    },
    getServicePacket : function() {
        var pack = new FB_PROTOCOL.ServiceTransportPacket();
        pack.pid = Poker.MyPlayer.id;
        pack.seq = 0;
        pack.idtype = 0; // namespace
        pack.service = "com.cubeia.game.poker.challenge:challenge-service";

        return pack;
    },
    requestChallengeConfig : function() {
        var pack = this.getServicePacket();
        var cr = new com.cubeia.games.challenge.io.protocol.ChallengeConfigurationsRequest();
        cr.playerId = Poker.MyPlayer.id;
        pack.servicedata = FIREBASE.ByteArray.toBase64String(cr.save().createGameDataArray(cr.classId()));
        connector.sendProtocolObject(pack);
    },
    handleChallengeConfiguration : function(configurations) {

    },
    declineChallenge : function(challengeId) {
        console.log("DECLINING C " + challengeId);
        var connector = Poker.AppCtx.getConnector();
        var pack = this.getServicePacket();
        var cr = new com.cubeia.games.challenge.io.protocol.DeclineChallengeRequest();
        cr.challengeId = challengeId;
        pack.servicedata = FIREBASE.ByteArray.toBase64String(cr.save().createGameDataArray(cr.classId()));
        connector.sendProtocolObject(pack);
        console.log("decline pack sent");
        console.log(pack);
    },
    acceptChallenge : function(challengeId) {
        var connector = Poker.AppCtx.getConnector();
        var pack = this.getServicePacket();
        var cr = new com.cubeia.games.challenge.io.protocol.AcceptChallengeRequest();
        cr.challengeId = challengeId;
        pack.servicedata = FIREBASE.ByteArray.toBase64String(cr.save().createGameDataArray(cr.classId()));
        connector.sendProtocolObject(pack);
        console.log("pack sent");
        console.log(pack);
    },
    challengeReceived : function(challengeId,challengerScreenName,config) {
        console.log("challenge received from " + challengerScreenName);
        var self = this;
        var notification = new Poker.Notification("Challenge Received!",
            challengerScreenName + ' has challenged you in a '+config.challengeName+' heads up Sit & Go!');
        notification.addAction("Accept",function(){
            self.acceptChallenge(challengeId);
        });
        notification.addAction("Decline",function(){
            self.declineChallenge(challengeId);
        });
        Poker.AppCtx.getNotificationsManager().notify(notification);

    },
    challengeResponse : function(challengeId, status) {
        console.log("Challenge response status");
        console.log(status);
        var notification = null;
        if(status == com.cubeia.games.challenge.io.protocol.ChallengeRequestStatusEnum.WAITING_FOR_ACCEPT) {
             notification = new Poker.Notification("Challenge sent","Waiting for player to respond");
        } else if(status == com.cubeia.games.challenge.io.protocol.ChallengeRequestStatusEnum.PLAYER_DECLINED) {
             notification = new Poker.Notification("Challenge declined","Your challenge was declined :(");
        } else if(status == com.cubeia.games.challenge.io.protocol.ChallengeRequestStatusEnum.PLAYER_NOT_FOUND) {
          console.log("Unhandled challenge response PLAYER_NOT_FOUND");
        }
        if(notification!=null) {
            Poker.AppCtx.getNotificationsManager().notify(notification);
        }

    }
});

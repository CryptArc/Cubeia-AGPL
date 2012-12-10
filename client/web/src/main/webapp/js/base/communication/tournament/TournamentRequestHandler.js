"use strict";
var Poker = Poker || {};
Poker.TournamentRequestHandler = Class.extend({
    connector : null,
    tournamentId : null,
    init : function(tournamentId) {
        this.tournamentId = tournamentId;
        this.connector = Poker.AppCtx.getConnector();
    },
    registerToTournament : function(){
        console.log("TournamentRequestHandler.registerToTournament");
        var registrationRequest = new FB_PROTOCOL.MttRegisterRequestPacket();
        registrationRequest.mttid = this.tournamentId;
        this.connector.sendProtocolObject(registrationRequest);
    },
    unregisterFromTournament : function(){
        console.log("TournamentRequestHandler.registerToTournament");
        var unregistrationRequest = new FB_PROTOCOL.MttUnregisterRequestPacket();
        unregistrationRequest.mttid = this.tournamentId;
        this.connector.sendProtocolObject(unregistrationRequest);
    },
    requestTournamentInfo : function() {
        var mtt = this.createMttPacket();
        var playerListRequest = new com.cubeia.games.poker.io.protocol.RequestTournamentLobbyData();
        var byteArray = new FIREBASE.ByteArray();//not using  playerListRequest.save() cos of a styx bug
        var data = FIREBASE.ByteArray.toBase64String(byteArray.createGameDataArray(playerListRequest.classId()));
        mtt.mttdata  = data;
        this.connector.sendProtocolObject(mtt);
       // console.log("sending mtt protocol obj");
       // console.log(mtt);

    },
    createMttPacket : function() {
        var mtt = new FB_PROTOCOL.MttTransportPacket();
        mtt.mttid = this.tournamentId;
        mtt.pid = Poker.MyPlayer.id;
        return mtt;
    },
    leaveTournamentLobby : function() {
        Poker.AppCtx.getTournamentManager().removeTournament(this.tournamentId);
        Poker.AppCtx.getViewManager().removeTournamentView(this.tournamentId);
    }
});
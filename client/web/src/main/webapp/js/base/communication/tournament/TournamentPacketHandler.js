"use strict";
var Poker = Poker || {};
Poker.TournamentPacketHandler = Class.extend({
    tournamentManager : null,
    init : function() {
        this.tournamentManager = Poker.AppCtx.getTournamentManager();
    },
    handleTournamentTransport : function(packet) {
        console.log("Got tournament transport");

        var valueArray =  FIREBASE.ByteArray.fromBase64String(packet.mttdata);
        var gameData = new FIREBASE.ByteArray(valueArray);
        var length = gameData.readInt(); // drugs.
        var classId = gameData.readUnsignedByte();
        var tournamentPacket = com.cubeia.games.poker.io.protocol.ProtocolObjectFactory.create(classId, gameData);

        var tournamentManager = Poker.AppCtx.getTournamentManager();
        switch (tournamentPacket.classId()) {
            case com.cubeia.games.poker.io.protocol.TournamentOut.CLASSID:
                this.handleTournamentOut(tournamentPacket);
                break;
            case com.cubeia.games.poker.io.protocol.TournamentLobbyData.CLASSID:
                tournamentManager.handleTournamentLobbyData(packet.mttid, tournamentPacket);
                break;
            default:
                console.log("Unhandled tournament packet");
        }
    },
    handleTournamentOut: function (packet) {
        var dialogManager = Poker.AppCtx.getDialogManager();
        if (packet.position == 1) {
            dialogManager.displayGenericDialog({header:"Message", message:"Congratulations, you won the tournament!"});
        } else {
            dialogManager.displayGenericDialog({header:"Message", message:"You finished " + packet.position + " in the tournament."});
        }
    },
    handleRemovedFromTournamentTable:function (packet) {
        console.log("Removed from table " + packet.tableid + " in tournament " + packet.mttid + " keep watching? " + packet.keepWatching);
    },
    handleSeatedAtTournamentTable:function (seated) {
        console.log("I was seated in a tournament, opening table");
        console.log(seated);
        new Poker.TableRequestHandler(seated.tableid).joinTable();
    },
    handleRegistrationResponse : function (registrationResponse) {
        console.log("Registration response:");
        console.log(registrationResponse);

        if (registrationResponse.status == "OK") {
            this.tournamentManager.handleRegistrationSuccessful(registrationResponse.mttid);
        } else {
            this.tournamentManager.handleRegistrationFailure(registrationResponse.mttid);
        }
    },
    handleUnregistrationResponse : function (unregistrationResponse) {
        console.log("Unregistration response:");
        console.log(unregistrationResponse);
        if (unregistrationResponse.status == "OK") {
            this.tournamentManager.handleUnregistrationSuccessful(unregistrationResponse.mttid);
        } else {
            this.tournamentManager.handleUnregistrationFailure(unregistrationResponse.mttid)

        }
    },
    handleNotifyRegistered : function(packet) {
        this.tournamentManager.openTournamentLobbies(packet.tournaments);

    }
});
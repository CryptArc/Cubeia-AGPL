"use strict";
var Poker = Poker || {};
/**
 * Handles tournament lobby related data for the
 * tournaments the user is currently watching
 * @type {Poker.TournamentManager}
 */
Poker.TournamentManager = Class.extend({
    tournaments : null,
    registeredTournaments : null,
    dialogManager : null,
    tournamentUpdater : null,
    init : function(tournamentLobbyUpdateInterval) {
        this.tournaments = new Poker.Map();
        this.registeredTournaments = new Poker.Map();
        this.dialogManager = Poker.AppCtx.getDialogManager();
        var self = this;
        this.tournamentUpdater = new Poker.PeriodicalUpdater(function(){
            self.updateTournamentData();
        },tournamentLobbyUpdateInterval);
    },
    createTournament : function(id,name) {
        var viewManager = Poker.AppCtx.getViewManager();
        if(this.getTournamentById(id)!=null) {
            viewManager.activateViewByTournamentId(id);
        } else {
            var viewContainer = $(".view-container");

            var layoutManager = new Poker.TournamentLayoutManager(id,name, this.isRegisteredForTournament(id),
                viewContainer,function(){
                        this.removeTournament(id);
                    }
            );
            viewManager.addTournamentView(layoutManager.getViewElementId(),name, layoutManager);

            this.tournaments.put(id,new Poker.Tournament(id,name,layoutManager));
            new Poker.TournamentRequestHandler(id).requestTournamentInfo();
            this.activateTournamentUpdates(id);
            this.tournamentUpdater.start();
        }
    },
    removeTournament : function(tournamentId) {
        var tournament = this.tournaments.remove(tournamentId);
        if(tournament!=null) {
            if(this.tournaments.size()==0) {
                this.tournamentUpdater.stop();
            }
        }
    },
    getTournamentById : function(id) {
        return this.tournaments.get(id);
    },
    handleTournamentLobbyData : function(tournamentId, tournamentData) {
        console.log("tournament lobby data received");
        console.log(tournamentData);
        var tournament = this.getTournamentById(tournamentId);
        this.handlePlayerList(tournament,tournamentData.players);
        this.handleBlindsStructure(tournament,tournamentData.blindsStructure);
        this.handlePayoutInfo(tournament,tournamentData.payoutInfo);
        this.handleTournamentStatistics(tournament,tournamentData.tournamentStatistics);
        this.handleTournamentInfo(tournament,tournamentData.tournamentInfo);
    },
    handlePlayerList : function(tournament,playerList) {
        var players = [];
        if(playerList) {
           players = playerList.players;
        }
        tournament.tournamentLayoutManager.updatePlayerList(players);
    },
    handleBlindsStructure : function(tournament,blindsStructure) {
        tournament.tournamentLayoutManager.updateBlindsStructure(blindsStructure);
    },
    handlePayoutInfo : function(tournament, payoutInfo) {
        tournament.tournamentLayoutManager.updatePayoutInfo(payoutInfo);
    },
    handleTournamentStatistics : function(tournament,statistics) {
        tournament.tournamentLayoutManager.updateTournamentStatistics(statistics);
    },
    handleRegistrationSuccessful : function(tournamentId) {
        this.registeredTournaments.put(tournamentId,true);
        var tournament = this.tournaments.get(tournamentId);
        if(tournament!=null) {
            tournament.tournamentLayoutManager.setPlayerRegisteredState();
        }
        this.dialogManager.displayGenericDialog({header:"Message", message:"You successfully registered to tournament " + tournamentId});

    },
    handleTournamentInfo : function(tournament,info){
        tournament.tournamentLayoutManager.updateTournamentInfo(info);
    },
    handleRegistrationFailure : function(tournamentId) {
        this.dialogManager.displayGenericDialog({header:"Message",
            message:"Your registration attempt to tournament " + tournamentId + " was denied."});
    },
    handleUnregistrationSuccessful : function(tournamentId) {
        this.registeredTournaments.remove(tournamentId);
        var tournament = this.tournaments.get(tournamentId);
        if(tournament!=null) {
            tournament.tournamentLayoutManager.setPlayerUnregisteredState();
        }
        this.dialogManager.displayGenericDialog({header:"Message",
            message:"You successfully unregistered from tournament " + tournamentId});
    },
    handleUnregistrationFailure : function(tournamentId) {
        this.dialogManager.displayGenericDialog({header:"Message",
            message:"Your unregistration attempt from tournament " + tournamentId + " was denied."});
    },
    isRegisteredForTournament : function(tournamentId) {
        return this.registeredTournaments.get(tournamentId)!=null ? true : false;
    },
    activateTournamentUpdates : function(tournamentId) {
        var tournament = this.tournaments.get(tournamentId);
        if(tournament!=null) {
            tournament.updating = true;
        }
        this.tournamentUpdater.rushUpdate();
    },
    deactivateTournamentUpdates : function(tournamentId) {
        var tournament = this.tournaments.get(tournamentId);
        if(tournament!=null) {
            tournament.updating = false;
        }
    },
    updateTournamentData : function() {
        var tournaments =  this.tournaments.values();
        for(var i = 0; i<tournaments.length; i++) {
            if(tournaments[i].updating==true) {
                console.log("found updating tournament retrieving tournament data");
                new Poker.TournamentRequestHandler(tournaments[i].id).requestTournamentInfo();
            }
        }
    },
    openTournamentLobbies : function(tournamentIds) {
        //TODO: the name of the tournament needs to be fetched from somewhere!
        for(var i = 0; i<tournamentIds.length; i++) {
            this.registeredTournaments.put(tournamentIds[i],true);
            this.createTournament(tournamentIds[i],"Tourney");
        }
    }
});
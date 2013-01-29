"use strict";
var Poker = Poker || {};

/**
 *
 * @type {Poker.TournamentManager}
 */
Poker.TournamentLayoutManager = Class.extend({
    viewContainer : null,
    tournamentId :-1,

    /**
     * @type Poker.TemplateManager
     */
    templateManager : null,

    viewElement : null,
    playerListBody : null,
    registerButton : null,
    unregisterButton : null,
    loadingButton : null,
    leaveButton : null,
    leaveFunction : null,
    takeSeatButton : null,
    name : null,

    init : function(tournamentId, name, registered, viewContainer,leaveFunction) {
        this.leaveFunction = leaveFunction;
        this.tournamentId = tournamentId;
        this.viewContainer = viewContainer;
        this.name = name;
        this.templateManager = Poker.AppCtx.getTemplateManager();
        var template = this.templateManager.getTemplate("tournamentTemplate");
        var viewHTML = Mustache.render(template,{tournamentId : tournamentId, name : name});
        viewContainer.append(viewHTML);
        this.viewElement = $("#tournamentView"+tournamentId);
        this.playerListBody = this.viewElement.find(".player-list tbody");
        this.initActions();
        if(registered==true) {
            this.setPlayerRegisteredState();
        }
    },
    updatePlayerList : function(players) {
        var template = this.templateManager.getRenderTemplate("tournamentPlayerListItem");
        this.playerListBody.empty();
        var self = this;
        $.each(players,function(i,p) {
            self.playerListBody.append(template.render(p));
        });
        if(players.length==0) {
            this.playerListBody.append("<td/>").attr("colspan","3").append("No players registered");
        }
    },
    updateBlindsStructure : function(blindsStructure) {
        var blindsTemplate = this.templateManager.getRenderTemplate("tournamentBlindsStructureTemplate");
        this.viewElement.find(".blinds-structure").html(blindsTemplate.render(blindsStructure));

    },
    updateTournamentInfo : function(info) {
        console.log("Tournament info");
        console.log(info);
        if(info.maxPlayers == info.minPlayers) {
            $.extend(info,{sitAndGo : true});
        }
        var infoTemplate = this.templateManager.getRenderTemplate("tournamentInfoTemplate");
        this.viewElement.find(".tournament-info").html(infoTemplate.render(info));
        this.viewElement.find(".tournament-start-date").html(info.startTime);

    },
    updateTournamentStatistics : function(statistics) {
        var statsTemplate = this.templateManager.getRenderTemplate("tournamentStatsTemplate");
        this.viewElement.find(".tournament-stats").show().html(statsTemplate.render(statistics));
    },
    hideTournamentStatistics : function()  {
        this.viewElement.find(".tournament-stats").hide();
    },
    updatePayoutInfo : function(payoutInfo) {
        var payoutTemplate = this.templateManager.getRenderTemplate("tournamentPayoutStructureTemplate");
        this.viewElement.find(".payout-structure").html(payoutTemplate.render(payoutInfo));
    },
    initActions : function() {
        this.leaveButton = this.viewElement.find(".leave-action");
        this.registerButton = this.viewElement.find(".register-action");
        this.unregisterButton = this.viewElement.find(".unregister-action");
        this.loadingButton =  this.viewElement.find(".loading-action").hide();
        this.takeSeatButton =  this.viewElement.find(".take-seat-action").hide();
        var tournamentRequestHandler = new Poker.TournamentRequestHandler(this.tournamentId);
        var self = this;
        this.leaveButton.click(function(e){
            self.leaveLobby();
        });
        this.registerButton.click(function(e){
            tournamentRequestHandler.requestBuyInInfo();
        });
        this.unregisterButton.hide().click(function(e){
            $(this).hide();
            self.loadingButton.show();
            tournamentRequestHandler.unregisterFromTournament();

        });
        this.takeSeatButton.click(function(e){
            tournamentRequestHandler.takeSeat();
        });
    },

    onFailedRegistration : function() {
        this.setPlayerUnregisteredState();
    },
    onFailedUnregistraion : function() {
        this.setPlayerRegisteredState();
    },
    setTournamentNotRegisteringState : function(registered){
        if(registered) {
            this.takeSeatButton.show();
        } else {
            this.takeSeatButton.hide();
        }
        this.loadingButton.hide();
        this.registerButton.hide();
        this.unregisterButton.hide();
    },
    setPlayerRegisteredState : function() {
        this.loadingButton.hide();
        this.registerButton.hide();
        this.unregisterButton.show();
    },
    setPlayerUnregisteredState : function() {
        this.loadingButton.hide();
        this.registerButton.show();
        this.unregisterButton.hide();
    },
    getViewElementId : function() {
        return this.viewElement.attr("id");
    },
    leaveLobby : function() {
        new Poker.TournamentRequestHandler(this.tournamentId).leaveTournamentLobby();
    },
    showBuyInInfo : function(buyIn, fee, currency, balanceInWallet) {
        var buyInDialog = new Poker.TournamentBuyInDialog();
        buyInDialog.show(this.tournamentId,this.name,buyIn,fee,balanceInWallet);
    }

});

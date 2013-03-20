"use strict";
var Poker = Poker || {};

Poker.TournamentBuyInDialog = Poker.BuyInDialog.extend({
    init : function() {
        this._super();
    },
    show : function(tournamentId,name,buyIn,fee,balance) {
        var data = {
            tournamentId : tournamentId,
            buyIn : buyIn,
            fee : fee,
            balance : balance,
            name : name
        };
        var tournament = Poker.AppCtx.getTournamentManager().getTournamentById(tournamentId);
        var viewContainer = tournament.tournamentLayoutManager.viewContainer;
        this.render(data, viewContainer ,function(){
            new Poker.TournamentRequestHandler(tournamentId).registerToTournament();
            return true;
        });
    },
    getTemplateId : function() {
        return "tournamentBuyInContent";
    }

});
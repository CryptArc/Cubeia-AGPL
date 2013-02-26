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
        this.render(data, function(){
            new Poker.TournamentRequestHandler(tournamentId).registerToTournament();
            return true;
        });
    },
    getTemplateId : function() {
        return "tournamentBuyInContent";
    }

});
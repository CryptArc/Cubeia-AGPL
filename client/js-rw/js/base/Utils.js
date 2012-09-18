"use strict";
var Poker = Poker || {};

Poker.Utils = {
    formatCurrency : function(amount) {
        return parseFloat(amount/100).toFixed(2);
    },
    getCardString : function(gamecard) {
        var ranks = "23456789tjqka ";
        var suits = "cdhs ";
        var cardString = ranks.charAt(gamecard.rank) + suits.charAt(gamecard.suit);
        return cardString;
    }
};
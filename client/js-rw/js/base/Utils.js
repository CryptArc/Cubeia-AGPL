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
    },
    formatBlinds : function(amount) {
        var str = ""+ Poker.Utils.formatCurrency(amount);
        if(str.charAt(str.length-1)=="0") {
            str = str.substr(0,str.length-1);
            if(str.charAt(str.length-1)=="0"){
                str = str.substr(0,str.length-2);
            }
        }
        return str;
    }
};
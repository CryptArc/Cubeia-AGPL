"use strict";
var Poker = Poker || {};

/**
 * Global container for the logged in user
 * keeps track of the player id name and the
 * last betAmount
 * @type {Object}
 */
Poker.MyPlayer = {
    id : -1,
    name : null,
    betAmount : 0,
    onLogin : function(playerId, name) {
        Poker.MyPlayer.id = playerId;
        Poker.MyPlayer.name = name;
    },
    clear : function() {
        Poker.MyPlayer.id = -1;
        Poker.MyPlayer.name = "";
    }
};
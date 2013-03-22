"use strict";
var Poker = Poker || {};

/**
 * Global container for the logged in user
 * keeps track of the player id name and the
 * last betAmount
 * @type {Poker.MyPlayer}
 */
Poker.MyPlayer = {
    /**
     * @type Number
     */
    id : -1,
    /**
     * @type String
     */
    name : null,

    /**
     * @type String
     */
    password : null,

    /**
     * @type String
     */

    sessionToken : null,

    /**
     * type Number
     */
    betAmount : 0,

    loginToken : null,



    onLogin : function(playerId, name, credentials) {
        console.log("credentials at MyPlayer: ", credentials);
        if (!credentials) credentials = "No Token Here..."
        Poker.MyPlayer.sessionToken = credentials;
        Poker.MyPlayer.id = playerId;
        Poker.MyPlayer.name = name;
    },
    clear : function() {
        Poker.MyPlayer.id = -1;
        Poker.MyPlayer.name = "";
    }
};
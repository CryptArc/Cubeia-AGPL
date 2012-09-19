"use strict";
var Poker = Poker || {};

Poker.Table = Class.extend({
    capacity : 0,
    id : -1,
    players : [],
    myPlayerSeat : null,
    init : function(id,capacity) {
        this.id = id;
        this.capacity = capacity;
    },
    /**
     *
     * @param position at the table
     * @param player to add to the table
     */
    addPlayer : function(seat,player) {
        if(seat<0 || seat>=this.capacity) {
            throw "Table : seat " + seat + " of player "+ player.name+" is invalid, capacity="+this.capacity;
        }
        this.players[seat] = player;
    },
    /**
     * Get player at a specific position
     * @param seat of the player
     * @return {Poker.Player} the player at the seat
     */
    getPlayerAtPosition : function(seat) {
        return this.players[seat];
    },
    /**
     * Get a player by its player id
     * @param playerId to get
     * @return {Poker.Player} with the playerId or null if not found
     */
    getPlayerById : function(playerId) {
        for(var p in this.players) {
            if(this.players[p].id == playerId) {
                return this.players[p];
            }
        }

        return null;
    },
    /**
     * Returns the number of players at the table;
     * @return {int}
     */
    getNrOfPlayers : function() {
        return this.players.length;

    }
});
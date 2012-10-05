"use strict";
var Poker = Poker || {};

Poker.Table = Class.extend({
    capacity : 0,
    id : -1,
    players : null,
    myPlayerSeat : null,
    name : null,
    init : function(id,capacity,name) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.players = new Poker.Map();
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
        this.players.put(seat,player);

    },
    removePlayer : function(playerId) {
       var kvp = this.players.keyValuePairs();
       for(var i = 0; i<kvp.length; i++) {
           if(kvp[i].value.id == playerId) {
               this.players.remove(kvp[i].key);
               return;
           }
       }
       console.log("player not found when trying to remove");
    },
    /**
     * Get player at a specific position
     * @param seat of the player
     * @return {Poker.Player} the player at the seat
     */
    getPlayerAtPosition : function(seat) {

        return this.players.get(seat);
    },
    /**
     * Get a player by its player id
     * @param playerId to get
     * @return {Poker.Player} with the playerId or null if not found
     */
    getPlayerById : function(playerId) {
        var players = this.players.values();
        for(var i = 0; i<players.length; i++) {
            if(players[i].id == playerId) {
                return players[i];
            }
        }
        return null;
    },
    /**
     * Returns the number of players at the table;
     * @return {int}
     */
    getNrOfPlayers : function() {
        return this.players.size();

    }
});
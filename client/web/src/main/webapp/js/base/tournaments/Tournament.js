"use strict";
var Poker = Poker || {};
Poker.Tournament = Class.extend({
    id : -1,
    name : null,
    tournamentLayoutManager : null,
    updating : false,
    init : function(id,name,tournamentLayoutManager) {
        this.id = id;
        this.name = name;
        this.tournamentLayoutManager = tournamentLayoutManager;
    }
});
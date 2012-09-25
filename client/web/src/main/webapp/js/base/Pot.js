"use strict";
var Poker = Poker || {};

Poker.PotType = {
    MAIN : 1,
    SIDE : 2
};

Poker.Pot = Class.extend({
    id : -1,
    type : null,
    amount : 0,
    init : function(id,type,amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;

    }
});
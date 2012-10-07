"use strict";
var Poker = Poker || {};

Poker.Action = Class.extend({
    type : null,
    minAmount : 0,
    maxAmount : 0,
    init : function(type,minAmount,maxAmount){
        this.type = type;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }
});

Poker.ActionType = {
    CALL : {
        text : "Call",
        id : "action-call"
    },
    CHECK : {
        text : "Check",
        id : "action-check"
    },
    FOLD : {
        text : "Fold",
        id : "action-fold"
    },
    BET : {
        text : "Bet",
        id : "action-bet"
    },
    RAISE : {
        text : "Raise",
        id : "action-raise"
    },
    SMALL_BLIND : {
        text : "Small Blind",
        id : "action-small-blind"
    },
    BIG_BLIND : {
        text : "Big Blind",
        id : "action-big-blind"
    },
    JOIN :{
        text : "Join",
        id : "action-join"
    },
    LEAVE : {
        text : "leave",
        id : "action-leave"
    },
    SIT_OUT : {
        text : "Sit-out",
        id : "action-sit-out"
    },
    SIT_IN : {
        text : "Sit-in",
        id : "action-sit-in"
    }

};
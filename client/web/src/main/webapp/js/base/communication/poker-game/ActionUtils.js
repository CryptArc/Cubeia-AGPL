"use strict";
var Poker = Poker || {};


Poker.ActionUtils = Class.extend({
    init : function() {
    },
    getActionType : function(actType){
        var type = null;
        switch (actType) {
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.CHECK:
                type = Poker.ActionType.CHECK;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.CALL:
                type = Poker.ActionType.CALL;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.BET:
                type = Poker.ActionType.BET;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.RAISE:
                type = Poker.ActionType.RAISE;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.FOLD:
                type = Poker.ActionType.FOLD;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.SMALL_BLIND:
                type = Poker.ActionType.SMALL_BLIND;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.BIG_BLIND:
                type = Poker.ActionType.BIG_BLIND;
                break;
            default:
                console.log("Unhandled ActionTypeEnum " + actType);
                break;
        }
        return type;
    },
    getAction : function(act) {
        var type = this.getActionType(act.type);
        return new Poker.Action(type,act.minAmount, act.maxAmount);
    },
    getPokerActions : function(allowedActions){
        var actions = [];
        for(var a in allowedActions) {
            var ac = this.getAction(allowedActions[a]);
            if(ac!=null) {
                actions.push(ac);
            }
        }
        return actions;
    }

});
Poker.ActionUtils = new Poker.ActionUtils();

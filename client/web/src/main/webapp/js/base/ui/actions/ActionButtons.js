"use strict";
var Poker = Poker || {};

/**
 * @type {Poker.ActionButtons}
 * @extends Poker.AbstractTableButtons
 */
Poker.ActionButtons = Poker.AbstractTableButtons.extend({

    doBetActionButton : null,
    doRaiseActionButton : null,
    cancelBetActionButton : null,
    fixedBetActionButton : null,
    fixedRaiseActionButton : null,

    init : function(view,actionCallback, raiseCallback, betCallback, amountCallback) {
        this._super(view,actionCallback);

        this._addActionButton($(".action-bet",view),Poker.ActionType.BET,betCallback ,false);
        this._addActionButton($(".action-raise",view),Poker.ActionType.RAISE,raiseCallback,false);

        this._addActionButton($(".action-check", view), Poker.ActionType.CHECK, actionCallback, false);
        this._addActionButton($(".action-fold", view), Poker.ActionType.FOLD, actionCallback, false);
        this._addActionButton($(".action-call", view), Poker.ActionType.CALL, actionCallback, true);
        this._addActionButton($(".action-big-blind", view), Poker.ActionType.BIG_BLIND, actionCallback, true);
        this._addActionButton($(".action-small-blind", view), Poker.ActionType.SMALL_BLIND, actionCallback, true);


        //we can't put it in actionButtons since it's a duplicate action
        this.doBetActionButton = new Poker.BetAmountButton($(".do-action-bet",view),
            Poker.ActionType.BET,actionCallback,true,amountCallback);
        this.doRaiseActionButton = new Poker.BetAmountButton($(".do-action-raise",view),
            Poker.ActionType.RAISE,actionCallback,true,amountCallback);
        this.cancelBetActionButton = new Poker.ActionButton($(".action-cancel-bet",view),null,function(){
            self.onClickCancelButton();
        },false);

        this.fixedBetActionButton = new Poker.ActionButton($(".fixed-action-bet",view),Poker.ActionType.BET,actionCallback,true);
        this.fixedRaiseActionButton = new Poker.ActionButton($(".fixed-action-raise",view),Poker.ActionType.RAISE,actionCallback,true);

    },
    _addActionButton : function(elId, actionType, callback, showAmount){
        var button = null;
        if(actionType.id == Poker.ActionType.BET.id || actionType.id == Poker.ActionType.RAISE.id ) {
            button = new Poker.BetSliderButton(elId,actionType,callback,showAmount);
        } else {
            button = new Poker.ActionButton(elId, actionType, callback, showAmount);
        }
        this.buttons.put(actionType.id, button);
    },
    hideAll : function() {
        var buttons = this.buttons.values();
        for(var a in buttons) {
            buttons[a].el.hide();
        }
        this.cancelBetActionButton.hide();
        this.doBetActionButton.hide();
        this.doRaiseActionButton.hide();
        this.fixedBetActionButton.hide();
        this.fixedRaiseActionButton.hide();
    },

    showButtons : function(actions, mainPot, fixedLimit) {
        for (var a in actions){
            var act = actions[a];
            console.log("Action:");
            console.log(act);
            if(fixedLimit==true && act.type.id == Poker.ActionType.BET.id) {
                if(act.minAmount>0) {
                    this.fixedBetActionButton.setAmount(act.minAmount);
                }
                this.fixedBetActionButton.show();
            } else if(fixedLimit==true && act.type.id == Poker.ActionType.RAISE.id) {
                if(act.minAmount>0) {
                    this.fixedRaiseActionButton.setAmount(act.minAmount,act.maxAmount,mainPot);
                }
                this.fixedRaiseActionButton.show();
            } else {
                if(act.minAmount>0) {
                    this.getButton(act.type).setAmount(act.minAmount,act.maxAmount,mainPot);
                }
                this.show(act.type);
            }
        }
    }
});
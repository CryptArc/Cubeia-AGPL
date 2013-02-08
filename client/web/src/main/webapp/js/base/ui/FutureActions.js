"use strict";
var Poker = Poker || {};

Poker.FutureActions = Class.extend({

    selectedFutureActionType : null,
    currentCallAmount : 0,
    currentRaiseAmount : 0,
    currentActions : null,
    container : null,
    init : function(container) {
        var self = this;
        this.container = container;

        $.each(this.getFutureActionTypes(),function(i,actionType){
            container.find("."+actionType.id + " input").on("change",function(e) {

                if(self.selectedFutureActionType!=null) {
                    if(self.selectedFutureActionType.id === actionType.id) {
                        self.setSelectedFutureAction(null);
                    } else {
                        container.find("."+self.selectedFutureActionType.id + " input").attr("checked",false);
                    }
                }
                if($(this).is(":checked")) {
                    self.setSelectedFutureAction(actionType);
                }

            });
        });
    },
    /**
     *
     * @param {Poker.FutureActionType} futureActionType
     */
    setSelectedFutureAction : function(futureActionType) {
        this.selectedFutureActionType = futureActionType;
    },
    setFutureActions : function(futureActionTypes, callAmount, raiseAmount) {
        if(this.selectedFutureActionType != null) {
            if(this.selectedFutureActionType.id == Poker.FutureActionType.CHECK_OR_FOLD.id && callAmount>0) {
                this.setSelectedFutureAction(Poker.FutureActionType.FOLD);
            } else if(this.selectedFutureActionType.id == Poker.FutureActionType.CALL_CURRENT_BET.id && callAmount!=this.currentCallAmount) {
                this.clear();
            }   else if(this.selectedFutureActionType.id == Poker.FutureActionType.RAISE.id && raiseAmount!=this.currentRaiseAmount) {
                this.clear();
            }
        }
        this.displayFutureActions(futureActionTypes,callAmount,raiseAmount);
        this.currentRaiseAmount = raiseAmount;
        this.currentCallAmount = callAmount;
    },
    displayFutureActions : function(actions,callAmount,raiseAmount) {
        this.container.show();
        this.container.find(".future-action").hide();
        for(var i = 0; i<actions.length; i++) {
            var actionContainer = this.container.find("."+actions[i].id).show();
            if(actions[i].id === Poker.FutureActionType.CALL_CURRENT_BET.id) {
                actionContainer.find(".amount").html(callAmount);
            } else if(actions[i].id === Poker.FutureActionType.RAISE) {
                actionContainer.find(".amount").html(raiseAmount);
            }
        }
    },
    getFutureActionTypes : function() {
        var types = [];
        for(var x in Poker.FutureActionType) {
            types.push(Poker.FutureActionType[x]);
        }
        return types;
    },
    /**
     *
     * @param {Poker.Action[]} actions
     */
    getAction : function(actions) {
        if(this.selectedFutureActionType == null) {
            return null;
        }
        var selectedId = this.selectedFutureActionType.id;
        switch(selectedId) {
            case Poker.FutureActionType.FOLD.id:
                return this.findAction(Poker.ActionType.FOLD,actions);

            case Poker.FutureActionType.CHECK.id:
                return this.findAction(Poker.ActionType.CHECK,actions);

            case Poker.FutureActionType.CHECK_OR_FOLD.id:
                var check = this.findAction(Poker.ActionType.CHECK,actions)
                if(check==null) {
                    return this.findAction(Poker.ActionType.FOLD,actions);
                } else {
                    return check;
                }
            case Poker.FutureActionType.CALL_CURRENT_BET.id:
                var call = this.findAction(Poker.ActionType.CALL,actions);
                if(call!=null && call.minAmount == this.currentCallAmount) {
                    return call;
                } else {
                    return null;
                }
            case Poker.FutureActionType.CHECK_OR_CALL_ANY.id:
                var check = this.findAction(Poker.ActionType.CHECK,actions);
                if(check==null) {
                    var call = this.findAction(Poker.ActionType.CALL,actions);
                    if(call!=null && this.currentCallAmount == call.minAmount) {
                        return call;
                    }
                }
                return check;

            case Poker.FutureActionType.RAISE.id:
                var raise = this.findAction(Poker.ActionType.RAISE,actions);
                if(raise!=null && raise.minAmount == this.raiseAmount) {
                    return raise;
                } else {
                    return null;
                }

            case Poker.FutureActionType.RAISE_ANY.id:
                var raise = this.findAction(Poker.ActionType.RAISE,actions);
                if(raise!=null) {
                    return raise;
                } else {
                    return null;
                }
        }

    },
    /**
     *
     * @param  type
     * @param {Poker.Action[]} actions
     * @return {Poker.Action}
     */
    findAction : function(type,actions) {
        for(var i = 0; i<actions.length; i++) {
            if(actions[i].type.id === type.id) {
                return actions[i];
            }
        }
        return null;
    },
    clear : function() {
        this.setSelectedFutureAction(null);
        this.container.find(".future-action input").attr("checked",false);
    }

});

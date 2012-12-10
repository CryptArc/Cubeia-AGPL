"use strict";
var Poker = Poker || {};


Poker.PokerPacketHandler = Class.extend({
    tableManager : null,
    tableId : null,
    actionSender : null,
    init : function(tableId) {
        this.tableId = tableId;
        this.tableManager = Poker.AppCtx.getTableManager();
        this.actionSender = new Poker.ActionSender();
    },
    handleRequestAction : function(requestAction) {

        this.tableManager.updateMainPot(this.tableId,requestAction.currentPotSize);

        Poker.PokerSequence.setSequence(this.tableId,requestAction.seq);

        var acts = Poker.ActionUtils.getPokerActions(requestAction.allowedActions);

        if(acts.length>0 && (acts[0].type.id == Poker.ActionType.BIG_BLIND.id || acts[0].type.id == Poker.ActionType.SMALL_BLIND.id)) {
            //for now auto post blinds
            console.log("Auto posting " + acts[0].type.text);
            this.actionSender.sendAction(this.tableId,requestAction.seq, requestAction.allowedActions[0].type, requestAction.allowedActions[0].minAmount);
            return;
        }
        this.tableManager.handleRequestPlayerAction(
            this.tableId,
            requestAction.player,
            acts,
            requestAction.timeToAct);

    },
    handleBuyIn : function(protocolObject) {
        var po = protocolObject;
        console.log("BUY-IN:");
        console.log(protocolObject);
        this.tableManager.handleBuyInInfo(this.tableId,po.balanceInWallet, po.balanceOnTable, po.maxAmount, po.minAmount,po.mandatoryBuyin);
    },
    handlePerformAction : function(performAction){
        var actionType = Poker.ActionUtils.getActionType(performAction.action.type);

        var amount = 0;
        if(performAction.betAmount) {
            amount = Poker.Utils.formatCurrency(performAction.betAmount);
        }
        this.tableManager.handlePlayerAction(this.tableId,performAction.player,actionType,amount);
    }
});
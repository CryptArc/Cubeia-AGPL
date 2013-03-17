"use strict";
var Poker = Poker || {};

Poker.BuyInDialog = Class.extend({
    dialogManager : null,
    templateManager : null,
    init : function() {
        this.dialogManager = Poker.AppCtx.getDialogManager();
        this.templateManager = Poker.AppCtx.getTemplateManager();
    },
    show : function(tableId,tableName, balanceInWallet, maxAmount, minAmount) {

        var data = {
            tableId : tableId,
            title : tableName,
            balance : balanceInWallet,
            maxAmount : maxAmount,
            minAmount : minAmount
        };
        var self = this;
        this.render(data,function(){
            var buyIn = $("#facebox .buyin-amount").val();
            if (self.validateAmount(buyIn)) {
                new Poker.PokerRequestHandler(data.tableId).buyIn(buyIn)
            }
            return false; //don't close the dialog, need to wait for response
        });
        $(".buyin-amount").val(data.maxAmount);

    },
    render : function(data, okFunction) {
        var self = this;
        var template = this.templateManager.getRenderTemplate(this.getTemplateId());
        $("#buyInDialog").html(template.render(data));
        this.dialogManager.displayDialog(
            "buyInDialog",
            function() {
                $("#facebox .buyin-amount").blur();
               return okFunction();
            },
            function() {
                $(".buyin-error").hide();

            });
        $("#facebox .buyin-amount").bind("keyup",function(e){
            if(e.keyCode == 13) {
                $("#facebox .dialog-ok-button").click();
            }
        }).val(data.minAmount).select();
    },
    onError : function(msg) {
        $(".buyin-error").html(msg).show();
    },
    validateAmount : function(amount) {
        return true;
    },
    close : function() {
        this.dialogManager.close();
    },
    getTemplateId : function() {
        return "cashGamesBuyInContent";
    }
});
"use strict";
var Poker = Poker || {};

Poker.BuyInDialog = Class.extend({
    dialogManager : null,
    dialog : null,
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
            var buyIn =  self.dialog.find(".buyin-amount").val();
            if (self.validateAmount(buyIn)) {
                new Poker.PokerRequestHandler(data.tableId).buyIn(buyIn)
            }
            return false; //don't close the dialog, need to wait for response
        });
        this.dialog.find(".buyin-amount").val(data.maxAmount);

    },
    render : function(data, okFunction) {
        var self = this;
        var template = this.templateManager.getRenderTemplate(this.getTemplateId());
        $("#buyInDialog").html(template.render(data));

        var dialog = this.dialogManager.displayDialog(
            "buyInDialog",
            function(dialogElement) {
                dialogElement.find(".buyin-amount").blur();
               return okFunction();
            },
            function(dialogElement) {
                dialogElement.find(".buyin-error").hide();

            });
            dialog.find(".buyin-amount").bind("keyup",function(e){
            if(e.keyCode == 13) {
                dialog.find(".dialog-ok-button").click();
            }
        }).val(data.minAmount).select();
        this.dialog = dialog;
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
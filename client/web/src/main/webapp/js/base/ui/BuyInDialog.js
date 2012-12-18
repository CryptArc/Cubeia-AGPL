"use strict";
var Poker = Poker || {};

Poker.BuyInDialog = Class.extend({
    dialogManager : null,
    init : function() {
        this.dialogManager = Poker.AppCtx.getDialogManager();

    },
    show : function(tableId,tableName, balanceInWallet, maxAmount, minAmount) {
        var formattedMinAmount = Poker.Utils.formatCurrency(minAmount);
        $(".buyin-balance").html(Poker.Utils.formatCurrencyString(balanceInWallet));
        $(".buyin-min-amount").html(Poker.Utils.formatCurrencyString(minAmount));
        $(".buyin-max-amount").html(Poker.Utils.formatCurrencyString(maxAmount));
        $(".buyin-table-name").html(tableName);
        var self = this;

        $(".buyin-amount").val(Poker.Utils.formatCurrency(minAmount));

        this.dialogManager.displayDialog(
            "buyinDialog",
            function(){
                var val = $("#facebox .buyin-amount").val();
                if(self.validateAmount(val)) {
                    new Poker.PokerRequestHandler(tableId).buyIn(Math.round(parseFloat(val)*100))
                }
                return false; //don't close the dialog, need to wait for response
            },
            function(){
                $(".buyin-error").hide();

            });
        $("#facebox .buyin-amount").bind("keyup",function(e){
            if(e.keyCode == 13) {
                $("#facebox .dialog-ok-button").click();
            }
        }).val(formattedMinAmount).select();
    },
    onError : function(msg) {
        $(".buyin-error").html(msg).show();
    },
    validateAmount : function(amount) {
        return true;
    },
    close : function() {
        this.dialogManager.close();
    }
});

"use strict";
var Poker = Poker || {};

Poker.BuyInDialog = Class.extend({
    tableComHandler : null,
    init : function(tableComHandler) {
        this.tableComHandler = tableComHandler;
    },
    show : function(tableName, balanceInWallet, maxAmount, minAmount) {
        $(".buyin-balance").html("&euro;").append(Poker.Utils.formatCurrency(balanceInWallet));
        $(".buyin-min-amount").html("&euro;").append(Poker.Utils.formatCurrency(minAmount));
        $(".buyin-max-amount").html("&euro;").append(Poker.Utils.formatCurrency(maxAmount));
        $(".buyin-table-name").html(tableName);
        var self = this;

        $(".buyin-amount").val(Poker.Utils.formatCurrency(minAmount));

        dialogManager.displayDialog(
            "buyinDialog",
            function(){
                var val = $("#facebox .buyin-amount").val();
                if(self.validateAmount(val)) {
                    self.tableComHandler.buyIn(Math.round(parseFloat(val)*100));
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
        }).focus();
    },
    onError : function(msg) {
        $(".buyin-error").html(msg).show();
    },
    validateAmount : function(amount) {
        return true;
    },
    close : function() {
        dialogManager.close();
    }
});
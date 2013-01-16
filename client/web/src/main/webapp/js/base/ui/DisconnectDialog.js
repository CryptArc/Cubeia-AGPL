"use strict";
var Poker = Poker || {};

/**
 *
 * @type {Poker.DisconnectDialog}
 */
Poker.DisconnectDialog = Class.extend({
    dialogManager : null,
    templateManager : null,
    open : false,
    init : function() {
        this.dialogManager = Poker.AppCtx.getDialogManager();
        this.templateManager = Poker.AppCtx.getTemplateManager();
    },
    show : function(count) {
        var self = this;
        $(".reconnectAttempt").html(count);
        if(this.open==false) {
            this.dialogManager.displayDialog(
                "disconnectDialog",
                function() {
                    return true;
                },
                function() {
                    self.open = false;
                });
        }
        this.open  = true;

    },
    close : function() {
        this.dialogManager.close();
        this.open = false;
    },
    getTemplateId : function() {
        return "cashGamesBuyInContent";
    }
});
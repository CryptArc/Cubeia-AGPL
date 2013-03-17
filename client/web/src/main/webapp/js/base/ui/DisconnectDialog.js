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
                    document.location.reload();
                },
                function() {
                    self.open = false;
                });
        }
        this.open  = true;

    },
    stoppedReconnecting : function() {
        $(".disconnect-reconnecting").hide();
        $(".stopped-reconnecting").show();
    },
    close : function() {
        this.dialogManager.close();
        this.open = false;
    },
    getTemplateId : function() {
        return "cashGamesBuyInContent";
    }
});

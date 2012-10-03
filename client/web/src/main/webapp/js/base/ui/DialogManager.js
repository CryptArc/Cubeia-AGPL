"use strict";
var Poker = Poker || {};
Poker.DialogManager = Class.extend({
    templateManager : null,
    container : null,
    currentCloseCallback : null,
    open : false,
    dialogQueue : [],
    init : function()  {
        this.templateManager = new Poker.TemplateManager();
        var c = $("<div/>").attr("id","genericDialogContainer");
        $("body").append(c);
        this.container = $("#genericDialogContainer");

        var self = this;
        $(document).bind("close.facebox",function(){
            if(self.currentCloseCallback!=null) {
                self.currentCloseCallback();
            }
            self.open = false;
            self.openQueuedDialog();
        });
    },
    openQueuedDialog : function() {
        if(this.dialogQueue.length>0) {
            var d = this.dialogQueue[0];
            this.dialogQueue.splice(0,1);
            self.displayDialog(d.dialogId, d.okCallback, d.closeCallback);
        }
    },
    queueDialog : function(dialogId,okCallback,closeCallback) {
        this.dialogQueue.push({
           dialogId : dialogId,
           okCallback : okCallback,
           closeCallback : closeCallback
        });
    },
    /**
     * Display a dialog by passing a DOM element id you want to be placed in
     * the dialog, if a dialog is open it will be queued and showed when
     * previous dialog is closed
     * @param dialogId
     * @param okCallback
     * @param closeCallback
     */
    displayDialog : function(dialogId, okCallback, closeCallback) {
        if(this.open == true) {
            this.queueDialog(dialogId,okCallback,closeCallback);
            return;
        }
        this.open = true;

        var self = this;
        var fb = $.facebox({div : "#" + dialogId, opacity:0.6});
        if(closeCallback) {
            this.currentCloseCallback = closeCallback;
        }
        $("#facebox .dialog-cancel-button").click(function(){
            self.close();
        });
        $("#facebox .dialog-ok-button").click(function(){
            if(okCallback() || !okCallback) {
                self.close();
            }
        });
    },
    close : function() {
        $.facebox.close();
    }
});
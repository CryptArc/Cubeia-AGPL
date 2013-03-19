"use strict";
var Poker = Poker || {};
Poker.DialogManager = Class.extend({
    templateManager: null,
    container: null,
    currentCloseCallback: null,
    open: false,
    dialogQueue: null,
    currentDialog : null,
    init: function() {
        this.dialogQueue = [];
        this.templateManager = new Poker.TemplateManager();
        var c = $("<div/>").attr("id", "genericDialogContainer");
        $("body").append(c);
        this.container = $("#genericDialogContainer");

        var self = this;
        $(document).bind("afterClose.facebox", function() {
            if (self.currentCloseCallback != null) {
                self.currentCloseCallback();
            }
            self.open = false;
            self.openQueuedDialog();
        });
    },
    openQueuedDialog: function() {
        if (this.dialogQueue.length > 0) {
            var d = this.dialogQueue[0];
            this.dialogQueue.splice(0, 1);
            this.displayDialog(d.dialogId, d.okCallback, d.closeCallback);
        }
    },
    queueDialog: function(dialogId, okCallback, closeCallback) {
        this.dialogQueue.push({
            dialogId: dialogId,
            okCallback: okCallback,
            closeCallback: closeCallback
        });
    },
    /**
     * Displays a generic dialog with a header, message and a continue button
     * example
     * displayManager.displayGenericDialog({header : "header" , message:"message", okButtonText : "reload"});
     * @param {Object} content - the content of the dialog, see above for format
     * @param {String} content.header - header of the dialog
     * @param {String} content.message - the message to display
     * @param {String} [content.translationKey] - optional translation key to use
     * @param {Boolean} [content.displayCancelButton] - if you should display a cancel
     * @param {Function} [okCallback] callback to execute when ok button is clicked
     */
    displayGenericDialog: function(content, okCallback) {

        if(typeof(content.translationKey)!="undefined") {
            content = $.extend(content,{ header : i18n.t("dialogs." + content.translationKey + ".header"),
                message : i18n.t("dialogs." + content.translationKey + ".message")});
        }

        if (content.header) {
            $("#genericDialog h1").html(content.header);
        } else {
            $("#genericDialog h1").hide();
        }

        if (content.message) {
            $("#genericDialog .message").html(content.message);
        } else {
            $("#genericDialog .message").hide();
        }

        if(content.displayCancelButton === true) {
            $("#genericDialog .dialog-cancel-button").show();
        } else {
            $("#genericDialog .dialog-cancel-button").hide();
        }
        var self = this;
        if (typeof(content.okButtonText) != "undefined") {
            $("#genericDialog .dialog-ok-button").html(content.okButtonText);
        }
        if (typeof(okCallback) == "undefined") {
            this.displayDialog("genericDialog", function() {
                self.close();
            }, null);
        } else {
            this.displayDialog("genericDialog", function() {
                return okCallback();
            }, null);
        }
    },

    display : function(dialog,okCallback,closeCallback) {
        if (closeCallback) {
            this.currentCloseCallback = closeCallback;
        }

        var targetFontSize =  Math.round(90* $(window).width()/1024);
        if (targetFontSize > 125) {
            targetFontSize = 125;
        }

        var dialogElement = dialog.getElement();
        dialogElement.css({fontSize : targetFontSize + "%"});
        dialogElement.find(".dialog-cancel-button").touchSafeClick(function(){
            dialog.close();
        });
        dialogElement.find(".dialog-ok-button").touchSafeClick(function() {
            if (okCallback() || !okCallback) {
                dialog.close();
            }
        });

        this.currentDialog = dialog;
    },

    /**
     * Display a dialog by passing a DOM element id you want to be placed in
     * the dialog, if a dialog is open it will be queued and showed when
     * previous dialog is closed
     * @param dialogId
     * @param okCallback
     * @param closeCallback
     */
    displayDialog: function(dialogId, okCallback, closeCallback) {
        if (this.open == true) {
            this.queueDialog(dialogId, okCallback, closeCallback);
            return;
        }
        this.open = true;

        var self = this;
        var dialog = new Poker.Dialog($("body"), $("#"+dialogId));
        this.display(dialog,okCallback,closeCallback);
        return dialog;
    },
    close : function() {
        if(this.currentDialog!=null) {
            this.currentDialog.close();
        }
    }
});

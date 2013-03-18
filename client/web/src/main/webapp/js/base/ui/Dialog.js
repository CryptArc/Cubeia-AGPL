var Poker = Poker || {};

Poker.DialogSequence = 0;
Poker.Dialog = Class.extend({
    /**
     * @type {Poker.TemplateManager}
     */
    templateManager : null,
    parentContainer : null,
    dialogContent : null,
    dialogElement : null,
    id : null,
    settings : {
        closeOnBackgroundClick : true
    },
    /**
     *
     * @param parentContainer
     * @param dialogId
     * @param settings.closeOnBackgroundClick
     */
    init : function(parentContainer,dialogContent,settings) {
        this.settings = $(this.settings, settings || {});
        this.dialogContent = dialogContent;
        this.templateManager = Poker.AppCtx.getTemplateManager();
        this.parentContainer = parentContainer;
        this.id = "dialog-" + Poker.DialogSequence++;
        this.show();
    },
    show : function() {
        var self = this;
        var html = this.templateManager.render("overLayDialogTemplate",{ dialogId : this.id });
        this.parentContainer.append(html);
        this.dialogElement = $("#"+this.id);
        var content = this.dialogElement.find(".dialog-content");

        content.append(this.dialogContent.html());
        var height = this.parentContainer.height();
        if(height == 0) {
            height = $(window).height();
        }
        var top = 30 * ( height - content.outerHeight() ) / height;
        console.log("height = " + height + ", ch="+ content.outerHeight());
        content.css("top",top + "%");
    },
    close : function() {
        $("#"+this.id).remove();
    },
    getElement : function() {
        return this.dialogElement;
    }

});
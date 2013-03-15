var Poker = Poker || {};

Poker.Log = Class.extend({

    logContainer : null,
    templateManager : null,
    init : function(logContainer) {
        this.logContainer = logContainer;
        this.templateManager = Poker.AppCtx.getTemplateManager();
    },

    append : function(message){

        var scrollDown = false;
        if(Math.abs(this.logContainer[0].scrollHeight - this.logContainer.scrollTop() - this.logContainer.outerHeight())<10) {
            scrollDown = true;
        }

        this.logContainer.append(message);

        if(scrollDown==true) {
            this.logContainer.scrollTop(this.logContainer[0].scrollHeight);
        }


    },
    appendTemplate : function(templateId,data) {
        var html = this.templateManager.render(templateId,data);
        this.append(html);
    }
});
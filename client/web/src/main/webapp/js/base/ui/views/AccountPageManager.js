"use strict";
var Poker = Poker || {};
Poker.AccountPageManager = Class.extend({
    templateManager : null,
    menuItemTemplate : null,
    init : function() {
        this.templateManager = new Poker.TemplateManager();
        this.menuItemTemplate = "menuItemTemplate";
        var self = this;
        $(".my-page-button").touchSafeClick(function(){
            self.toggle();
        });

        $(".account-overlay").touchSafeClick(function(){
            self.toggle();
        });
    },

    activeView : null,

    toggle : function() {
        var iframe = document.getElementById("account_iframe");
        var url = Poker.OperatorConfig.getProfilePageUrl();
        iframe.setAttribute("src", url);

        $(".account-overlay").toggle();
        /*
        $("#mainMenuList").find("li").removeClass("active");
        if(this.activeView!=null){
            this.activeView.deactivate();
        }
        */
    }
});


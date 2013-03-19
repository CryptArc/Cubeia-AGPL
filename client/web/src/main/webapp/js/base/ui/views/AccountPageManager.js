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

        $("#account_iframe").attr("src", "http://www.cubeiasocial.com");

    },

    activeView : null,

    toggle : function() {
    //    $('.main-menu-container').toggleClass('visible');
    //    $(".view-container").toggleClass("slided");
        $(".account-overlay").toggle();
        $("#mainMenuList").find("li").removeClass("active");
        if(this.activeView!=null){
            this.activeView.deactivate();
        }
    }
});


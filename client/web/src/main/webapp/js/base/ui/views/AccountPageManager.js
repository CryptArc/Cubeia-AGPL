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

        var token = "TOKENSTUFF";

    //    var iframe = $("#account_iframe");

    //    var testurl = "file:///C:/projects/operator-api/player-api/server/src/main/webapp/html/test.html"


    //    iframe.attr("src", testurl);

    //    var iframe = document.getElementById("account_iframe");

    },

    activeView : null,

    toggle : function() {
        var iframe = document.getElementById("account_iframe");
        // TODO: get this URL from operator configuration
        var temporaryHardcodedUrl = "http://csobe1.cubeia.com/player-api/html/index.html";
        iframe.setAttribute("src", temporaryHardcodedUrl);

        $(".account-overlay").toggle();
        $("#mainMenuList").find("li").removeClass("active");
        if(this.activeView!=null){
            this.activeView.deactivate();
        }
    }
});


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
    //    var testurl = "file:///C:/projects/operator-api/player-api/server/src/main/webapp/html/test.html"
    //    var testurl2 = "http://www.cubeiasocial.com"
           var testurl3 = "https://dl.dropbox.com/u/5300639/player-api/index.html"
        iframe.setAttribute("src", testurl3);

        $(".account-overlay").toggle();
        $("#mainMenuList").find("li").removeClass("active");
        if(this.activeView!=null){
            this.activeView.deactivate();
        }
    }
});


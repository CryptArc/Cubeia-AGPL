"use strict";
var Poker = Poker || {};
Poker.AccountPageManager = Class.extend({
    templateManager : null,
    menuItemTemplate : null,
    activeView : null,
    userPanel : null,
    userOverlay : null,
    buyCreditsView : null,
    editProfileView : null,
    init : function() {
        this.templateManager = new Poker.TemplateManager();
        this.menuItemTemplate = "menuItemTemplate";
        this.userPanel = $(".user-panel");
        this.userOverlay = $(".user-overlay-container");
        this.setupUserPanel();
        var self = this;
        $("#editProfileButton").click(function(e){
            self.toggleAccountingOverlay();
            if(self.editProfileView==null) {
                var url = Poker.OperatorConfig.getProfilePageUrl();
                self.editProfileView = new Poker.ExternalPageView("editProfileView","Edit Profile","C",self.addToken(url));
                self.editProfileView.fixedSizeView = true;
                Poker.AppCtx.getViewManager().addView(self.editProfileView);

            }
            Poker.AppCtx.getViewManager().activateView(self.editProfileView);
        });
        $("#buyCreditsButton").click(function(e){
            self.toggleAccountingOverlay();
            if(self.buyCreditsView==null) {
                var url = Poker.OperatorConfig.getBuyCreditsUrl();
                self.buyCreditsView = new Poker.ExternalPageView("buyCreditsView","Buy credits","C",self.addToken(url));
                self.buyCreditsView.fixedSizeView = true;
                Poker.AppCtx.getViewManager().addView(self.buyCreditsView);

            }
            Poker.AppCtx.getViewManager().activateView(self.buyCreditsView);

        });


    },
    addToken : function(url) {
        return url + "?token="+Poker.MyPlayer.sessionToken+"&playerId="+Poker.MyPlayer.id + "&r="+Math.random();
    },
    toggleAccountingOverlay : function() {
        this.userOverlay.toggle();
        this.userPanel.toggleClass("active");
        this.toggle();
    },
    setupUserPanel : function() {
        var self = this;
        this.userPanel.click(function(e){
            self.toggleAccountingOverlay();
            $(document).mouseup(function(e){
                if(self.userPanel.has(e.target).length === 0
                    && self.userOverlay.has(e.target).length === 0) {
                    self.toggleAccountingOverlay();
                    $(document).off("mouseup");
                }
            });
        });
    },
    toggle : function() {
        var iframe = $("#accountIframe");
        var url = Poker.OperatorConfig.getAccountInfoUrl();
        iframe.attr("src",this.addToken(url));
    }
});


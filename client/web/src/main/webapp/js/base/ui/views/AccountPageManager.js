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
    currentBonus : null,
    init : function() {
        this.templateManager = new Poker.TemplateManager();
        this.menuItemTemplate = "menuItemTemplate";
        this.userPanel = $(".user-panel");
        this.userOverlay = $(".user-overlay-container");
        this.setupUserPanel();
        var self = this;
        var vm =  Poker.AppCtx.getViewManager();
        $("#refillButton").click(function(e){
            self.requestTopUp();
        });
        $("#editProfileButton").click(function(e){
            self.closeAccountOverlay();
            if(self.editProfileView==null) {
                var url = Poker.OperatorConfig.getProfilePageUrl();
                self.editProfileView = new Poker.ExternalPageView(
                    "editProfileView","Edit Profile","C",self.addToken(url),function(){
                        vm.removeView(self.editProfileView);
                        self.editProfileView = null;
                    });
                self.editProfileView.fixedSizeView = true;
               vm.addView(self.editProfileView);

            }
            Poker.AppCtx.getViewManager().activateView(self.editProfileView);
        });
        $("#buyCreditsButton").click(function(e){
            self.closeAccountOverlay();
            if(self.buyCreditsView==null) {
                var url = Poker.OperatorConfig.getBuyCreditsUrl();
                self.buyCreditsView = new Poker.ExternalPageView(
                    "buyCreditsView","Buy credits","C",self.addToken(url),
                    function(){
                        vm.removeView(self.buyCreditsView);
                        self.buyCreditsView = null;
                    });
                self.buyCreditsView.fixedSizeView = true;
                Poker.AppCtx.getViewManager().addView(self.buyCreditsView);

            }
            Poker.AppCtx.getViewManager().activateView(self.buyCreditsView);

        });

        $(".logout-link").click(function() {
            self.closeAccountOverlay();
            self.logout();
        });

    },
    onLogin : function(playerId,name) {
        var self = this;
        $(".username").html(name);
        $(".user-id").html(playerId);
        if(Poker.MyPlayer.sessionToken!=null) {
            Poker.AppCtx.getPlayerApi().requestPlayerProfile(playerId,Poker.MyPlayer.sessionToken,
                function(profile){
                    console.log("PROFILE");
                    console.log(profile);
                    if (profile !=null && profile.externalAvatarUrl != null) {
                        $(".user-panel-avatar").addClass("user-panel-custom-avatar").css("backgroundImage","url('"+profile.externalAvatarUrl+"')");
                    } else {
                        self.displayDefaultAvatar(playerId);
                    }
                },
                function(){
                    self.displayDefaultAvatar(playerId);
                })
        } else {
            this.displayDefaultAvatar(playerId);
        }
    },
    displayDefaultAvatar : function(playerId){
        $(".user-panel-avatar").addClass("avatar" + (playerId % 9));
    },
    logout : function() {
        $.ga._trackEvent("user_navigation", "clicked_logout");
        Poker.Utils.removeStoredUser();
        var logout_url = Poker.OperatorConfig.getLogoutUrl();
        if(!logout_url) {
            Poker.AppCtx.getCommunicationManager().setIgnoreNextForceLogout();
            Poker.AppCtx.getCommunicationManager().getConnector().logout(true);
            document.location.reload();
        } else {
            var dialogManager = Poker.AppCtx.getDialogManager();
            dialogManager.displayGenericDialog({
                container:  Poker.AppCtx.getViewManager().getActiveView().getViewElement(),
                header: i18n.t("account.logout"),
                message: i18n.t("account.logout-warning"),
                displayCancelButton: true
            }, function() {
                Poker.AppCtx.getCommunicationManager().setIgnoreNextForceLogout();
                Poker.AppCtx.getCommunicationManager().getConnector().logout(true);
                document.location = logout_url;
            });
        }
    },
    addToken : function(url) {
        return url + "?userSessionToken="+Poker.MyPlayer.sessionToken+"&playerId="+Poker.MyPlayer.id + "&skin=" + Poker.SkinConfiguration.name
            +"&operatorId=" + Poker.SkinConfiguration.operatorId + "&operatorAuthToken=" + Poker.MyPlayer.loginToken + "&r="+Math.random();
    },
    closeAccountOverlay : function() {
        this.userOverlay.hide();
        this.userPanel.removeClass("active");
    },
    openAccountOverlay : function() {
        this.userOverlay.show();
        this.userPanel.addClass("active");
        this.openAccountFrame();
    },
    setupUserPanel : function() {
        var self = this;
        this.userPanel.click(function(e){
            if(self.userOverlay.is(":visible")) {
                self.closeAccountOverlay();
                $(document).off("mouseup.account");
            } else {
                self.openAccountOverlay();
            }
            $(document).on("mouseup.account",function(e){
                if(self.userPanel.has(e.target).length === 0
                    && self.userOverlay.has(e.target).length === 0) {
                    self.closeAccountOverlay();
                    $(document).off("mouseup.account");
                }
            });
        });
    },
    createParametersFromCurrencies: function(currencies) {
        var parameters = "";
        for (var i = 0; i < currencies.length; i++) {
            var currency = currencies[i];
            parameters += "&" + currency.code + "=" + currency.name;
        }
        return parameters;
    },
    openAccountFrame : function() {
        $.ga._trackEvent("user_navigation", "open_account_frame");

        var url = Poker.OperatorConfig.getAccountInfoUrl();
        if(url!=null && (url=="" || url=="internal")) {
           this.displayInternalAccountPage();
        } else {
            $("#internalAccountContent").hide();
            var iframe = $("#accountIframe");
            var urlWithParams = this.addToken(url);
            // Add currency params so we can show currencies in the way specified by the operator.
            urlWithParams += this.createParametersFromCurrencies(Poker.OperatorConfig.getEnabledCurrencies());
            iframe.attr("src", urlWithParams);
        }

    },
    displayInternalAccountPage : function() {
        $("#internalAccountContent").show();
        $("#accountIframe").hide();
        var self = this;
        Poker.AppCtx.getPlayerApi().requestAccountInfo(Poker.MyPlayer.sessionToken,
            function(data){
                var name = "";
                if(typeof(data.screenname)!="undefined") {
                    name = data.screenname;
                } else if(typeof(data.externalUsername)!="undefined") {
                    name = data.externalUsername;
                } else if(typeof(data.username)!="undefined") {
                    name = data.username;
                }
                $("#user_name").html(name);

            },
            function(){
                console.log("Error fetching account info");
            }
        );
        Poker.AppCtx.getPlayerApi().requestBonusInfo(Poker.MyPlayer.sessionToken,
            function(data){
              self.onBonusInfo(data);
            },
            function(){}
        );
    },
    onBonusInfo : function(data) {
        var self = this;
        var accounts = [];
        $.each(data.accounts,function(i,a){
            if(Poker.OperatorConfig.isCurrencyEnabled(a.currency)) {
                var formattedBalance = Poker.Utils.formatWithSymbol(a.balance, a.currency);
                accounts.push({ balance : formattedBalance});
            }
        });
        var template = Poker.AppCtx.getTemplateManager().getRenderTemplate("balanceTemplate");

        $("#accountBalancesContainer").html(template.render({accounts : accounts}));
        $("#topUpCurrencies").empty();
        $.each(data.bonuses,function(i,bonus){
            var currencyName = Poker.Utils.translateCurrencyCode(bonus.currencyCode);
            $("#topUpCurrencies").append($("<div/>").attr("id","topUp"+bonus.currencyCode).html(currencyName).click(function(e){
                self.displayTopUpInfo(bonus);
            }));
        });
        this.displayTopUpInfo(this.getCurrentBonus(data.bonuses));
    },
    getCurrentBonus : function(bonuses) {
        var name = this.getCurrentBonusName();
        if(name==null) {
            return bonuses[0];
        } else {
            for(var i = 0; i<bonuses.length; i++) {
                var b = bonuses[i];
                if(b.bonusName == name) {
                    return b;
                }
            }
            return bonuses[0];
        }

    },
    getCurrentBonusName : function(){
        if(this.currentBonus == null) {
            return null;
        }
        return this.currentBonus.bonusName;
    },
    displayTopUpInfo : function(bonus) {
        this.currentBonus = bonus;
        $("#topUpCurrencies .active").removeClass("active");
        $("#topUp"+bonus.currencyCode).addClass("active");
        if(bonus.timeToNextCollect>0) {
            $("#coolDownProgress").show();
            $("#bonusCollectContainer .top-up-progress").show();
            $("#bonusCollectContainer .balance-too-high").hide();
            var fractionRemaining = 100 * bonus.timeToNextCollect / bonus.coolDown;
            $("#coolDownProgress").width(fractionRemaining+"%");
            $("#refillButton").attr("class","").addClass("refill-unavailable");
            var time = new Date().getTime()+bonus.timeToNextCollect;
            $("#coolDownLabel").html(moment(time).fromNow());
        } else if(bonus.canCollect == true) {
            $("#bonusCollectContainer .top-up-progress").show();
            $("#bonusCollectContainer .balance-too-high").hide();
            $("#coolDownProgress").hide();
            $("#refillButton").attr("class","").addClass("refill-available");
            $("#coolDownLabel").html("Top up is available!");
        } else {
            $("#bonusCollectContainer .balance-too-high").show();
            $("#bonusCollectContainer .top-up-progress").hide();
            $("#bonusCoolDownTime").html((bonus.coolDown/3600000));
            $("#bonusBalanceLowerLimit").html(bonus.bonusBalanceLowerLimit);
        }
    },
    requestTopUp : function() {
        var self = this;
        Poker.AppCtx.getPlayerApi().requestTopUp(this.currentBonus.bonusName,Poker.MyPlayer.sessionToken,
            function(data){
                self.onBonusInfo(data);
            },
            function(){

            }
        );
    }
});


"use strict";
var Poker = Poker || {};
Poker.MainMenuManager = Class.extend({
    templateManager : null,
    menuItemTemplate : null,
    init : function(viewManager) {
        this.templateManager = new Poker.TemplateManager();
        this.menuItemTemplate = this.templateManager.getTemplate("menuItemTemplate");
        var self = this;
        $(".main-menu-button").click(function(e){
            self.toggle();
        });
        $(".menu-overlay").click(function(e){
            self.toggle();
        })
        var cashier = new Poker.MenuItem("Cashier","Manage your funds","cashier")
        this.addMenuItem(cashier,null);
        var helpMenuItem = new Poker.MenuItem("Help & rules","Learn how to play poker","help");
        this.addExternalMenuItem(helpMenuItem,function(){
            console.log(Poker.OperatorConfig.getClientHelpUrl());
            window.open(Poker.OperatorConfig.getClientHelpUrl());
        });
        this.addMenuItem(new Poker.MenuItem("Gameplay settings","Muck loosing cards, Muck winning cards","gameplay"),null);
        this.addMenuItem(new Poker.MenuItem("Sound settings","Turn sound on/off","sound"),null);
        var devSettings = new Poker.MenuItem("Development settings","Settings only shown in development","development");
        this.addMenuItem(devSettings,new Poker.DevSettingsView("#devSettingsView",""));

    },
    activeView : null,
    addExternalMenuItem : function(item,activateFunc){
        var self = this;
        item.setActivateFunction(activateFunc);
        item.appendTo("#mainMenuList",this.menuItemTemplate);
        if(self.activeView!=null) {
            self.activeView.deactivate();
        }
    },
    addMenuItem : function(item,view) {
        var self = this;
        item.setActivateFunction(function(){
           $("#mainMenuList").find("li").removeClass("active");
            if(self.activeView!=null) {
                self.activeView.deactivate();
            }
            if(view!=null) {
                self.activeView = view;
                view.activate();
            }

        });

        item.appendTo("#mainMenuList",this.menuItemTemplate);

    },
    toggle : function() {
        $('.main-menu-container').toggleClass('visible');
        $(".view-container").toggleClass("slided");
        $(".menu-overlay").toggle();
        $("#mainMenuList").find("li").removeClass("active");
        if(this.activeView!=null){
            this.activeView.deactivate();
        }
    }
});

Poker.MenuItem = Class.extend({
    title : null,
    description : null,
    cssClass : null,
    activateFunction : null,

    init : function(title,description,cssClass){

        this.title = title;
        this.description = description;
        this.cssClass = cssClass;
    },
    setActivateFunction : function(func) {
        this.activateFunction = func;
    },
    appendTo : function(containerId,template) {
        var self = this;
        var html = Mustache.render(template,
            {
                title:this.title,
                description:this.description,
                cssClass : this.cssClass
            });
        $(containerId).append(html);
        $(containerId).find("."+this.cssClass).click(function(){
            self.activateFunction();
            $(this).addClass("active");
        });
    }
});
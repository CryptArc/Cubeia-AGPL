"use strict";
var Poker = Poker || {};

Poker.View = Class.extend({
    viewElement : null,
    fixedSizeView : false,
    id : null,
    init : function(viewElementId,name) {
        this.viewElement = $(viewElementId);
        var self = this;
    },
    activate : function() {
        this.viewElement.show();
        this.onViewActivated();
    },
    deactivate : function() {
        this.viewElement.hide();
        this.onViewDeactivated();
    },
    onViewActivated : function() {

    },
    onViewDeactivated : function() {

    },
    close : function() {
        this.viewElement.remove();
    }

});
Poker.TabView = Poker.View.extend({
    selectable : true,
    tabElement : null,
    init : function(viewElement,name) {

        this._super(viewElement,name);
        var t = Poker.AppCtx.getTemplateManager().getTemplate("tabTemplate");

        var item = $(Mustache.render(t,{name:name}));
        this.tabElement = item;
    },
    setSelectable : function (selectable) {
        if(selectable == false) {
            this.tabElement.hide();
            this.viewElement.hide();
        } else {
            this.tabElement.show();
        }
    },
    requestFocus : function() {
        if(!this.tabElement.hasClass("active")) {
            this.tabElement.addClass("focus");
        }
    },
    activateTab : function() {
        this.tabElement.addClass("active");
        this.tabElement.removeClass("focus");
    },
    deactivateTab : function() {
        this.tabElement.removeClass("active");
    },
    onViewActivated : function() {
        this.activateTab();
    },
    onViewDeactivated : function() {
        this.deactivateTab();
    },
    close : function(){
        this.tabElement.remove();
        this.viewElement.remove();
    }

});

Poker.TableView = Poker.TabView.extend({
    layoutManager : null,
    init : function(layoutManager,name) {
        this._super("#"+layoutManager.tableView.attr("id"),name);
        this.layoutManager = layoutManager;
    },
    onViewActivated : function() {
        this.layoutManager.onActivateView();
        this.activateTab();
        this.viewElement.removeClass("no-transitions");
    },
    onViewDeactivated : function() {
        $("#tableViewContainer").hide();
        this.deactivateTab();
        this.viewElement.addClass("no-transitions");
        this.layoutManager.onDeactivateView();
    },
    getTableId : function()  {
        return this.layoutManager.tableId;
    }

});

Poker.DevSettingsView = Poker.View.extend({
    init : function(viewElementId,name) {
        this._super(viewElementId,name);
    },
    onViewActivated : function() {
        Poker.Settings.bindSettingToggle($("#swipeEnabled"),Poker.Settings.Param.SWIPE_ENABLED);
        Poker.Settings.bindSettingToggle($("#freezeComEnabled"),Poker.Settings.Param.FREEZE_COMMUNICATION);
    },
    onDeactivateView : function() {
        $("#swipeEnabled").unbind();
        $("#freezeComEnabled").unbind();
    }
});
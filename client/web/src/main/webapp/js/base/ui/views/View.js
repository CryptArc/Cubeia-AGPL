"use strict";
var Poker = Poker || {};

Poker.View = Class.extend({
    viewElement : null,

    id : null,
    init : function(viewElementId,name) {
        this.viewElement = $(viewElementId);
        var self = this;
    },
    activate : function() {
        this.activateView();
    },
    deactivate : function() {
        this.deactivateView();
    },
    activateView : function() {
        this.viewElement.show();
    },
    deactivateView : function() {
        this.viewElement.hide();
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
        var item = $("<div/>").append($("<span/>").html(name));
        this.tabElement = $("<li/>").append(item);
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
    activate : function() {
        this.activateView();
        this.activateTab();
    },
    deactivate : function() {
        this.deactivateView();
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
    activate : function() {
        this.layoutManager.onActivateView();
        this.activateView();
        this.activateTab();
        this.viewElement.removeClass("no-transitions");
    },
    deactivate : function() {

        $("#tableViewContainer").hide();
        this.deactivateView();
        this.deactivateTab();
        this.viewElement.addClass("no-transitions");
        this.layoutManager.onDeactivateView();
    },
    getTableId : function()  {
        return this.layoutManager.tableId;
    }

});
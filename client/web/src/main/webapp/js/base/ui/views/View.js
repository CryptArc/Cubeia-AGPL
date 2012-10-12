"use strict";
var Poker = Poker || {};

Poker.View = Class.extend({
    viewElement : null,
    selectable : true,
    tabElement : null,
    id : null,
    init : function(viewElementId,name) {
        this.viewElement = $(viewElementId);
        var item = $("<div/>").append($("<span/>").html(name));
        this.tabElement = $("<li/>").append(item);
        var self = this;
    },
    activate : function() {
        this.activateView();
        this.activateTab();
    },
    requestFocus : function() {
        if(!this.tabElement.hasClass("active")) {
            this.tabElement.addClass("focus");

        }
    },
    activateView : function() {
        this.viewElement.show();
    },
    deactivateView : function() {
        console.log("Deactivating view " + this.viewElement.attr("id"));
        this.viewElement.hide();
    },
    activateTab : function() {
        this.tabElement.addClass("active");
        this.tabElement.removeClass("focus");
    },
    deactivate : function() {
        this.deactivateView();
        this.deactivateTab();
    },
    deactivateTab : function() {
        this.tabElement.removeClass("active");
    },
    close : function() {
        this.tabElement.remove();
        this.viewElement.remove();
    },
    setSelectable : function (selectable) {
        if(selectable == false) {
            this.tabElement.hide();
            this.viewElement.hide();
        } else {
            this.tabElement.show();
        }
    }
});

Poker.TableView = Poker.View.extend({
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
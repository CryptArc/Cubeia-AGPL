"use strict";
var Poker = Poker || {};
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

"use strict";
var Poker = Poker || {};
Poker.TabView = Poker.View.extend({
    selectable : true,
    tabElement : null,
    cardTemplate : null,
    init : function(viewElement,name) {

        this._super(viewElement,name);
        var t = Poker.AppCtx.getTemplateManager().getTemplate("tabTemplate");
        this.cardTemplate = Poker.AppCtx.getTemplateManager().getTemplate("miniCardTemplate");

        var item = $(Mustache.render(t,{name:name}));
        this.tabElement = item;
        this.tabElement.find(".mini-cards").hide();
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
    updateInfo : function(data) {
        var c = data.card;
        if(c!=null) {
            var html = Mustache.render(this.cardTemplate,{domId:c.id + "-" + c.tableId, cardString:c.cardString});
            this.tabElement.find(".mini-cards").attr("style","").append(html);
        } else {
            this.tabElement.find(".mini-cards").empty().hide();
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
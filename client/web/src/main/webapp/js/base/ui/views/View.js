"use strict";
var Poker = Poker || {};

Poker.View = Class.extend({
    viewElement : null,
    selectable : true,
    tabElement : null,
    init : function(viewElementId,name) {
        this.viewElement = $(viewElementId);
        this.tabElement = $("<li/>").html(name);
        var self = this;
    },
    activate : function() {
        this.viewElement.show();
        this.tabElement.addClass("active");
    },
    deactivate : function() {
        this.viewElement.hide();
        this.tabElement.removeClass("active");
    },
    close : function() {

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
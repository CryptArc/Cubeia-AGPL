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
    updateInfo : function(data) {

    },
    close : function() {
        this.viewElement.remove();
    }

});





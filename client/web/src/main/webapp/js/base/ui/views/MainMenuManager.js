"use strict";
var Poker = Poker || {};
Poker.MainMenuManager = Class.extend({

    init : function(viewManager) {
        $(".main-menu-button").click(function(e){
            $('.main-menu-container').toggleClass('visible');
            $(".slidable").toggleClass("slided");
        });
    }
});
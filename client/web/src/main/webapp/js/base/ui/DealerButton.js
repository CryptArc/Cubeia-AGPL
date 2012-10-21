"use strict";
var Poker = Poker || {};
Poker.DealerButton = Class.extend({
    element : null,
    showing : false,
    animationManager : null,
    init : function(element,animationManager) {
        this.animationManager = animationManager;
        this.element = element;
        this.hide();
    },
    show : function() {
        if(this.showing==false) {
            this.showing = true;
            this.element.show();
        }
    },
    hide : function() {
      this.showing = false;
      this.element.hide();
    },
    move : function(top,left) {
        this.show();
        new Poker.TransformAnimation(this.element).addTranslate3d(left,top,0,"px").start(this.animationManager);
    }
});
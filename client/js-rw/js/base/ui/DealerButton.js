"use strict";
var Poker = Poker || {};
Poker.DealerButton = Class.extend({
    element : null,
    showing : false,
    cssAnimator : null,
    init : function() {
        this.cssAnimator = new Poker.CSSAnimator();
        this.element = $("#dealerButton");
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
        var self = this;
        setTimeout(function(){
            self.cssAnimator.addTransform(self.element.get(0),"translate3d("+left+"px,"+top+"px,0)");
        },50);
    }
});
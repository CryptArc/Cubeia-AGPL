"use strict";
var Poker = Poker || {};

Poker.ViewSwiper = Class.extend({
    startXPos : 0,
    centerElement : null,
    leftElement : null,
    rightElement : null,
    cssAnimator : null,
    nextCallback : null,
    previousCallback : null,
    switchNext : false,
    switchPrevious : false,
    setElements : function(left,center,right) {
        this.leftElement = left;

        this.centerElement = center;

        this.rightElement = right;

    },
    init : function(swipeElement,nextCallback,previousCallback) {
        var self = this;
        this.cssAnimator = new Poker.CSSAnimator();
        this.nextCallback = nextCallback;
        this.previousCallback = previousCallback;
        swipeElement.bind("touchstart",function(e){
            if(e.originalEvent.touches.length==1){

                var touch = e.originalEvent.touches[0];
                self.start(touch.pageX);
                self.switchNext=false;
                self.switchPrevious=false;
            }
        });
        swipeElement.bind("touchmove",function(e){
            if(e.originalEvent.touches.length==1){
                e.preventDefault();
                var touch = e.originalEvent.touches[0];
                var moveX = touch.pageX-self.startXPos;
                if(moveX>0) {
                    self.moveRight(moveX);
                } else {
                    self.moveLeft(-moveX);
                }
            }
        });
        swipeElement.bind("touchend",function(e){

            if(this.switchNext==true) {
                this.nextCallback();
            } else if(this.switchPrevious==true) {
                this.previousCallback();
            }

            var left = self.leftElement.width();
            var right = self.rightElement.width();
            if(self.leftElement.attr("id")!=self.centerElement.attr("id")) {
                self.leftElement.attr("style",self.cssAnimator.createTransformString(["translate3d(-"+left+"px,0,0)"],"center"));
            }
            if(self.rightElement.attr("id")!=self.centerElement.attr("id")) {
                self.rightElement.attr("style",self.cssAnimator.createTransformString(["translate3d("+right+"px,0,0)"],"center"));
            }

            var trans = self.cssAnimator.createTransformString(["translate3d(0,0,0)"],"center");
            self.centerElement.attr("style",trans);
        });
    },
    start : function(x) {
        this.startXPos = x;
    },
    moveLeft : function(distance) {
        if(distance>(this.centerElement.width()/3)) {
            this.switchPrevious = true;
        } else {
            this.switchPrevious = false;
        }
        var transform = this.cssAnimator.createTransformString(["translate3d(-"+distance+"px,0,0)"],"center");
        this.centerElement.attr("style",transform);
        if(this.rightElement.attr("id")!=this.centerElement.attr("id")){
            var pos = this.rightElement.width() - distance;
            var rt = this.cssAnimator.createTransformString(["translate3d("+pos+"px,0,0)"],"center");
            this.rightElement.show().attr("style",rt);
        }
    },
    moveRight : function(distance) {
        if(distance>(this.centerElement.width()/3)) {
            this.switchNext = true;
        } else {
            this.switchNext = false;
        }
        var transform = this.cssAnimator.createTransformString(["translate3d("+distance+"px,0,0)"],"center");
        this.centerElement.attr("style",transform);
        if(this.leftElement.attr("id")!=this.centerElement.attr("id")) {
            var pos = -this.leftElement.width()+distance;
            var rt = this.cssAnimator.createTransformString(["translate3d("+pos+"px,0,0)"],"center");
            this.leftElement.show().attr("style",rt);
        }

    }

});
"use strict";
var Poker = Poker || {};

Poker.ViewSwiper = Class.extend({
    startXPos : 0,
    centerElement : null,
    leftElement : null,
    leftStyle : null,
    centerStyle : null,
    rightStyle : null,
    rightElement : null,
    cssAnimator : null,
    nextCallback : null,
    previousCallback : null,
    completeRight : false,
    completeLeft: false,
    moved : false,
    animationManager : null,

    init : function(swipeElement,nextCallback,previousCallback) {
        var self = this;
        this.cssAnimator = new Poker.CSSAnimator();
        this.nextCallback = nextCallback;
        this.previousCallback = previousCallback;
        swipeElement.bind("touchstart",function(e){

            if(e.originalEvent.touches.length==1){
                this.moved = false;
                console.log("start");
                var touch = e.originalEvent.touches[0];
                self.start(touch.pageX);
                self.switchNext=false;
                self.switchPrevious=false;
            }
        });
        swipeElement.bind("touchmove",function(e){
            if(e.originalEvent.touches.length==1){
                var touch = e.originalEvent.touches[0];
                var moveX = touch.pageX-self.startXPos;
                e.preventDefault();
                if(Math.abs(moveX)>0) {
                    this.moved = true;
                }
                if(moveX>0) {
                    self.moveRight(moveX);
                } else if(moveX<0){
                    self.moveLeft(-moveX);
                }
            }
        });
        swipeElement.bind("touchend",function(e){
                if(this.moved==true) {
                    console.log("ending");
                    self.end();
                }

        });
    },
    setElements : function(left,center,right) {

        console.log(left);
        console.log(center);
        console.log(right);
        this.called = false;
        this.animationManager = new Poker.AnimationManager();
        this.leftElement = left!=null ? left.viewElement : null;
        this.centerElement = center!=null ? center.viewElement : null;
        this.rightElement = right!=null ? right.viewElement : null;

        if(this.leftElement!=null) {
            this.leftStyle = this.leftElement.show().attr("style");
            this.leftElement.hide();
        } else {
            this.leftStyle = "";
        }
        if(this.centerElement!=null) {
            console.log(this.centerElement);
            var self = this;
            setTimeout(function(){
                self.centerStyle = self.centerElement.attr("style");
            },50);
        } else {
            this.centerStyle = "";
        }

        if(this.rightElement!=null) {
            this.rightStyle  = this.rightElement.show().attr("style");
            this.rightElement.hide();
        } else {
            this.rightStyle = "";
        }

    },
    end : function() {
        this.startXPos = 0;
        if(this.completeRight==true) {
            this.completeRight=false;
            this.finishRight();
            return;
        } else if(this.completeLeft==true) {
            this.completeLeft=false;
            this.finishLeft();
            return;
        }


        this.setRightStyle("");
        if(this.rightElement!=null) {
            this.rightElement.hide();
        }
        this.setCenterStyle("");
        this.setLeftStyle("");
        if(this.leftElement!=null) {
            this.leftElement.hide();
        }
        return;
    },
    reset : function() {
        this.setRightStyle("");
        this.setLeftStyle("");
        this.setCenterStyle("");
    },
    finishRight : function() {
        console.log("FINNISH RIGHT");
        var self = this;
        new Poker.TransformAnimation(this.leftElement).
            addDefaultStyle(this.leftStyle).
            addTransition("transform",0.5,"ease-out").
            addTransform("translate3d(0,0,0)").
            start(this.animationManager);



        var anim = new Poker.TransformAnimation(this.centerElement).
            addDefaultStyle(this.centerStyle).
            addTransition("transform",0.5,"ease-out").
            addTransform("translate3d("+this.centerElement.width()+"px,0,0)");

        setTimeout(function(){self.rightCallBack();},600);

        anim.start(this.animationManager);

    },
    called : false,
    rightCallBack : function() {
        console.log("right callback");
        this.called = true;
        this.reset();
        this.previousCallback();

    },
    finishLeft : function() {
        var self = this;
        console.log("FINISH LEFT");

        new Poker.TransformAnimation(this.centerElement).
            addDefaultStyle(this.centerStyle).
            addTransition("transform",0.5,"ease-out").
            addTransform("translate3d(-"+this.centerElement.width()+"px,0,0)").
            start(this.animationManager);

        setTimeout(function(){self.rightCallBack();},600);

        new Poker.TransformAnimation(this.rightElement).
            addDefaultStyle(this.rightStyle).
            addTransition("transform",0.5,"ease-out").
            addTransform("translate3d(0,0,0)").
            start(this.animationManager);
    },
    start : function(x) {
        this.startXPos = x;
    },
    moveLeft : function(distance) {
        var transform = this.cssAnimator.createTranslatePx(-distance,0,0);
        this.setCenterStyle(transform);
        if(this.rightElement==null) {
            return;
        }
        if(distance>(this.centerElement.width()/3)) {
            this.completeLeft = true;
        } else {
            this.completeLeft  = false;
        }

        if(this.rightElement.attr("id")!=this.centerElement.attr("id")){
            var pos = this.rightElement.width() - distance;
            this.rightElement.show();
            this.setRightStyle(this.cssAnimator.createTranslatePx(pos,0,0));

        }
    },
    setRightStyle : function(str) {
        if(this.rightElement!=null) {
            this.rightElement.attr("style",this.rightStyle + " "+ str);
        }
    },
    setLeftStyle : function(str) {
        if(this.leftElement!=null) {
            this.leftElement.attr("style",this.leftStyle + " "+ str);
        }
    },
    setCenterStyle : function(str) {
        if(this.centerElement!=null) {
            this.centerElement.attr("style",this.centerStyle + " "+ str);
        }
    },
    moveRight : function(distance) {

        var transform = this.cssAnimator.createTranslatePx(distance,0,0);
        this.setCenterStyle(transform);
        if(this.leftElement==null) {
            return;
        }
        if(distance>(this.centerElement.width()/3)) {
            this.completeRight = true;
        } else {
            this.completeLeft= false;
        }

        if(this.leftElement.attr("id")!=this.centerElement.attr("id")) {
            var pos = -this.leftElement.width()+distance;
            this.leftElement.show();
            this.setLeftStyle(this.cssAnimator.createTranslatePx(pos,0,0));

        }

    }

});
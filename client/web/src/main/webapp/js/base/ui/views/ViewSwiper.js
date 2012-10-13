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
    completeRight : false,
    completeLeft: false,
    moved : false,
    animationManager : null,
    transitioning : false,

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
                if(self.transitioning==true) {
                    return;
                }
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
                    self.end();
                }

        });
    },
    setElements : function(left,center,right) {

        this.called = false;
        this.animationManager = new Poker.AnimationManager();
        this.leftElement = left!=null ? left.viewElement : null;
        this.centerElement = center!=null ? center.viewElement : null;
        this.rightElement = right!=null ? right.viewElement : null;

        if(this.leftElement!=null) {
            this.cssAnimator.setTranslate3dPx(this.leftElement,-this.centerElement.width(),0,0);
        }
        if(this.centerElement!=null) {
            this.cssAnimator.setTranslate3dPx(this.centerElement,0,0,0);
        }

        if(this.rightElement!=null) {
            this.cssAnimator.setTranslate3dPx(this.rightElement,this.centerElement.width(),0,0);
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

        this.moveToOriginalPositions();

    },
    moveToOriginalPositions : function() {
        new Poker.TransformAnimation(this.centerElement).
            addTransition("transform",0.2,"ease-out").
            addTranslate3dPx(0,0,0).
            start(this.animationManager);


        if(this.rightElement!=null) {
            new Poker.TransformAnimation(this.rightElement).
                addTransition("transform",0.2,"ease-out").
                addTranslate3dPx(this.centerElement.width(),0,0).
                start(this.animationManager);
        }

        if(this.leftElement!=null) {
            new Poker.TransformAnimation(this.leftElement).
                addTransition("transform",0.2,"ease-out").
                addTranslate3dPx(-this.centerElement.width(),0,0).
                start(this.animationManager);
        }
    },
    reset : function() {
        this.cssAnimator.clear(this.leftElement);
        this.cssAnimator.clear(this.rightElement);
        this.cssAnimator.clear(this.centerElement);
    },
    finishRight : function() {
        var self = this;
        this.transitioning = true;
        new Poker.TransformAnimation(this.leftElement).
            addTransition("transform",0.5,"ease-out").
            addTransform("translate3d(0,0,0)").
            start(this.animationManager);



        var anim = new Poker.TransformAnimation(this.centerElement).
            addCallback(function(){self.rightCallBack();}).
            addTransition("transform",0.5,"ease-out").
            addTransform("translate3d("+this.centerElement.width()+"px,0,0)");



        anim.start(this.animationManager);

    },
    called : false,
    rightCallBack : function() {
        this.transitioning = false;
        this.reset();
        this.previousCallback();

    },
    leftCallBack : function() {
        this.transitioning = false;
        this.reset();
        this.nextCallback();

    },
    finishLeft : function() {
        var self = this;
        this.transitioning = true;

        new Poker.TransformAnimation(this.centerElement).
            addTransition("transform",0.5,"ease-out").
            addTransform("translate3d(-"+this.centerElement.width()+"px,0,0)").
            addCallback(function(){self.leftCallBack()}).
            start(this.animationManager);

        new Poker.TransformAnimation(this.rightElement).
            addTransition("transform",0.5,"ease-out").
            addTransform("translate3d(0,0,0)").
            start(this.animationManager);
    },
    start : function(x) {
        this.startXPos = x;
    },
    moveLeft : function(distance) {
        if(this.rightElement==null ) {
            distance = Math.min(distance,this.centerElement.width()/6);
        }
        this.cssAnimator.setTranslate3dPx(this.centerElement,-distance,0,0);
        if(this.rightElement==null) {
            return;
        }
        if(distance>(this.centerElement.width()/3)) {
            this.completeLeft = true;
            this.completeRight = false;
        } else {
            this.completeLeft  = false;
        }

        if(this.rightElement.attr("id")!=this.centerElement.attr("id")){
            var pos = this.rightElement.width() - distance;
            this.rightElement.show();
            this.cssAnimator.setTranslate3dPx(this.rightElement,pos,0,0);

        }
    },
    moveRight : function(distance) {
        if(this.leftElement==null ) {
            distance = Math.min(distance,this.centerElement.width()/6);
        }
        this.cssAnimator.setTranslate3dPx(this.centerElement,distance,0,0);

        if(this.leftElement==null) {
            return;
        }
        if(distance>(this.centerElement.width()/3)) {
            this.completeRight = true;
            this.completeLeft=false;
        } else {
            this.completeLeft= false;
        }

        if(this.leftElement.attr("id")!=this.centerElement.attr("id")) {
            var pos = -this.leftElement.width()+distance;
            this.leftElement.show();
            this.cssAnimator.setTranslate3dPx(this.leftElement,pos,0,0);

        }

    }

});
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
    running : false,
    swiped : false,
    animationManager : null,
    transitioning : false,
    minSwipe : 6,

    init : function(swipeElement,nextCallback,previousCallback) {
        var self = this;
        this.cssAnimator = new Poker.CSSAnimator();
        this.nextCallback = nextCallback;
        this.previousCallback = previousCallback;
        swipeElement.bind("touchstart",function(e){
            if(!Poker.Settings.isEnabled(Poker.Settings.Param.SWIPE_ENABLED)){
                return;
            }
            if(e.originalEvent.touches.length==1 && self.running==false){
                self.running = true;
                var touch = e.originalEvent.touches[0];
                self.start(touch.pageX);
                self.switchNext=false;
                self.switchPrevious=false;
            }
        });
        swipeElement.bind("touchmove",function(e){

            if(e.originalEvent.touches.length==1 && self.running==true){
                var touch = e.originalEvent.touches[0];
                var moveX = touch.pageX-self.startXPos;

                if(Math.abs(moveX)>5) {
                    e.preventDefault();
                    self.swiped = true;
                } else {
                    return;
                }
                if(moveX>0) {
                    self.moveRight(moveX);
                } else if(moveX<0){
                    self.moveLeft(-moveX);
                }
            }
        });
        swipeElement.bind("touchend",function(e){
            if(self.running==true && self.swiped == true) {
                self.end();
                self.swiped == false;

            }

        });
    },
    setElements : function(left,center,right) {
        this.animationManager = new Poker.AnimationManager();
        this.leftElement = left!=null ? left.viewElement : null;
        this.centerElement = center!=null ? center.viewElement : null;
        this.rightElement = right!=null ? right.viewElement : null;

        if(this.leftElement!=null) {
            this.cssAnimator.clearTransition(this.leftElement);
            this.cssAnimator.setTranslate3dPx(this.leftElement,-this.centerElement.width(),0,0);
        }

        if(this.rightElement!=null) {
            this.cssAnimator.clearTransition(this.rightElement);
            this.cssAnimator.setTranslate3dPx(this.rightElement,this.centerElement.width(),0,0);
        }

        if(this.centerElement!=null) {
            this.cssAnimator.clearTransition(this.centerElement);
            this.cssAnimator.setTranslate3dPx(this.centerElement,0,0,0);
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

        //when there was no swipe, transition views back to 0
        this.moveToOriginalPositions();

    },
    moveToOriginalPositions : function() {
        var self = this;
        new Poker.TransformAnimation(this.centerElement).
            addTransition("transform",0.2,"ease-out").
            addCallback(function(){self.running=false;}).
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
        new Poker.TransformAnimation(this.leftElement).
            addTransition("transform",0.5,"ease-out").
            addTranslate3dPx(0,0,0).
            start(this.animationManager);

        new Poker.TransformAnimation(this.centerElement).
            addCallback(function(){self.rightCallBack();}).
            addTransition("transform",0.5,"ease-out").
            addTranslate3dPx(this.centerElement.width(),0,0).
            start(this.animationManager);


    },
    rightCallBack : function() {
        this.running=false;
        this.previousCallback();
    },
    leftCallBack : function() {
        this.running=false;
        this.nextCallback();


    },
    finishLeft : function() {
        var self = this;
        this.transitioning = true;

        new Poker.TransformAnimation(this.centerElement).
            addTransition("transform",0.5,"ease-out").
            addTranslate3dPx(-this.centerElement.width(),0,0).
            addCallback(function(){self.leftCallBack()}).
            start(this.animationManager);

        new Poker.TransformAnimation(this.rightElement).
            addTransition("transform",0.5,"ease-out").
            addTranslate3dPx(0,0,0).
            start(this.animationManager);
    },
    start : function(x) {
        this.startXPos = x;
    },
    moveLeft : function(distance) {
        if(this.rightElement==null ) {
            distance = Math.min(distance,this.centerElement.width()/this.minSwipe);
        }
        this.cssAnimator.setTranslate3dPx(this.centerElement,-distance,0,0);
        if(this.rightElement==null) {
            return;
        }
        if(distance>(this.centerElement.width()/this.minSwipe)) {
            this.completeLeft = true;
            this.completeRight = false;
        } else {
            this.completeLeft  = false;
        }

        var pos = this.rightElement.width() - distance;
        this.rightElement.show();
        this.cssAnimator.setTranslate3dPx(this.rightElement,pos,0,0);
    },
    moveRight : function(distance) {
        if(this.leftElement==null ) {
            distance = Math.min(distance,this.centerElement.width()/this.minSwipe);
        }
        this.cssAnimator.setTranslate3dPx(this.centerElement,distance,0,0);

        if(this.leftElement==null) {
            return;
        }
        if(distance>(this.centerElement.width()/this.minSwipe)) {
            this.completeRight = true;
            this.completeLeft=false;
        } else {
            this.completeLeft= false;
        }

        var pos = -this.leftElement.width()+distance;
        this.leftElement.show();
        this.cssAnimator.setTranslate3dPx(this.leftElement,pos,0,0);

    }

});
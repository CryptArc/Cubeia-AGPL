var Poker = Poker || {};

Poker.AnimationManager = Class.extend({
    cssAnimator : null,
    init : function() {
        this.cssAnimator = new Poker.CSSAnimator();
    },
    animate : function(animation,delay) {
        var self = this;
        animation.build();
        if(typeof(delay) == "undefined") {
            delay = 50;
        }
        this.cssAnimator.removeTransitionCallback(animation.element);
        animation.prepare();

        this.cssAnimator.addTransitionCallback(animation.element,
            function(){
                if(animation.callback!=null) {
                    animation.callback();
                }
                if(animation.nextAnimation!=null)  {
                    self.animate(animation.nextAnimation,0);
                }
            });
        if(delay==0) {
            animation.animate();
        } else {
            setTimeout(function(){
                animation.animate();
            },delay);
        }


    }
});
Poker.AnimationManager = new Poker.AnimationManager();

Poker.Animation = Class.extend({
    element : null,
    callback : null,
    nextAnimation : null,
    init : function(element) {
        if(typeof(element)=="undefined") {
            throw "Poker.Animation requires an element";
        }
        if(element.length) {
           element = element.get(0);
        }
        this.element = element;
    },
    addCallback : function(callback) {
        this.callback = callback;
        return this;
    },
    prepare : function() {

    },
    animate : function() {

    },
    next : function(el) {

    },
    build : function() {

    },
    start : function() {
       Poker.AnimationManager.animate(this);
    }
});
Poker.CSSClassAnimation = Poker.Animation.extend({
    classNames : null,
    init : function(element) {
        this._super(element);
        this.classNames = [];
    },
    addClass : function(className) {
        this.classNames.push(className);
        return this;
    },
    animate : function() {
        var el =  $(this.element);
        for(var i = 0; i<this.classNames.length; i++) {
            el.addClass(this.classNames[i]);
        }
    },
    next : function(el) {
        this.nextAnimation = new Poker.CSSClassAnimation(el || this.element);
        return this.nextAnimation;
    }
});

Poker.TransformAnimation = Poker.Animation.extend({
    transform : null,
    transition : null,
    origin : null,
    transitionStr : null,
    transformStr : null,

    init : function(element) {
        this._super(element);
    },
    prepare : function() {
        if(this.transitionStr!=null) {
            this.element.style.cssText = this.transitionStr;
        }
    },
    animate : function() {
        if(this.transformStr!=null) {
            this.element.style.cssText+=this.transformStr;
        }
    },
    addTransform : function(transform) {
        this.transform = transform;
        return this;
    },
    addTransition : function(property, time, easing) {
        this.transition = property + " " + time + "s " + easing;
        return this;
    },
    next : function(el) {
        this.nextAnimation = new Poker.TransformAnimation(el || this.element);
        return this.nextAnimation;
    },
    addOrigin : function(origin) {
        this.origin = origin;
        return this;
    },
    build : function() {
        var cssAnimator = new Poker.CSSAnimator();
        if(this.transition!= null ) {
            this.transitionStr = cssAnimator.createTransitionString([this.transition]);
        }
        if(this.transform!=null) {
            this.transformStr = cssAnimator.createTransformString([this.transform],this.origin);
        }
        if(this.nextAnimation!=null) {
            this.nextAnimation.build();
        }
    }
});
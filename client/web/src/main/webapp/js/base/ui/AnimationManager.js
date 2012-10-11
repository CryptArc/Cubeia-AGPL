var Poker = Poker || {};

Poker.AnimationManager = Class.extend({
    cssAnimator : null,
    active : true,
    pendingAnimations : null,
    currentAnimations : null,
    currentId : 0,
    init : function() {
        this.pendingAnimations = [];
        this.currentAnimations = [];
        this.cssAnimator = new Poker.CSSAnimator();
    },
    activate : function() {
        this.active = true;
        for(var x in this.pendingAnimations) {
            this.animate(this.pendingAnimations[x]);
        }
        this.pendingAnimations = [];
    },
    removeAnimation : function(animation) {
        this.removeCurrentAnimation(animation);
        this.removePendingAnimation(animation);
    },
    removePendingAnimation : function(animation) {
        for(var i = 0; i<this.pendingAnimations.length; i++) {
            if(this.pendingAnimations[i].id == animation.id){
                this.pendingAnimations.splice(i,1);
                break;
            }
        }
    },
    removeCurrentAnimation : function(animation) {
        for(var i = 0; i<this.currentAnimations.length;i++) {
            if(this.currentAnimations[i].id == animation.id){
                this.currentAnimations.splice(i,1);
                break;
            }
        }
    },
    /**
     * Executes an animation
     * @param animation
     * @param delay
     */
    animate : function(animation,delay) {
        var self = this;
        if(animation.id == null) {
            animation.id = this.nextId();
        }
        //if it's a timed animation (time sensitive) and the animation manager isn't active
        //  we need to store it for later activation
        if(this.active === false && animation.timed === true ) {
            this.pendingAnimations.push(animation);
            return;
        }
        //if it's a timed animation we're about to start we need to keep track of it
        //since it might get inactive when switching views
        if(animation.timed === true ) {
            this.currentAnimations.push(animation);
        }
        //build transition and transform strings and add
        animation.build();

        if(typeof(delay) == "undefined") {
            delay = 50;
        }
        //be sure it's no crap
        this.cssAnimator.removeTransitionCallback(animation.element);

        //add the transition properties to the element
        animation.prepare();

        //setup the animation callbacks
        this.cssAnimator.addTransitionCallback(animation.element,
            function(){
                if(animation.timed==true) {
                    self.removeCurrentAnimation(animation);
                }
                if(animation.callback!=null) {
                    animation.callback();
                }
                if(animation.nextAnimation!=null)  {
                    self.animate(animation.nextAnimation,0);
                }

            });
        //if the animation manager is NOT active (view not showing)
        if(this.active==false) {
            animation.animate(); //add the transforms right away
            if(animation.callback!=null) {
                animation.callback();  // since it's not active execute callback
            }
            if(animation.nextAnimation!=null) {
                this.animate(animation.nextAnimation,0);
            }

        } else if(delay==0) {
            animation.animate();
        } else {
            setTimeout(function(){
                animation.animate();
            },delay);
        }
    },
    setActive : function(active) {
        this.active = active;
        if(active===true) {
            this.activate();
        } else {
            this.deactivate();
        }
    },
    deactivate : function() {
        for(var x in this.currentAnimations) {
            this.cssAnimator.removeTransitionCallback(this.currentAnimations[x].element);
            this.currentAnimations[x].cancel();
            this.pendingAnimations.push(this.currentAnimations[x]);
        }
        this.currentAnimations = [];
    },
    nextId : function() {
        return this.currentId++;
    }
});


Poker.Animation = Class.extend({
    id : null,
    element : null,
    callback : null,
    nextAnimation : null,
    timed : false,
    init : function(element) {
        if(typeof(element)=="undefined") {
            throw "Poker.Animation requires an element";
        }
        if(typeof(element.length)!="undefined" && element.length==0) {
            throw "Poker.Animation requires an element";
        } else if(typeof(element.length)!="undefined") {
            element = element.get(0);
        }

        this.element = element;
    },
    cancel : function () {
        this.element.style.cssText = "";
    },
    setTimed : function(timed) {
        this.timed = timed;
        return this;
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
    start : function(animationManager) {

        animationManager.animate(this);
        return this;

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
    transitionTime : 0,
    transitionProperty : null,
    transitionEasing : null,
    origin : null,
    transitionStr : null,
    transformStr : null,
    created : null,
    init : function(element) {
        this._super(element);
        this.created = new Date().getTime();
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
        this.transitionProperty = property;
        this.transitionTime = time;
        this.transitionEasing = easing;
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
    getCalculatedTransitionTime : function() {
        if(this.timed === true) {
            var now = new Date().getTime();
            var timeLeft = (this.transitionTime - (now-this.created)/1000);
            if(timeLeft<0){
                timeLeft=0;
            }
            return timeLeft;
        } else {
            return this.transitionTime;
        }

    },
    build : function() {
        var cssAnimator = new Poker.CSSAnimator();
        if(this.transitionProperty!= null ) {
            var t = this.transitionProperty + " " + this.getCalculatedTransitionTime() + "s " + this.transitionEasing;
            this.transitionStr = cssAnimator.createTransitionString([t]);
        }
        if(this.transform!=null) {
            this.transformStr = cssAnimator.createTransformString([this.transform],this.origin);
        }
        if(this.nextAnimation!=null) {
            this.nextAnimation.build();
        }
    }
});
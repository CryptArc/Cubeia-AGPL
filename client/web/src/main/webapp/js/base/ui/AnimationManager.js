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
        // if it's a timed animation (time sensitive) and the animation manager isn't active
        // we need to store it for later activation
        if(this.active === false && animation.timed === true ) {
            this.pendingAnimations.push(animation);
            return;
        }

        //if it's a timed animation we're about to start we need to keep track of it
        //since it might get inactive when switching views
        var timeLeft = animation.getRemainingTime()>0;
        if(animation.timed === true && timeLeft) {
            this.currentAnimations.push(animation);
        } else if(animation.timed === true && !timeLeft) {
            //callback();
            //return;
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
        this.cssAnimator.addTransitionCallback(animation.element,function(){
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
    startTime : null,
    transitionTime : 0,
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
        this.startTime = new Date().getTime();
    },
    getRemainingTime : function() {
        if(this.timed === true) {
            var now = new Date().getTime();

            var timeLeft = (this.transitionTime - (now-this.startTime)/1000);

            if(timeLeft<0){
                timeLeft=0;
            }
            return timeLeft;
        } else {
            return this.transitionTime;
        }

    },
    cancel : function () {
        var cssAnimator = new Poker.CSSAnimator();
        cssAnimator.clear(this.element);
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
Poker.Transform = Class.extend({
    scaleVal : null,
    rotateVal : null,
    translateVal : null,

    init : function(){

    },
    getScale : function() {
      return this.scaleVal;
    },
    scale : function(x,y,z) {
        this.scaleVal = {x:x,y:y,z:z};
    },
    getRotate : function( ){
        return this.rotateVal;
    },
    rotate : function(angle) {
        this.rotateVal = angle;
    },
    translate : function(x,y,z,unit) {
        if(unit==null) {
            unit = "%";
        }
        this.translateVal = {x:x,y:y,z:z,unit:unit};
    },
    getTranslate : function() {
        return this.translateVal;
    }
});

Poker.TransformAnimation = Poker.Animation.extend({
    transform : null,
    startTransform : null,
    transitionProperty : null,
    transitionEasing : null,
    origin : null,

    cssAnimator : null,
    init : function(element) {
        this._super(element);

        this.cssAnimator = new Poker.CSSAnimator();
        this.transform = new Poker.Transform();
        this.startTransform = new Poker.Transform();
        this.startTransform.scale(1,1,1);
        this.startTransform.rotate(0);
        this.startTransform.translate(0,0,0);
    },

    prepare : function() {
        if(this.timed==true) {
            this.setTimedStartTransform();
        }
        if(this.transitionProperty!=null) {
            this.cssAnimator.addTransition(this.element,this.getRemainingTime());
        }
    },
    setTimedStartTransform : function() {
        var currentTransitionTime = this.getRemainingTime();
        var originalTransitionTime = this.transitionTime;
        var remaining = currentTransitionTime / originalTransitionTime;
        var transform = "";

        if(this.transform.getScale()!=null) {
            var s = this.transform.getScale();
            var start = this.startTransform.getScale();
            var x = (start.x - s.x) * remaining + s.x;
            var y = (start.y - s.y) * remaining + s.y;
            var z = (start.z - s.z) * remaining + s.z;
            this.cssAnimator.setScale3d(this.element,x,y,z,this.origin);

        } else if(this.transform.getTranslate()!=null) {

            var t = this.transform.getTranslate();
            var start = this.startTransform.getScale();
            var x = (start.x - t.x) * remaining + t.x;
            var y = (start.y - t.y) * remaining + t.y;
            var z = (start.z - t.z) * remaining + t.z;
            this.cssAnimator.setTranslate3d(this.element,x,y,z,start.unit,this.origin);

        } else if(this.transform.getRotate()!=null) {
            var r = this.transform.getRotate();
            var start = this.startTransform.getRotate();
            var angle = (start - r) * remaining + r;
            this.cssAnimator.setRotate(this.element,angle);
        }

    },
    animate : function() {
        if(this.getTransform()!=null) {
           this.cssAnimator.addTransform(this.element,this.getTransform(),this.origin);
        }
    },
    addTransform : function(transform) {
        this.transform = transform;
        return this;
    },

    getTransform : function() {
        var transform = "";
        if(this.transform.getScale()!=null) {
            var s = this.transform.getScale();
            transform+= this.cssAnimator.toScale3dString(s.x, s.y, s.z);
        }
        if(this.transform.getTranslate()!=null) {
            var t = this.transform.getTranslate();
            transform+=this.cssAnimator.toTranslate3dString(t.x, t.y, t.z, t.unit);
        }
        if(this.transform.getRotate()!=null){
            transform+=this.cssAnimator.toRotateString(this.transform.getRotate());
        }

        if(transform=="") {
            return null;
        } else {
            return transform;
        }
    },
    addStartScale : function(x,y,z) {
        this.startTransform.scale(x,y,x);
        return this;
    },
    addStartRotate : function(angle) {
       this.startTransform.rotate(angle);
        return this;
    },
    addStartTranslate : function(x,y,z,unit) {
        this.startTransform.translate(x,y,z,unit);
        return this;
    },
    addScale3d : function(x,y,z) {
        this.transform.scale(x,y,z);
        return this;
    },
    addTranslate3d : function(x,y,z,unit){
        this.transform.translate(x,y,z,unit);
        return this;
    },
    addRotate : function(angle) {
        this.transform.rotate(angle);
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
        this.nextAnimation.startTime = this.nextAnimation.startTime + this.transitionTime*1000;
        return this.nextAnimation;
    },
    addOrigin : function(origin) {
        this.origin = origin;
        return this;
    },
    getCalculatedTransition : function() {
        return this.transitionProperty + " " + this.getRemainingTime() + "s " + this.transitionEasing;
    },

    build : function() {
        if(this.nextAnimation!=null) {
            this.nextAnimation.build();
        }
    }
});
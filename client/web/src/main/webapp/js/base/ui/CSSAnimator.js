"use strict";
var Poker = Poker || {};

Poker.CSSAnimator = Class.extend({
    prefix : ["-moz-","-webkit-","-o-", ""],
    jsPropertyPrefix : ["moz","webkit","o",""],
    addTransition : function(el,transition) {
        if(typeof(el)=="undefined") {
            return;
        }
        el = this.getElement(el);
        for(var i = 0; i<this.prefix.length;i++){
            if(typeof(el.style[this.prefix[i]+"transition"])!="undefined"){
                el.style[this.prefix[i]+"transition"] = this.prefix[i] + transition;
            }
        }
    },
    clear : function(el) {
        this.clearTransition(el);
        this.clearTransform(el);
    },
    clearTransform : function(el) {
        if(el == null) {
            return;
        }
        el = this.getElement(el);
        for(var i = 0; i<this.prefix.length; i++) {

            if(typeof(el.style[this.prefix[i]+"transform"])!="undefined") {
                el.style[this.prefix[i]+"transform"] = "";
            }
            if(typeof(el.style[this.prefix+"transform-origin"])!="undefined") {
                el.style[this.prefix[i]+"transform-origin"]= "";
            }

        }
    },
    clearTransition : function(el){
        if(el == null) {
            return;
        }
        el = this.getElement(el);
        for(var i = 0; i<this.prefix.length; i++) {
            var property = this.prefix[i]+"transition";
            if(typeof(el.style[property])!="undefined"){
                el.style[property] = "";
            }
        }
    },
    toScale3dString : function(x,y,z) {
        return "scale3d("+x+","+y+","+z+")";
    },
    toTranslate3dString : function(x,y,z,unit) {
        return "translate3d("+this.withUnit(x,unit)+","+this.withUnit(y,unit)+","+this.withUnit(z,unit)+")";
    },
    withUnit : function(val,unit)  {
        if(val!=0) {
            if(unit == "px") {
                val = Math.round(val);
            }
            return val+unit;
        } else {
            return 0;
        }

    },
    toRotateString : function(angle) {
        return "rotate("+angle+"deg)";
    },
    setScale3d : function(el,x,y,z,orig) {
        if(typeof(el)=="undefined") {
            return;
        }
        if(typeof(orig)=="undefined") {
            orig = "center";
        }

        this.addTransform(el,this.toScale3dString(x,y,z),orig);
    },
    setRotate : function(el,angle) {
        if(typeof(el)=="undefined") {
            return;
        }
        this.addTransform(el,this.toRotateString(angle));
    },
    setTranslate3d : function(el,x,y,z,unit,orig) {
        if(typeof(el)=="undefined") {
            return;
        }
        if(typeof(orig)=="undefined") {
            orig = "center";
        }

        this.addTransform(el,this.toTranslate3dString(x,y,z,unit),orig);
    },
    addTransform : function(el,transform,origin)  {
        if(!el || !transform) {
            throw "Poker.CSSAnimator: Illegal argument, element and transforms must be set";
        }
        el = this.getElement(el);
        for(var i = 0; i<this.prefix.length; i++) {

            if(typeof(el.style[this.prefix[i]+"transform"])!="undefined") {
                el.style[this.prefix[i]+"transform"]=transform;
            }
            if(origin!=null && typeof(el.style[this.prefix[i]+"transform-origin"])!="undefined") {
                el.style[this.prefix[i]+"transform-origin"]=origin;
            }
        }
    },
    getElement : function(el) {
        if(typeof(el.length)!="undefined") {
            return el.get(0);
        }  else {
            return el;
        }

    },
    addTransitionCallback : function(element,func) {
        if(!element || !func) {
           throw "Poker.CSSAnimator: Illegal argument, element and callback function must be set";
        }
        this.removeTransitionCallback(element);
        $(element).bind('webkitTransitionEnd',func);
        $(element).bind('transitionend',func);
        $(element).bind('transitionend',func);
        $(element).bind('msTransitionEnd',func);
    },
    removeTransitionCallback : function(element) {
        $(element).unbind("webkitTransitionEnd");
        $(element).unbind("transitionend");
        $(element).unbind("oanimationend");
        $(element).unbind("msTransitionEnd");


    }
});
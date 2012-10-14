"use strict";
var Poker = Poker || {};

Poker.CSSAnimator = Class.extend({
    prefix : ["-moz-","-webkit-","-o-", ""],
    addTransition : function(el,transition,clear) {
        var arr = new Array();
        arr.push(transition);
        this.addTransitions(el,arr,clear);
    },
    createTransitionString : function(transitions) {
        var transitionsStr = "";
        for(var j = 0; j<this.prefix.length; j++) {
            var p = this.prefix[j];
            transitionsStr+=p+"transition:"
            for(var i=0; i<transitions.length; i++) {
                if(transitions[i].indexOf("transform")==0) {
                    transitionsStr+=p;
                }
                transitionsStr+=transitions;
                if(i!=(transitions.length-1) ) {
                    transitions+=",";
                }
            }
            transitionsStr+=";";
        }
        return transitionsStr;
    },
    addTransitions : function(el,transitions,clear) {

        if(!el || !transitions) {
            throw "Poker.CSSAnimator: Illegal argument, element and transition must be set";
        }
        var transitionsStr = this.createTransitionString(transitions);
        if(clear) {
            el.style.cssText=transitionsStr;
        } else {
            el.style.cssText+=transitionsStr;
        }


    },
    addTransform : function(el,transform,origin) {
        if(!el || !transform) {
            throw "Poker.CSSAnimator: Illegal argument, element and transforms must be set";
        }
        var arr = new Array(transform);
        this.addTransforms(el,arr,origin)
    },
    createTransformString : function(transforms,origin) {
        var transformStr = "";
        for(var j = 0; j<this.prefix.length; j++) {
            var p = this.prefix[j];
            transformStr+=p+"transform:";
            for(var i=0; i<transforms.length; i++) {
                transformStr+=transforms[i];
                transformStr+=" ";
            }
            transformStr+=";";
        }

        if(typeof(origin)!="undefined") {
            for(var i = 0; i<this.prefix.length; i++) {
                var p = this.prefix[i];
                transformStr+=p+"transform-origin:"+origin+";";
            }
        }
        return transformStr;
    },
    addTransforms : function(el,transforms,origin)  {
        if(!el || !transforms) {
            throw "Poker.CSSAnimator: Illegal argument, element and transforms must be set";
        }
        var transformStr = this.createTransformString(transforms,origin);
        el.style.cssText+=transformStr;
    },
    addTransitionCallback : function(element,func) {
        if(!element || !func) {
           throw "Poker.CSSAnimator: Illegal argument, element and callback function must be set";
        }
        element.addEventListener("webkitTransitionEnd", func,false);
        element.addEventListener("transitionend", func,false);
        element.addEventListener("oanimationend", func,false);
        element.addEventListener("msTransitionEnd",func,false);
    },
    removeTransitionCallback : function(element) {
        element.removeEventListener("webkitTransitionEnd");
        element.removeEventListener("transitionend");
        element.removeEventListener("oanimationend");
        element.removeEventListener("msTransitionEnd");
    }
});
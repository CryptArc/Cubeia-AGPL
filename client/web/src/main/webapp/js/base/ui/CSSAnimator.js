"use strict";
var Poker = Poker || {};

Poker.CSSAnimator = Class.extend({
    prefix : ["-moz-","-webkit-","-o-", ""],
    addTransition : function(el,transition,clear) {
        var arr = new Array();
        arr.push(transition);
        this.addTransitions(el,arr,clear);
    },
    addTransitions : function(el,transitions,clear) {

        if(!el || !transitions) {
            throw "Poker.CSSAnimator: Illegal argument, element and transition must be set";
        }
        var transitionsStr = "";
        for(var p in this.prefix) {
            transitionsStr+=this.prefix[p]+"transition:"
            for(var i=0; i<transitions.length; i++) {
                if(transitions[i].trim().indexOf("transform")==0) {
                    transitionsStr+=this.prefix[p];
                }
                transitionsStr+=transitions;
                if(i!=(transitions.length-1) ) {
                    transitions+=",";
                }
            }
            transitionsStr+=";";
        }
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
    addTransforms : function(el,transforms,origin)  {
        if(!el || !transforms) {
            throw "Poker.CSSAnimator: Illegal argument, element and transforms must be set";
        }
        var transformStr = "";
        for(var p in this.prefix) {
            transformStr+=this.prefix[p]+"transform:";
             for(var i=0; i<transforms.length; i++) {
                 transformStr+=transforms[i];
                 transformStr+=" ";
            }
            transformStr+=";";
        }

        if(typeof(origin)!="undefined") {
            for(var p in this.prefix) {
               transformStr+=this.prefix[p]+"transform-origin:"+origin+";";
            }
        }
        el.style.cssText+=transformStr;
    },
    addTransitionCallback : function(element,func) {
        if(!element || !func) {
           throw "Poker.CSSAnimator: Illegal argument, element and callback function must be set"
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
"use strict";
var Poker = Poker || {};

Poker.Utils = {
    currencySymbol : "&euro;",
    formatCurrency : function(amount) {
        return parseFloat(amount/100).toFixed(2);
    },
    formatCurrencyString : function(amount) {
        return Poker.Utils.currencySymbol + Poker.Utils.formatCurrency(amount);
    },
    getCardString : function(gamecard) {
        var ranks = "23456789tjqka ";
        var suits = "cdhs ";
        return ranks.charAt(gamecard.rank) + suits.charAt(gamecard.suit);
    },
    formatBlinds : function(amount) {
        var str = ""+ Poker.Utils.formatCurrency(amount);
        if(str.charAt(str.length-1)=="0") {
            str = str.substr(0,str.length-1);
            if(str.charAt(str.length-1)=="0"){
                str = str.substr(0,str.length-2);
            }
        }
        return str;
    },
    calculateDimensions : function(availableWidth, availableHeight, aspectRatio) {
        var width = availableHeight * aspectRatio;
        var height = availableHeight;
        if(width > availableWidth) {
            height = availableWidth/aspectRatio;
            width = availableWidth;
        }
        return { width : width, height : height};
    },
    /**
     * Calculates the distance in percent for two elements based on
     * a containers dimensions, if the container isn't specified
     * window dimensions will be used
     * @param src
     * @param target
     * @param [targetCenter]
     * @param [srcCenter]
     * @return {Object}
     */
    calculateDistance : function(src,target,targetCenter, srcCenter) {
        var srcOffset = src.offset();
        var targetOffset = target.offset();
        var targetLeft = targetOffset.left;

        if(targetCenter === true) {
            targetLeft = targetLeft + (target.outerWidth()/2);
        }

        var srcLeft = srcOffset.left;
        if(srcCenter === true) {
            srcLeft = srcLeft + (src.outerWidth()/2);
        }

        var leftPx = targetLeft - srcLeft;
        var topPx =  targetOffset.top - srcOffset.top;
        var distLeft = 100 * (leftPx/src.outerWidth());
        var distTop = 100 * (topPx/src.outerHeight());


        return { left : distLeft, top : distTop };

    },
    storeUser : function(username,password) {
        Poker.Utils.store("username",username);
        Poker.Utils.store("password",password);
    },
    removeStoredUser : function() {
        Poker.Utils.remove("username");
        Poker.Utils.remove("password");
    },
    store : function(name,value) {
         var store = Poker.Utils.getStorage();
         if(store!=null) {
            store.removeItem(name);
            store.setItem(name,value);
         }
    },
    remove : function(name) {
        var store = Poker.Utils.getStorage();
        if(store!=null) {
            store.removeItem(name);
        }
    },
    load : function(name,defaultValue) {
        var store = Poker.Utils.getStorage();
        if(store != null) {
            return store.getItem(name);
        } else if (typeof(defaultValue) !== "undefined") {
            return defaultValue;
        } else {
            return null;
        }
    },
    loadBoolean : function(name, defaultValue) {
      var val = Poker.Utils.load(name, defaultValue);
      if (val!=null){
          return val == "true";
      } else if (typeof(defaultValue) !== "undefined") {
        return defaultValue;
      }
      return false;
    },
    getStorage : function() {
        if(typeof(localStorage)!=="undefined") {
            return localStorage;
        }
        else {
            return null;
        }
    }

};
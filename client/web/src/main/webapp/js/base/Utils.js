"use strict";
var Poker = Poker || {};

Poker.Utils = {
    currencySymbol : "&euro;",
    formatCurrency : function(amount) {
        return parseFloat(amount/100).toFixed(2);
    },
    formatCentString : function(str) {
        var length = str.length;
        if(str=="0" || length==0) {
            return "-";
        } else if(length<3) {
            return "0.00".substr(0,(4-length)) + str;
        } else {
            var val =  str.substr(0,length-2)
            var decimals = str.substr(length-2,length);
            if(parseInt(decimals)>0) {
                val = "." + decimals;
            }
            return val;
        }
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
    /**
     * Calculates the distance in percent for two elements based on
     * a containers dimensions, if the container isn't specified
     * window dimensions will be used
     * @param src
     * @param target
     * @return {Object}
     */
    calculateDistance : function(src,target) {
        var srcOffset = src.offset();
        var targetOffset = target.offset();
        var leftPx = targetOffset.left - srcOffset.left;
        var topPx =  targetOffset.top - srcOffset.top;
        var distLeft = 100 * (leftPx/src.width());
        var distTop = 100 * (topPx/src.height());


        return { left : distLeft, top : distTop };

    },
    store : function(name,value) {
         var store = Poker.Utils.getStorage();
         if(store!=null) {
            store.removeItem(name);
            store.setItem(name,value);
         }
    },
    load : function(name,defaultValue) {
        var store = Poker.Utils.getStorage();
        if(store!=null) {
            return store.getItem(name);
        } else if(typeof(defaultValue)!=="undefined") {
            return defaultValue;
        } else {
            return null;
        }
    },
    loadBoolean : function(name,defaultValue) {
      var val = Poker.Utils.load(name,defaultValue);
      if(val!=null){
          return val == "true";
      } else if(typeof(defaultValue)!=="undefined") {
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
"use strict";
var Poker = Poker || {};

Poker.Utils = {
    CURRENCY_SYMBOL : "&euro;",
    formatCurrency : function(amount) {
        return parseFloat(amount/100).toFixed(2);
    },
    formatCurrencyString : function(amount) {
        return Poker.Utils.CURRENCY_SYMBOL + Poker.Utils.formatCurrency(amount);
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
     * Calculates the distance in percent (of the src elements width) between two elements.
     *
     * Used together with css translate transforms.
     *
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
    },
    readParam : function(key,params) {
        for (var i = 0; i < params.length; i++) {
            var object = params[i];

            if (object.key == key) {

                var p = null;
                var valueArray = FIREBASE.ByteArray.fromBase64String(object.value);
                var byteArray = new FIREBASE.ByteArray(valueArray);
                if (object.type == 1) {
                    p = byteArray.readInt();
                } else {
                    p = byteArray.readString();
                }

                //shouldn't this work?
                //  var p =  FIREBASE.Styx.readParam(object);
                return p;
            }
        }
        return null;
    }

};
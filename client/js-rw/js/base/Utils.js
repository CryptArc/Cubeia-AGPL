"use strict";
var Poker = Poker || {};

Poker.Utils = {
    formatCurrency : function(amount) {
        return parseFloat(amount/100).toFixed(2);
    },
    getCardString : function(gamecard) {
        var ranks = "23456789tjqka ";
        var suits = "cdhs ";
        var cardString = ranks.charAt(gamecard.rank) + suits.charAt(gamecard.suit);
        return cardString;
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
    store : function(name,value) {
         var store = Poker.Utils.getStorage();
         if(store!=null) {
            store.removeItem(name);

            store.setItem(name,value);
             console.log("storing " + name + " = " + value);
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
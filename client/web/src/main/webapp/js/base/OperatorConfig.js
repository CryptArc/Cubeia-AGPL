"use strict";
var Poker = Poker || {};

/**
 * @type {Poker.OperatorConfig}
 */
Poker.OperatorConfig = Class.extend({
    operatorId : null,

    /**
     * @type Poker.Map
     */
    configMap : null,

    currencyMap : null,
    enabledCurrencies : null,

    /**
     * @type Boolean
     */
    populated : false,
    init : function() {
        this.configMap = new Poker.Map();
    },
    isPopulated : function() {
        return this.populated;
    },
    createCurrencyMap: function(currencyParam) {
        this.currencyMap = [];
        this.enabledCurrencies = [];
        var currencies = currencyParam.split(",");
        for (var i = 0; i < currencies.length; i++) {
            var keyValuePair = currencies[i].split("=");
            this.currencyMap[keyValuePair[0]] = keyValuePair[1];
            this.enabledCurrencies.push({id : keyValuePair[0], name : keyValuePair[1]});
        }
    },
    populate : function(params) {
        for(var p in params) {
          this.configMap.put(p,params[p]);
        }
        this.createCurrencyMap("EUR=Euro,XCC=XCC");
        this.populated = true;
    },
    getLogoutUrl : function() {
        return this.getValue("LOGOUT_PAGE_URL","");
    },
    getClientHelpUrl : function() {
        return this.getValue("CLIENT_HELP_URL","");
    },

    getProfilePageUrl:function() {
        return this.getValue("PROFILE_PAGE_URL", "http://localhost:8083/player-api/html/profile.html");
    },
    getBuyCreditsUrl : function() {
        return this.getValue("BUY_CREDITS_URL", "http://localhost:8083/player-api/html/buy-credits.html");
    },
    getAccountInfoUrl : function() {
        return this.getValue("ACCOUNT_INFO_URL", "http://localhost:8083/player-api/html/");
    },
    getShareUrl : function() {
        return this.getValue("SHARE_URL", null);
    },
    getEnabledCurrencies : function() {
        return this.enabledCurrencies;
    },
    getCurrencyMap : function() {
        return this.currencyMap;
    },
    getValue : function(param,def) {
        var value =  this.configMap.get(param);
        if(value==null) {
          console.log("Value for param " + param + " not available, returning default " + def);
          value = def;
      }
      return value;
    }
});
Poker.OperatorConfig = new Poker.OperatorConfig();
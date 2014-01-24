"use strict";

var Poker = Poker || {};

Poker.PlayerApi = Class.extend({
    baseUrl : null,

    init : function(baseUrl) {
        this.baseUrl = baseUrl;
    },
    /**
     * Retrieves the player profile for a specific player
     * @param {Number} playerId id of the player to get the profile for
     * @param {String} sessionToken authentication token to the player api
     * @param {Function} callback success callback
     * @param {Function} errorCallback error callback
     */
    requestPlayerProfile : function(playerId,sessionToken,callback,errorCallback) {
        var url = this.baseUrl + "/public/player/"+playerId+"/profile?session="+sessionToken;
        $.ajax(url, {
            method : "GET",
            contentType : "application/json",
            success : function(data) {
                callback(data);
            },
            error : function() {
                console.log("Error while fetching player profile " + url);
                if(typeof(errorCallback)!="undefined") {
                    errorCallback();
                }
            }

        });
    },
    requestExperienceInfo : function(sessionToken,callback,errorCallback) {
        var url = this.baseUrl + "/player/experience/poker";
        this.requestInfo(url,sessionToken,"GET",callback,errorCallback);
    },
    requestBonusInfo : function(sessionToken,callback,errorCallback) {
        var url = this.baseUrl + "/player/bonus";
        this.requestInfo(url,sessionToken,"GET",callback,errorCallback);
    },

    requestAccountInfo : function(sessionToken,callback,errorCallback) {
        var url = this.baseUrl + "/player/profile";
        this.requestInfo(url,sessionToken,"GET",callback,errorCallback);

    },
    requestTopUp : function(bonusName,sessionToken,callback,errorCallback) {
        var url = this.baseUrl + "/player/bonus/"+bonusName;
        this.requestInfo(url,sessionToken,"POST",callback,errorCallback);
    },
    requestInfo : function(url,sessionToken,method,callback,errorCallback) {
        $.ajax(url + "?r="+Math.random()+"&session="+sessionToken, {
            type : method,
            contentType : "application/json",
            success : function(data) {
                callback(data);
            },
            error : function() {
                console.log("Error while fetching player profile " + url);
                if(typeof(errorCallback)!="undefined") {
                    errorCallback();
                }
            }

        });
    }


});
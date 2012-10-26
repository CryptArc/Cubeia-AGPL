"use strict";
var Poker = Poker || {};

Poker.LoginView = Poker.TabView.extend({
    init: function(viewElementId,name){
        this._super(viewElementId,name);
        var self = this;
        $("#loginButton").click(function(e){
            console.log("logging in");
            if ($('#user').val() != "username" && $('#pwd').val() != "Password") {
                var usr = $('#user').val();
                var pwd = $('#pwd').val();
                Poker.AppCtx.getComHandler().doLogin(usr,pwd);
            }
        });
        $("#user").keyup(function(e){
            self.handleKeyUp(e.keyCode);
        });
        $("#pwd").keyup(function(e){
            self.handleKeyUp(e.keyCode);
        });
    },
    handleKeyUp : function(keyCode) {
        if(keyCode == 13) {
            $("#loginButton").click();
        }
    }
});
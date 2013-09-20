"use strict"
var Poker = Poker || {};
Poker.SkinConfiguration = {
    operatorId : 1,
    name : "mirch",
    preLoadImages : null,
    title : "Mirch Poker",
    onLoad : function() {
        $("#closeHandRankings").click(function(){
            $("#handRankingsView").toggle();
        });
        $(".hand-ranking-icon").click(function(){
            $(".nice-scroll").niceScroll();
            $("#handRankingsView").toggle();
        });
    }
};
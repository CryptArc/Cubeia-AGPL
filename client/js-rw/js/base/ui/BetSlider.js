"use strict";
var Poker = Poker || {};

Poker.BetSlider = Class.extend({
    minBet : 0,
    maxBet : 0,
    markers : [],
    slider : null,
    valueOutputs : null,
    containerId : null,
    init : function(containerId) {
       this.valueOutputs =  $(".slider-value");
       this.containerId = containerId;
    },
    displayOutput : function(value) {
        Poker.MyPlayer.betAmount = value;
        this.valueOutputs.html("&euro;").append(Poker.Utils.formatCurrency(value));
    },

    draw : function() {
        var container = $("#"+this.containerId);
        container.remove();
        container = $("<div/>").attr("id",this.containerId).addClass("poker-slider");

        $("body").append(container);

        var self = this;
        this.slider = container.slider({
            animate: true,
            range: "min",
            orientation: "vertical",
            value: self.minBet,
            max: self.maxBet,
            min: self.minBet,
            step: 50,

            //this gets a live reading of the value and prints it on the page
            slide: function( event, ui ) {
                self.displayOutput(ui.value);
            },

            //this updates the hidden form field so we can submit the data using a form
            change: function(event, ui) {
                self.displayOutput(ui.value);
            }

        });

        $.each(this.markers,function(i,m){
            var value = m.value;
            var marker = m.name;
            var percent = Math.round(100*value/self.maxBet);
            var position = 100 - percent;
            if(position>98) {
                position=96;
            }
            var div = $("<div/>").append(marker).addClass("marker").css("top", position+"%");
            container.append(div);
            div.click(function(e){
                self.slider.slider("value",value);
            });
        });
        this.displayOutput(this.minBet);
    },
    setMinBet : function(minBet) {
        this.minBet = minBet;
    },
    setMaxBet : function(maxBet){
        this.maxBet = maxBet;
    },
    clear : function() {
        this.markers = [];
    },
    remove : function() {
      if(this.slider) {
          this.slider.slider("destroy");
          $("#"+this.containerId).remove();
      }
    },
    addMarker : function(name, value) {
        this.markers.push({name : name, value  : value});
    }
});

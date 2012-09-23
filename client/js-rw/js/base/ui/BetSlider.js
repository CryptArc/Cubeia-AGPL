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
            min: 0,
            step: 50,

            //this gets a live reading of the value and prints it on the page
            slide: function(event,ui ) {
                self.handleChangeValue(ui.value);
            },

            //this updates the hidden form field so we can submit the data using a form
            change: function(event, ui) {
                self.handleChangeValue(ui.value);
            }

        });

        $.each(this.markers,function(i,m){
            var value = m.value;
            var marker = m.name;
            var percent = Math.round(100*(value/self.maxBet));


            var div = $("<div/>").append(marker).addClass("marker").css("bottom", percent+"%");
            container.append(div);
            div.click(function(e){
                self.slider.slider("value",value);
            });
        });
        this.displayOutput(this.minBet);
    },
    handleChangeValue : function(value) {
        if(value<this.minBet) {
            this.slider.slider("value",this.minBet);
        } else {
            this.displayOutput(value);
        }
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
    closeValueExist : function(val) {
      for(var x in this.markers) {
          var mv = 100 * this.markers[x].value / this.maxBet;
          var valPercent = 100 * val / this.maxBet;
          if(mv<(valPercent+5) && mv>(valPercent-5)){
              return true;
          }
      }
      return false;
    },
    /**
     * Adds a marker to the slider, if there is a marker too close
     * tho this marker value it will be ignored
     *
     * @param name  - marker label
     * @param value - the value of the marker
     */
    addMarker : function(name, value) {
        if(value<=this.maxBet && value>=this.minBet && !this.closeValueExist(value)) {
            this.markers.push({name : name, value  : value})
        }
    }
});

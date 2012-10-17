"use strict";
var Poker = Poker || {};

Poker.Settings = {
    Param : {
        SWIPE_ENABLED : "settings.swipe",
        FREEZE_COMMUNICATION : "swttings.freeze"
    },
    isEnabled : function(param,def) {
        if(def==null) {
            def = false;
        }
        return Poker.Utils.loadBoolean(param,def);
    },
    setProperty : function(prop,value) {
        Poker.Utils.store(prop,value);
    },
    bindSettingToggle : function(checkbox,param) {
        var self = this;
        var enabled = this.isEnabled(param);
        checkbox.attr("checked",enabled);
        checkbox.change(function(){
            if(checkbox.is(":checked")) {
                self.setProperty(param,true);
            } else {
                self.setProperty(param,false);
            }
        });
    }

};


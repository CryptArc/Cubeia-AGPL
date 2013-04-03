"use strict";
var Poker = Poker || {};
Poker.SoundSettingsView = Poker.View.extend({
    init : function(viewElementId,name) {
        this._super(viewElementId,name);
    },
    onViewActivated : function() {
        Poker.Settings.bindSettingToggle($("#soundEnabled"),Poker.Settings.Param.SOUND_ENABLED);
    },
    onDeactivateView : function() {
        $("#soundEnabled").unbind();
    }
});
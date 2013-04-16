"use strict";
var Poker = Poker || {};
Poker.SoundSettingsView = Poker.View.extend({
    init : function(viewElementId,name) {
        this._super(viewElementId,name);
    },
    activate : function() {
        this._super();
        Poker.Settings.bindSettingToggle($("#soundEnabled"),Poker.Settings.Param.SOUND_ENABLED);
        $.ga.trackEvent("user_navigation", "enable_sound_effects", "no_label_used", "no_value_used");
    },
    deactivate : function() {
        this._super();
        $("#soundEnabled").unbind();
        $.ga.trackEvent("user_navigation", "disable_sound_effects", "no_label_used", "no_value_used");
    }
});
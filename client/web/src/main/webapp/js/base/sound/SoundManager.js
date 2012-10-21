"use strict";
var Poker = Poker || {};

/**
 * The sound manager is responsible for playing sounds in the client.
 *
 * It's built for being able to handle multi tabling, where only some sounds should
 * be played (like alerts) if the table is not active.
 *
 * @type {*}
 */
Poker.SoundManager = Class.extend({
    soundsRepository:null,
    tableId:null,

    init:function (soundRepository, tableId) {
        this.soundsRepository = soundRepository;
        this.tableId = tableId;
    },

    playSound:function (soundName) {
        if (this.soundsEnabled()) {
            console.log("Playing sound: " + soundName);
            var sound = this.soundsRepository.getSound(soundName);
            if (sound) {
                sound.play();
            } else {
                console.log("No sound found.");
            }
        }
    },

    soundsEnabled:function () {
        return true;
    }
});
"use strict";
var Poker = Poker || {};

/**
 * The sound manager is responsible for playing sounds in the client.
 *
 * It's built for being able to handle multi tabling, where only some sounds should
 * be played (like alerts) if the table is not active.
 *
 * @type {Poker.SoundManager}
 */
Poker.SoundManager = Class.extend({
    /**
     * @type Poker.SoundRepository
     */
    soundsRepository:null,
    /**
     * @type Number
     */
    tableId:null,

    init:function (soundRepository, tableId) {
        this.soundsRepository = soundRepository;
        this.tableId = tableId;
    },

    playSound:function (soundData) {
        if (this.soundsEnabled()) {
            console.log("Playing sound: " + soundData);
            var sound = this.soundsRepository.getSound(soundData.id);
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
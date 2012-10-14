"use strict";
var Poker = Poker || {};

/**
 * The SoundRepository is responsible for loading and caching the sounds in the client.
 *
 * @type {*}
 */
Poker.SoundRepository = Class.extend({
    sounds: [],

    init:function () {
        this.loadSounds();
    },

    loadSounds:function () {
        for (var sound in Poker.Sounds) {
            console.log("Loading sound " + sound + " from file " + Poker.Sounds[sound]);
            var audio = new Audio("sounds/" + Poker.Sounds[sound]);
            this.sounds[Poker.Sounds[sound]] = audio;
        }
    },

    getSound:function (soundName) {
        return this.sounds[soundName];
    }

});
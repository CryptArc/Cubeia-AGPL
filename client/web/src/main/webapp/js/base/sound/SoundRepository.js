"use strict";
var Poker = Poker || {};

/**
 * The SoundRepository is responsible for loading and caching the sounds in the client.
 *
 * @type {*}
 */
Poker.SoundRepository = Class.extend({
    sounds: null,

    init:function () {
        this.sounds = [];
        this.loadSounds();
    },

    loadSounds:function () {
        var codec = this.getCodec();
        var path = "../sounds/"+codec+"/";


        if(typeof(Audio)=="undefined") {
            return;
        }
        for (var sound in Poker.Sounds) {
            var file = path+Poker.Sounds[sound].filename+"."+codec;

            var audio = new Audio([file]);
            audio.volume = Poker.Sounds[sound].gain;
            console.log("Loading sound " + sound + " from file "+file+" ", audio);
            this.sounds[Poker.Sounds[sound].id] = audio;
        }
    },

    getSound:function (soundId) {
        console.log(soundId, this.sounds)
        return this.sounds[soundId];
    },

    getCodec:function()  {
        return "mp3";
    }

});
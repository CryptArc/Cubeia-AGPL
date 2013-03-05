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

            var soundList = Poker.Sounds[sound].soundList
                var soundSources = [];
                for (var i = 0; i < soundList.length; i++) {
                    var file = path+Poker.Sounds[sound].soundList[i].file+"."+codec;
                    var audio = new Audio([file]);
                    audio.volume = Poker.Sounds[sound].soundList[i].gain;
                    console.log("Loading sound " + sound + " from file "+file+" ", audio);
                    soundSources[i] = audio;
                }
            this.sounds[Poker.Sounds[sound].id] = soundSources;

        }
    },

    getSound:function (soundId, selection) {
        console.log(this.sounds[soundId][selection], soundId, this.sounds)
        return this.sounds[soundId][selection];
    },

    getCodec:function()  {
        return "mp3";
    }

});
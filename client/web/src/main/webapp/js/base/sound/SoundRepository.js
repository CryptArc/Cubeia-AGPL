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
        var path = "/poker-client/sounds/"+codec+"/";

        var audioModel = "Audio";
        var context = null;

        if(typeof(Audio)=="undefined") {
            return;
        }

        if(typeof(webkitAudioContext)!="undefined") {
            var audioModel = "webkitAudioContext";
            var context = new webkitAudioContext();
        }

        for (var sound in Poker.Sounds) {

            var soundList = Poker.Sounds[sound].soundList
                var soundSources = [];
                for (var i = 0; i < soundList.length; i++) {
                    var file = path+Poker.Sounds[sound].soundList[i].file+"."+codec;
                    var audio = new Poker.SoundSource(file, audioModel, context);
                    audio.setGain(Poker.Sounds[sound].soundList[i].gain);
                    console.log("Loading to "+audioModel+" from file "+file);
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
        return "ogg";
    }

});
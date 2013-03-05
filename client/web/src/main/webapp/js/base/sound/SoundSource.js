"use strict";
var Poker = Poker || {};

/**
 * This creates a sound source. The differences between WebAudio and Html Audio
 * sounds require different models for loading and playing sound data. This class
 * handle some of that.
 *
 * @type {Poker.SoundSource}
 */
Poker.SoundSource = function (url, audioModel, context) {

    /**
     * @type Poker.SoundSource
     */

    this.url = url;
    this.context = context;
    this.source = {};


    this.play = function () {
        this.playSource();
    };

    this.setGain = function(gain) {
        this.source.volume = gain;
    };

    this.playSource = function() {
        this.source.play();
    };

    this.load = function (audioModel, context) {
        var url = this.url;
        switch (audioModel) {
            case "Audio":
                console.log("Audio sound create")
                this.source = new Audio([url]);
            break;
            case "webkitAudioContext":
                this.loadXHR(url, this.source, context);

                this.source.volume = 1;
                var source = this.source;

                this.source.play = function() {
                    console.log("PLay Context sound!", source);
                    var sourceNode = context.createBufferSource();
                    sourceNode.buffer = source.soundBuffer;
                    sourceNode.gain.value = source.volume;
                    sourceNode.connect(context.destination);
                    sourceNode.noteOn(0);
                };

            break;
        }

    };

    // User XHR for loading to the WebAudio player - superior quality on all supported devices

    this.loadXHR = function(url, source, context) {
//    debug.log("start XHR: "+id)
        var source = source;
        source.url = url;

        source[url] = new XMLHttpRequest();
        source[url].open('GET', source.url, true);
        source[url].responseType = 'arraybuffer';

        // Decode asynchronously
        var request = source[url];

        source[url].onload = function() {
            var onError = function() {alert("Sound Decoding Error!!")};

            context.decodeAudioData(request.response, function(buffer) {
                source.soundBuffer = buffer;
            }, onError)
        };

        source[url].onError = function() {
            console.log("load Error!: "+url)
        };

        source[url].send();
    };

    this.load(audioModel, context);
};


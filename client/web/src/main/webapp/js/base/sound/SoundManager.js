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
     * @type Poker.SoundPlayer
     */
    soundPlayer:null,
    /**
     * @type Number
     */
    tableId:null,

    init:function (soundRepository, tableId) {
        this.soundPlayer = new Poker.SoundPlayer(soundRepository)
        this.tableId = tableId;
    },

    playSound:function (sound, selection) {
        if (this.soundsEnabled()) {
            var soundPlayer = this.soundPlayer;
            var playTimeout = setTimeout(function() {
                soundPlayer.play(sound, selection);
            }, sound.delay);
        }
    },

    soundsEnabled:function () {
        return true;
    },

    handleAlert:function(alert) {

    },

    handleTableUpdate:function(sound, tableId) {
        if (tableId != this.tableId) return;
        console.log("Sound List:", sound.soundList)
        var selection = Math.floor(Math.random()*sound.soundList.length);
        this.playSound(sound, selection)
    },

    playerAction:function(actionType, tableId, player, amount) {
        console.log(actionType);
        var sound = Poker.Sounds[actionType.id];

        var selection = Math.floor(Math.random()*sound.soundList.length);
        this.playSound(sound, selection)
    //    console.log("Player Action Sound! ", action, tableId, player, amount)
    }


});
"use strict";
var Poker = Poker || {};



Poker.Sounds = {

    DEAL_PLAYER:                  {id:"DEAL_PLAYER",         delay:0,     soundList:[{file:"card_1"      ,gain:1}, {file:"card_2"     ,gain:1}, {file:"card_3"     ,gain:1}, {file:"card_4"     ,gain:1}]},
    DEAL_COMMUNITY:               {id:"DEAL_COMMUNITY",      delay:100,   soundList:[{file:"card_7"      ,gain:1}, {file:"card_8"     ,gain:1}, {file:"card_9"     ,gain:1}]},
    REQUEST_ACTION:               {id:"REQUEST_ACTION",      delay:0,     soundList:[{file:"clocktick_2" ,gain:1}]},
    MOVE_DEALER_BUTTON:           {id:"MOVE_DEALER_BUTTON",  delay:300,   soundList:[{file:"arp_1"       ,gain:0.2}]},
    POT_TO_PLAYERS:               {id:"POT_TO_PLAYERS",      delay:260,   soundList:[{file:"chip_stack"     ,gain:0.1}]},
    TIME_WARNING:                 {id: "TIME_WARNING",       delay: 0,   soundList: [{file:"warning"     ,gain: 0.5}], alert : true},
    TIME_WARNING_FIRST:           {id: "TIME_WARNING",       delay: 0,   soundList: [{file:"warning"     ,gain: 0.1}], alert : true},
    "action-call"                :{id:"CALL"              ,  delay:0,     soundList:[{file:"raise_bet"  ,gain:0.5},{file:"raise_bet"  ,gain:0.3},{file:"raise_bet"  ,gain:0.1}]    },
    "action-check"               :{id:"CHECK"             ,  delay:0,     soundList:[{file:"knockcheck_1",gain:0.1},{file:"knockcheck_1",gain:0.2}]    },
    "action-bet"                 :{id:"BET"               ,  delay:0,   soundList:[{file:"raise_more"  ,gain:0.2},{file:"raise_more"  ,gain:0.1}]    },
    "action-raise"               :{id:"RAISE"             ,  delay:0,   soundList:[{file:"raise_more"  ,gain:0.3},{file:"raise_more"  ,gain:0.2},{file:"raise_more"  ,gain:0.1}]    },
    "action-small-blind"         :{id:"SMALL_BLIND"       ,  delay:100,   soundList:[{file:"raise_bet"  ,gain:0.1}]    },
    "action-big-blind"           :{id:"BIG_BLIND"         ,  delay:250,   soundList:[{file:"raise_bet"  ,gain:0.2}]    },
    "dead-small-blind"           :{id:"DEAD_SMALL_BLIND"  ,  delay:0,     soundList:[{file:"raise_bet"      ,gain:1}]    },
    "big-blind-plus-dead-small-blind":{id:"BIG_BLIND_PLUS_DEAD_SMALL_BLIND", delay:0, soundList:[{file:"chippile_1" ,gain:1}] }

}
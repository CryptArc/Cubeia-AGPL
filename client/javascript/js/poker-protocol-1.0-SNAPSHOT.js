// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

var POKER_PROTOCOL = POKER_PROTOCOL || {};



POKER_PROTOCOL.ActionTypeEnum=function(){};
POKER_PROTOCOL.ActionTypeEnum.SMALL_BLIND=0;
POKER_PROTOCOL.ActionTypeEnum.BIG_BLIND=1;
POKER_PROTOCOL.ActionTypeEnum.CALL=2;
POKER_PROTOCOL.ActionTypeEnum.CHECK=3;
POKER_PROTOCOL.ActionTypeEnum.BET=4;
POKER_PROTOCOL.ActionTypeEnum.RAISE=5;
POKER_PROTOCOL.ActionTypeEnum.FOLD=6;
POKER_PROTOCOL.ActionTypeEnum.DECLINE_ENTRY_BET=7;
POKER_PROTOCOL.ActionTypeEnum.ANTE=8;
POKER_PROTOCOL.ActionTypeEnum.makeActionTypeEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.ActionTypeEnum.SMALL_BLIND;
case 1:return POKER_PROTOCOL.ActionTypeEnum.BIG_BLIND;
case 2:return POKER_PROTOCOL.ActionTypeEnum.CALL;
case 3:return POKER_PROTOCOL.ActionTypeEnum.CHECK;
case 4:return POKER_PROTOCOL.ActionTypeEnum.BET;
case 5:return POKER_PROTOCOL.ActionTypeEnum.RAISE;
case 6:return POKER_PROTOCOL.ActionTypeEnum.FOLD;
case 7:return POKER_PROTOCOL.ActionTypeEnum.DECLINE_ENTRY_BET;
case 8:return POKER_PROTOCOL.ActionTypeEnum.ANTE
}return -1
};
POKER_PROTOCOL.BestHand=function(){this.classId=function(){return POKER_PROTOCOL.BestHand.CLASSID
};
this.player={};
this.handType={};
this.cards=[];
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.player);
byteArray.writeUnsignedByte(this.handType);
byteArray.writeInt(this.cards.length);
var i;
for(i=0;
i<this.cards.length;
i++){byteArray.writeArray(this.cards[i].save())
}return byteArray
};
this.load=function(byteArray){this.player=byteArray.readInt();
this.handType=POKER_PROTOCOL.HandTypeEnum.makeHandTypeEnum(byteArray.readUnsignedByte());
var i;
var cardsCount=byteArray.readInt();
var oGameCard;
this.cards=[];
for(i=0;
i<cardsCount;
i++){oGameCard=new POKER_PROTOCOL.GameCard();
oGameCard.load(byteArray);
this.cards.push(oGameCard)
}};
this.toString=function(){var result="POKER_PROTOCOL.BestHand :";
result+=" player["+this.player.toString()+"]";
result+=" hand_type["+this.handType.toString()+"]";
result+=" cards["+this.cards.toString()+"]";
return result
}
};
POKER_PROTOCOL.BestHand.CLASSID=5;
POKER_PROTOCOL.BuyInInfoRequest=function(){this.classId=function(){return POKER_PROTOCOL.BuyInInfoRequest.CLASSID
};
this.save=function(){return[]
};
this.load=function(byteArray){};
this.toString=function(){var result="POKER_PROTOCOL.BuyInInfoRequest :";
return result
}
};
POKER_PROTOCOL.BuyInInfoRequest.CLASSID=22;
POKER_PROTOCOL.BuyInInfoResponse=function(){this.classId=function(){return POKER_PROTOCOL.BuyInInfoResponse.CLASSID
};
this.maxAmount={};
this.minAmount={};
this.balanceInWallet={};
this.balanceOnTable={};
this.mandatoryBuyin={};
this.resultCode={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.maxAmount);
byteArray.writeInt(this.minAmount);
byteArray.writeInt(this.balanceInWallet);
byteArray.writeInt(this.balanceOnTable);
byteArray.writeBoolean(this.mandatoryBuyin);
byteArray.writeUnsignedByte(this.resultCode);
return byteArray
};
this.load=function(byteArray){this.maxAmount=byteArray.readInt();
this.minAmount=byteArray.readInt();
this.balanceInWallet=byteArray.readInt();
this.balanceOnTable=byteArray.readInt();
this.mandatoryBuyin=byteArray.readBoolean();
this.resultCode=POKER_PROTOCOL.BuyInInfoResultCodeEnum.makeBuyInInfoResultCodeEnum(byteArray.readUnsignedByte())
};
this.toString=function(){var result="POKER_PROTOCOL.BuyInInfoResponse :";
result+=" max_amount["+this.maxAmount.toString()+"]";
result+=" min_amount["+this.minAmount.toString()+"]";
result+=" balance_in_wallet["+this.balanceInWallet.toString()+"]";
result+=" balance_on_table["+this.balanceOnTable.toString()+"]";
result+=" mandatory_buyin["+this.mandatoryBuyin.toString()+"]";
result+=" result_code["+this.resultCode.toString()+"]";
return result
}
};
POKER_PROTOCOL.BuyInInfoResponse.CLASSID=23;
POKER_PROTOCOL.BuyInInfoResultCodeEnum=function(){};
POKER_PROTOCOL.BuyInInfoResultCodeEnum.OK=0;
POKER_PROTOCOL.BuyInInfoResultCodeEnum.MAX_LIMIT_REACHED=1;
POKER_PROTOCOL.BuyInInfoResultCodeEnum.UNSPECIFIED_ERROR=2;
POKER_PROTOCOL.BuyInInfoResultCodeEnum.makeBuyInInfoResultCodeEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.BuyInInfoResultCodeEnum.OK;
case 1:return POKER_PROTOCOL.BuyInInfoResultCodeEnum.MAX_LIMIT_REACHED;
case 2:return POKER_PROTOCOL.BuyInInfoResultCodeEnum.UNSPECIFIED_ERROR
}return -1
};
POKER_PROTOCOL.BuyInRequest=function(){this.classId=function(){return POKER_PROTOCOL.BuyInRequest.CLASSID
};
this.amount={};
this.sitInIfSuccessful={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.amount);
byteArray.writeBoolean(this.sitInIfSuccessful);
return byteArray
};
this.load=function(byteArray){this.amount=byteArray.readInt();
this.sitInIfSuccessful=byteArray.readBoolean()
};
this.toString=function(){var result="POKER_PROTOCOL.BuyInRequest :";
result+=" amount["+this.amount.toString()+"]";
result+=" sit_in_if_successful["+this.sitInIfSuccessful.toString()+"]";
return result
}
};
POKER_PROTOCOL.BuyInRequest.CLASSID=24;
POKER_PROTOCOL.BuyInResponse=function(){this.classId=function(){return POKER_PROTOCOL.BuyInResponse.CLASSID
};
this.balance={};
this.pendingBalance={};
this.amountBroughtIn={};
this.resultCode={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.balance);
byteArray.writeInt(this.pendingBalance);
byteArray.writeInt(this.amountBroughtIn);
byteArray.writeUnsignedByte(this.resultCode);
return byteArray
};
this.load=function(byteArray){this.balance=byteArray.readInt();
this.pendingBalance=byteArray.readInt();
this.amountBroughtIn=byteArray.readInt();
this.resultCode=POKER_PROTOCOL.BuyInResultCodeEnum.makeBuyInResultCodeEnum(byteArray.readUnsignedByte())
};
this.toString=function(){var result="POKER_PROTOCOL.BuyInResponse :";
result+=" balance["+this.balance.toString()+"]";
result+=" pending_balance["+this.pendingBalance.toString()+"]";
result+=" amount_brought_in["+this.amountBroughtIn.toString()+"]";
result+=" result_code["+this.resultCode.toString()+"]";
return result
}
};
POKER_PROTOCOL.BuyInResponse.CLASSID=25;
POKER_PROTOCOL.BuyInResultCodeEnum=function(){};
POKER_PROTOCOL.BuyInResultCodeEnum.OK=0;
POKER_PROTOCOL.BuyInResultCodeEnum.PENDING=1;
POKER_PROTOCOL.BuyInResultCodeEnum.INSUFFICIENT_FUNDS_ERROR=2;
POKER_PROTOCOL.BuyInResultCodeEnum.PARTNER_ERROR=3;
POKER_PROTOCOL.BuyInResultCodeEnum.MAX_LIMIT_REACHED=4;
POKER_PROTOCOL.BuyInResultCodeEnum.AMOUNT_TOO_HIGH=5;
POKER_PROTOCOL.BuyInResultCodeEnum.UNSPECIFIED_ERROR=6;
POKER_PROTOCOL.BuyInResultCodeEnum.SESSION_NOT_OPEN=7;
POKER_PROTOCOL.BuyInResultCodeEnum.makeBuyInResultCodeEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.BuyInResultCodeEnum.OK;
case 1:return POKER_PROTOCOL.BuyInResultCodeEnum.PENDING;
case 2:return POKER_PROTOCOL.BuyInResultCodeEnum.INSUFFICIENT_FUNDS_ERROR;
case 3:return POKER_PROTOCOL.BuyInResultCodeEnum.PARTNER_ERROR;
case 4:return POKER_PROTOCOL.BuyInResultCodeEnum.MAX_LIMIT_REACHED;
case 5:return POKER_PROTOCOL.BuyInResultCodeEnum.AMOUNT_TOO_HIGH;
case 6:return POKER_PROTOCOL.BuyInResultCodeEnum.UNSPECIFIED_ERROR;
case 7:return POKER_PROTOCOL.BuyInResultCodeEnum.SESSION_NOT_OPEN
}return -1
};
POKER_PROTOCOL.CardToDeal=function(){this.classId=function(){return POKER_PROTOCOL.CardToDeal.CLASSID
};
this.player={};
this.card={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.player);
byteArray.writeArray(this.card.save());
return byteArray
};
this.load=function(byteArray){this.player=byteArray.readInt();
this.card=new POKER_PROTOCOL.GameCard();
this.card.load(byteArray)
};
this.toString=function(){var result="POKER_PROTOCOL.CardToDeal :";
result+=" player["+this.player.toString()+"]";
result+=" card["+this.card.toString()+"]";
return result
}
};
POKER_PROTOCOL.CardToDeal.CLASSID=7;
POKER_PROTOCOL.DealPrivateCards=function(){this.classId=function(){return POKER_PROTOCOL.DealPrivateCards.CLASSID
};
this.cards=[];
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.cards.length);
var i;
for(i=0;
i<this.cards.length;
i++){byteArray.writeArray(this.cards[i].save())
}return byteArray
};
this.load=function(byteArray){var i;
var cardsCount=byteArray.readInt();
var oCardToDeal;
this.cards=[];
for(i=0;
i<cardsCount;
i++){oCardToDeal=new POKER_PROTOCOL.CardToDeal();
oCardToDeal.load(byteArray);
this.cards.push(oCardToDeal)
}};
this.toString=function(){var result="POKER_PROTOCOL.DealPrivateCards :";
result+=" cards["+this.cards.toString()+"]";
return result
}
};
POKER_PROTOCOL.DealPrivateCards.CLASSID=13;
POKER_PROTOCOL.DealPublicCards=function(){this.classId=function(){return POKER_PROTOCOL.DealPublicCards.CLASSID
};
this.cards=[];
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.cards.length);
var i;
for(i=0;
i<this.cards.length;
i++){byteArray.writeArray(this.cards[i].save())
}return byteArray
};
this.load=function(byteArray){var i;
var cardsCount=byteArray.readInt();
var oGameCard;
this.cards=[];
for(i=0;
i<cardsCount;
i++){oGameCard=new POKER_PROTOCOL.GameCard();
oGameCard.load(byteArray);
this.cards.push(oGameCard)
}};
this.toString=function(){var result="POKER_PROTOCOL.DealPublicCards :";
result+=" cards["+this.cards.toString()+"]";
return result
}
};
POKER_PROTOCOL.DealPublicCards.CLASSID=12;
POKER_PROTOCOL.DealerButton=function(){this.classId=function(){return POKER_PROTOCOL.DealerButton.CLASSID
};
this.seat={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeByte(this.seat);
return byteArray
};
this.load=function(byteArray){this.seat=byteArray.readByte()
};
this.toString=function(){var result="POKER_PROTOCOL.DealerButton :";
result+=" seat["+this.seat.toString()+"]";
return result
}
};
POKER_PROTOCOL.DealerButton.CLASSID=11;
POKER_PROTOCOL.DeckInfo=function(){this.classId=function(){return POKER_PROTOCOL.DeckInfo.CLASSID
};
this.size={};
this.rankLow={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.size);
byteArray.writeUnsignedByte(this.rankLow);
return byteArray
};
this.load=function(byteArray){this.size=byteArray.readInt();
this.rankLow=POKER_PROTOCOL.RankEnum.makeRankEnum(byteArray.readUnsignedByte())
};
this.toString=function(){var result="POKER_PROTOCOL.DeckInfo :";
result+=" size["+this.size.toString()+"]";
result+=" rank_low["+this.rankLow.toString()+"]";
return result
}
};
POKER_PROTOCOL.DeckInfo.CLASSID=35;
POKER_PROTOCOL.ErrorCodeEnum=function(){};
POKER_PROTOCOL.ErrorCodeEnum.UNSPECIFIED_ERROR=0;
POKER_PROTOCOL.ErrorCodeEnum.TABLE_CLOSING=1;
POKER_PROTOCOL.ErrorCodeEnum.TABLE_CLOSING_FORCED=2;
POKER_PROTOCOL.ErrorCodeEnum.CLOSED_SESSION_DUE_TO_FATAL_ERROR=3;
POKER_PROTOCOL.ErrorCodeEnum.makeErrorCodeEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.ErrorCodeEnum.UNSPECIFIED_ERROR;
case 1:return POKER_PROTOCOL.ErrorCodeEnum.TABLE_CLOSING;
case 2:return POKER_PROTOCOL.ErrorCodeEnum.TABLE_CLOSING_FORCED;
case 3:return POKER_PROTOCOL.ErrorCodeEnum.CLOSED_SESSION_DUE_TO_FATAL_ERROR
}return -1
};
POKER_PROTOCOL.ErrorPacket=function(){this.classId=function(){return POKER_PROTOCOL.ErrorPacket.CLASSID
};
this.code={};
this.referenceId={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeUnsignedByte(this.code);
byteArray.writeString(this.referenceId);
return byteArray
};
this.load=function(byteArray){this.code=POKER_PROTOCOL.ErrorCodeEnum.makeErrorCodeEnum(byteArray.readUnsignedByte());
this.referenceId=byteArray.readString()
};
this.toString=function(){var result="POKER_PROTOCOL.ErrorPacket :";
result+=" code["+this.code.toString()+"]";
result+=" reference_id["+this.referenceId.toString()+"]";
return result
}
};
POKER_PROTOCOL.ErrorPacket.CLASSID=2;
POKER_PROTOCOL.ExposePrivateCards=function(){this.classId=function(){return POKER_PROTOCOL.ExposePrivateCards.CLASSID
};
this.cards=[];
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.cards.length);
var i;
for(i=0;
i<this.cards.length;
i++){byteArray.writeArray(this.cards[i].save())
}return byteArray
};
this.load=function(byteArray){var i;
var cardsCount=byteArray.readInt();
var oCardToDeal;
this.cards=[];
for(i=0;
i<cardsCount;
i++){oCardToDeal=new POKER_PROTOCOL.CardToDeal();
oCardToDeal.load(byteArray);
this.cards.push(oCardToDeal)
}};
this.toString=function(){var result="POKER_PROTOCOL.ExposePrivateCards :";
result+=" cards["+this.cards.toString()+"]";
return result
}
};
POKER_PROTOCOL.ExposePrivateCards.CLASSID=14;
POKER_PROTOCOL.ExternalSessionInfoPacket=function(){this.classId=function(){return POKER_PROTOCOL.ExternalSessionInfoPacket.CLASSID
};
this.externalTableReference={};
this.externalTableSessionReference={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeString(this.externalTableReference);
byteArray.writeString(this.externalTableSessionReference);
return byteArray
};
this.load=function(byteArray){this.externalTableReference=byteArray.readString();
this.externalTableSessionReference=byteArray.readString()
};
this.toString=function(){var result="POKER_PROTOCOL.ExternalSessionInfoPacket :";
result+=" external_table_reference["+this.externalTableReference.toString()+"]";
result+=" external_table_session_reference["+this.externalTableSessionReference.toString()+"]";
return result
}
};
POKER_PROTOCOL.ExternalSessionInfoPacket.CLASSID=36;
POKER_PROTOCOL.FuturePlayerAction=function(){this.classId=function(){return POKER_PROTOCOL.FuturePlayerAction.CLASSID
};
this.action={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeUnsignedByte(this.action);
return byteArray
};
this.load=function(byteArray){this.action=POKER_PROTOCOL.ActionTypeEnum.makeActionTypeEnum(byteArray.readUnsignedByte())
};
this.toString=function(){var result="POKER_PROTOCOL.FuturePlayerAction :";
result+=" action["+this.action.toString()+"]";
return result
}
};
POKER_PROTOCOL.FuturePlayerAction.CLASSID=3;
POKER_PROTOCOL.GameCard=function(){this.classId=function(){return POKER_PROTOCOL.GameCard.CLASSID
};
this.cardId={};
this.suit={};
this.rank={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.cardId);
byteArray.writeUnsignedByte(this.suit);
byteArray.writeUnsignedByte(this.rank);
return byteArray
};
this.load=function(byteArray){this.cardId=byteArray.readInt();
this.suit=POKER_PROTOCOL.SuitEnum.makeSuitEnum(byteArray.readUnsignedByte());
this.rank=POKER_PROTOCOL.RankEnum.makeRankEnum(byteArray.readUnsignedByte())
};
this.toString=function(){var result="POKER_PROTOCOL.GameCard :";
result+=" card_id["+this.cardId.toString()+"]";
result+=" suit["+this.suit.toString()+"]";
result+=" rank["+this.rank.toString()+"]";
return result
}
};
POKER_PROTOCOL.GameCard.CLASSID=4;
POKER_PROTOCOL.HandCanceled=function(){this.classId=function(){return POKER_PROTOCOL.HandCanceled.CLASSID
};
this.save=function(){return[]
};
this.load=function(byteArray){};
this.toString=function(){var result="POKER_PROTOCOL.HandCanceled :";
return result
}
};
POKER_PROTOCOL.HandCanceled.CLASSID=16;
POKER_PROTOCOL.HandEnd=function(){this.classId=function(){return POKER_PROTOCOL.HandEnd.CLASSID
};
this.playerIdRevealOrder=[];
this.hands=[];
this.potTransfers={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.playerIdRevealOrder.length);
var i;
for(i=0;
i<this.playerIdRevealOrder.length;
i++){byteArray.writeInt(this.playerIdRevealOrder[i])
}byteArray.writeInt(this.hands.length);
for(i=0;
i<this.hands.length;
i++){byteArray.writeArray(this.hands[i].save())
}byteArray.writeArray(this.potTransfers.save());
return byteArray
};
this.load=function(byteArray){var i;
var playerIdRevealOrderCount=byteArray.readInt();
this.playerIdRevealOrder=[];
for(i=0;
i<playerIdRevealOrderCount;
i++){this.playerIdRevealOrder.push(byteArray.readInt())
}var handsCount=byteArray.readInt();
var oBestHand;
this.hands=[];
for(i=0;
i<handsCount;
i++){oBestHand=new POKER_PROTOCOL.BestHand();
oBestHand.load(byteArray);
this.hands.push(oBestHand)
}this.potTransfers=new POKER_PROTOCOL.PotTransfers();
this.potTransfers.load(byteArray)
};
this.toString=function(){var result="POKER_PROTOCOL.HandEnd :";
result+=" player_id_reveal_order["+this.playerIdRevealOrder.toString()+"]";
result+=" hands["+this.hands.toString()+"]";
result+=" pot_transfers["+this.potTransfers.toString()+"]";
return result
}
};
POKER_PROTOCOL.HandEnd.CLASSID=15;
POKER_PROTOCOL.HandPhase5cardEnum=function(){};
POKER_PROTOCOL.HandPhase5cardEnum.BETTING=0;
POKER_PROTOCOL.HandPhase5cardEnum.THIRD_STREET=1;
POKER_PROTOCOL.HandPhase5cardEnum.FOURTH_STREET=2;
POKER_PROTOCOL.HandPhase5cardEnum.FIFTH_STREET=3;
POKER_PROTOCOL.HandPhase5cardEnum.makeHandPhase5cardEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.HandPhase5cardEnum.BETTING;
case 1:return POKER_PROTOCOL.HandPhase5cardEnum.THIRD_STREET;
case 2:return POKER_PROTOCOL.HandPhase5cardEnum.FOURTH_STREET;
case 3:return POKER_PROTOCOL.HandPhase5cardEnum.FIFTH_STREET
}return -1
};
POKER_PROTOCOL.HandPhaseHoldemEnum=function(){};
POKER_PROTOCOL.HandPhaseHoldemEnum.PREFLOP=0;
POKER_PROTOCOL.HandPhaseHoldemEnum.FLOP=1;
POKER_PROTOCOL.HandPhaseHoldemEnum.TURN=2;
POKER_PROTOCOL.HandPhaseHoldemEnum.RIVER=3;
POKER_PROTOCOL.HandPhaseHoldemEnum.makeHandPhaseHoldemEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.HandPhaseHoldemEnum.PREFLOP;
case 1:return POKER_PROTOCOL.HandPhaseHoldemEnum.FLOP;
case 2:return POKER_PROTOCOL.HandPhaseHoldemEnum.TURN;
case 3:return POKER_PROTOCOL.HandPhaseHoldemEnum.RIVER
}return -1
};
POKER_PROTOCOL.HandTypeEnum=function(){};
POKER_PROTOCOL.HandTypeEnum.UNKNOWN=0;
POKER_PROTOCOL.HandTypeEnum.HIGH_CARD=1;
POKER_PROTOCOL.HandTypeEnum.PAIR=2;
POKER_PROTOCOL.HandTypeEnum.TWO_PAIR=3;
POKER_PROTOCOL.HandTypeEnum.THREE_OF_A_KIND=4;
POKER_PROTOCOL.HandTypeEnum.STRAIGHT=5;
POKER_PROTOCOL.HandTypeEnum.FLUSH=6;
POKER_PROTOCOL.HandTypeEnum.FULL_HOUSE=7;
POKER_PROTOCOL.HandTypeEnum.FOUR_OF_A_KIND=8;
POKER_PROTOCOL.HandTypeEnum.STRAIGHT_FLUSH=9;
POKER_PROTOCOL.HandTypeEnum.ROYAL_STRAIGHT_FLUSH=10;
POKER_PROTOCOL.HandTypeEnum.makeHandTypeEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.HandTypeEnum.UNKNOWN;
case 1:return POKER_PROTOCOL.HandTypeEnum.HIGH_CARD;
case 2:return POKER_PROTOCOL.HandTypeEnum.PAIR;
case 3:return POKER_PROTOCOL.HandTypeEnum.TWO_PAIR;
case 4:return POKER_PROTOCOL.HandTypeEnum.THREE_OF_A_KIND;
case 5:return POKER_PROTOCOL.HandTypeEnum.STRAIGHT;
case 6:return POKER_PROTOCOL.HandTypeEnum.FLUSH;
case 7:return POKER_PROTOCOL.HandTypeEnum.FULL_HOUSE;
case 8:return POKER_PROTOCOL.HandTypeEnum.FOUR_OF_A_KIND;
case 9:return POKER_PROTOCOL.HandTypeEnum.STRAIGHT_FLUSH;
case 10:return POKER_PROTOCOL.HandTypeEnum.ROYAL_STRAIGHT_FLUSH
}return -1
};
POKER_PROTOCOL.InformFutureAllowedActions=function(){this.classId=function(){return POKER_PROTOCOL.InformFutureAllowedActions.CLASSID
};
this.actions=[];
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.actions.length);
var i;
for(i=0;
i<this.actions.length;
i++){byteArray.writeArray(this.actions[i].save())
}return byteArray
};
this.load=function(byteArray){var i;
var actionsCount=byteArray.readInt();
var oFuturePlayerAction;
this.actions=[];
for(i=0;
i<actionsCount;
i++){oFuturePlayerAction=new POKER_PROTOCOL.FuturePlayerAction();
oFuturePlayerAction.load(byteArray);
this.actions.push(oFuturePlayerAction)
}};
this.toString=function(){var result="POKER_PROTOCOL.InformFutureAllowedActions :";
result+=" actions["+this.actions.toString()+"]";
return result
}
};
POKER_PROTOCOL.InformFutureAllowedActions.CLASSID=9;
POKER_PROTOCOL.PerformAction=function(){this.classId=function(){return POKER_PROTOCOL.PerformAction.CLASSID
};
this.seq={};
this.player={};
this.action={};
this.betAmount={};
this.raiseAmount={};
this.stackAmount={};
this.timeout={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.seq);
byteArray.writeInt(this.player);
byteArray.writeArray(this.action.save());
byteArray.writeInt(this.betAmount);
byteArray.writeInt(this.raiseAmount);
byteArray.writeInt(this.stackAmount);
byteArray.writeBoolean(this.timeout);
return byteArray
};
this.load=function(byteArray){this.seq=byteArray.readInt();
this.player=byteArray.readInt();
this.action=new POKER_PROTOCOL.PlayerAction();
this.action.load(byteArray);
this.betAmount=byteArray.readInt();
this.raiseAmount=byteArray.readInt();
this.stackAmount=byteArray.readInt();
this.timeout=byteArray.readBoolean()
};
this.toString=function(){var result="POKER_PROTOCOL.PerformAction :";
result+=" seq["+this.seq.toString()+"]";
result+=" player["+this.player.toString()+"]";
result+=" action["+this.action.toString()+"]";
result+=" bet_amount["+this.betAmount.toString()+"]";
result+=" raise_amount["+this.raiseAmount.toString()+"]";
result+=" stack_amount["+this.stackAmount.toString()+"]";
result+=" timeout["+this.timeout.toString()+"]";
return result
}
};
POKER_PROTOCOL.PerformAction.CLASSID=19;
POKER_PROTOCOL.PingPacket=function(){this.classId=function(){return POKER_PROTOCOL.PingPacket.CLASSID
};
this.identifier={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.identifier);
return byteArray
};
this.load=function(byteArray){this.identifier=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.PingPacket :";
result+=" identifier["+this.identifier.toString()+"]";
return result
}
};
POKER_PROTOCOL.PingPacket.CLASSID=39;
POKER_PROTOCOL.PlayerAction=function(){this.classId=function(){return POKER_PROTOCOL.PlayerAction.CLASSID
};
this.type={};
this.minAmount={};
this.maxAmount={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeUnsignedByte(this.type);
byteArray.writeInt(this.minAmount);
byteArray.writeInt(this.maxAmount);
return byteArray
};
this.load=function(byteArray){this.type=POKER_PROTOCOL.ActionTypeEnum.makeActionTypeEnum(byteArray.readUnsignedByte());
this.minAmount=byteArray.readInt();
this.maxAmount=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.PlayerAction :";
result+=" type["+this.type.toString()+"]";
result+=" min_amount["+this.minAmount.toString()+"]";
result+=" max_amount["+this.maxAmount.toString()+"]";
return result
}
};
POKER_PROTOCOL.PlayerAction.CLASSID=1;
POKER_PROTOCOL.PlayerBalance=function(){this.classId=function(){return POKER_PROTOCOL.PlayerBalance.CLASSID
};
this.balance={};
this.pendingBalance={};
this.player={};
this.playersContributionToPot={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.balance);
byteArray.writeInt(this.pendingBalance);
byteArray.writeInt(this.player);
byteArray.writeInt(this.playersContributionToPot);
return byteArray
};
this.load=function(byteArray){this.balance=byteArray.readInt();
this.pendingBalance=byteArray.readInt();
this.player=byteArray.readInt();
this.playersContributionToPot=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.PlayerBalance :";
result+=" balance["+this.balance.toString()+"]";
result+=" pendingBalance["+this.pendingBalance.toString()+"]";
result+=" player["+this.player.toString()+"]";
result+=" players_contribution_to_pot["+this.playersContributionToPot.toString()+"]";
return result
}
};
POKER_PROTOCOL.PlayerBalance.CLASSID=21;
POKER_PROTOCOL.PlayerDisconnectedPacket=function(){this.classId=function(){return POKER_PROTOCOL.PlayerDisconnectedPacket.CLASSID
};
this.playerId={};
this.timebank={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.playerId);
byteArray.writeInt(this.timebank);
return byteArray
};
this.load=function(byteArray){this.playerId=byteArray.readInt();
this.timebank=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.PlayerDisconnectedPacket :";
result+=" player_id["+this.playerId.toString()+"]";
result+=" timebank["+this.timebank.toString()+"]";
return result
}
};
POKER_PROTOCOL.PlayerDisconnectedPacket.CLASSID=37;
POKER_PROTOCOL.PlayerHandStartStatus=function(){this.classId=function(){return POKER_PROTOCOL.PlayerHandStartStatus.CLASSID
};
this.player={};
this.status={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.player);
byteArray.writeUnsignedByte(this.status);
return byteArray
};
this.load=function(byteArray){this.player=byteArray.readInt();
this.status=POKER_PROTOCOL.PlayerTableStatusEnum.makePlayerTableStatusEnum(byteArray.readUnsignedByte())
};
this.toString=function(){var result="POKER_PROTOCOL.PlayerHandStartStatus :";
result+=" player["+this.player.toString()+"]";
result+=" status["+this.status.toString()+"]";
return result
}
};
POKER_PROTOCOL.PlayerHandStartStatus.CLASSID=32;
POKER_PROTOCOL.PlayerPokerStatus=function(){this.classId=function(){return POKER_PROTOCOL.PlayerPokerStatus.CLASSID
};
this.player={};
this.status={};
this.inCurrentHand={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.player);
byteArray.writeUnsignedByte(this.status);
byteArray.writeBoolean(this.inCurrentHand);
return byteArray
};
this.load=function(byteArray){this.player=byteArray.readInt();
this.status=POKER_PROTOCOL.PlayerTableStatusEnum.makePlayerTableStatusEnum(byteArray.readUnsignedByte());
this.inCurrentHand=byteArray.readBoolean()
};
this.toString=function(){var result="POKER_PROTOCOL.PlayerPokerStatus :";
result+=" player["+this.player.toString()+"]";
result+=" status["+this.status.toString()+"]";
result+=" in_current_hand["+this.inCurrentHand.toString()+"]";
return result
}
};
POKER_PROTOCOL.PlayerPokerStatus.CLASSID=31;
POKER_PROTOCOL.PlayerReconnectedPacket=function(){this.classId=function(){return POKER_PROTOCOL.PlayerReconnectedPacket.CLASSID
};
this.playerId={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.playerId);
return byteArray
};
this.load=function(byteArray){this.playerId=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.PlayerReconnectedPacket :";
result+=" player_id["+this.playerId.toString()+"]";
return result
}
};
POKER_PROTOCOL.PlayerReconnectedPacket.CLASSID=38;
POKER_PROTOCOL.PlayerSitinRequest=function(){this.classId=function(){return POKER_PROTOCOL.PlayerSitinRequest.CLASSID
};
this.save=function(){return[]
};
this.load=function(byteArray){};
this.toString=function(){var result="POKER_PROTOCOL.PlayerSitinRequest :";
return result
}
};
POKER_PROTOCOL.PlayerSitinRequest.CLASSID=33;
POKER_PROTOCOL.PlayerSitoutRequest=function(){this.classId=function(){return POKER_PROTOCOL.PlayerSitoutRequest.CLASSID
};
this.save=function(){return[]
};
this.load=function(byteArray){};
this.toString=function(){var result="POKER_PROTOCOL.PlayerSitoutRequest :";
return result
}
};
POKER_PROTOCOL.PlayerSitoutRequest.CLASSID=34;
POKER_PROTOCOL.PlayerState=function(){this.classId=function(){return POKER_PROTOCOL.PlayerState.CLASSID
};
this.player={};
this.cards=[];
this.balance={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.player);
byteArray.writeInt(this.cards.length);
var i;
for(i=0;
i<this.cards.length;
i++){byteArray.writeArray(this.cards[i].save())
}byteArray.writeInt(this.balance);
return byteArray
};
this.load=function(byteArray){this.player=byteArray.readInt();
var i;
var cardsCount=byteArray.readInt();
var oGameCard;
this.cards=[];
for(i=0;
i<cardsCount;
i++){oGameCard=new POKER_PROTOCOL.GameCard();
oGameCard.load(byteArray);
this.cards.push(oGameCard)
}this.balance=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.PlayerState :";
result+=" player["+this.player.toString()+"]";
result+=" cards["+this.cards.toString()+"]";
result+=" balance["+this.balance.toString()+"]";
return result
}
};
POKER_PROTOCOL.PlayerState.CLASSID=6;
POKER_PROTOCOL.PlayerTableStatusEnum=function(){};
POKER_PROTOCOL.PlayerTableStatusEnum.SITIN=0;
POKER_PROTOCOL.PlayerTableStatusEnum.SITOUT=1;
POKER_PROTOCOL.PlayerTableStatusEnum.makePlayerTableStatusEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.PlayerTableStatusEnum.SITIN;
case 1:return POKER_PROTOCOL.PlayerTableStatusEnum.SITOUT
}return -1
};
POKER_PROTOCOL.PongPacket=function(){this.classId=function(){return POKER_PROTOCOL.PongPacket.CLASSID
};
this.identifier={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.identifier);
return byteArray
};
this.load=function(byteArray){this.identifier=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.PongPacket :";
result+=" identifier["+this.identifier.toString()+"]";
return result
}
};
POKER_PROTOCOL.PongPacket.CLASSID=40;
POKER_PROTOCOL.Pot=function(){this.classId=function(){return POKER_PROTOCOL.Pot.CLASSID
};
this.id={};
this.type={};
this.amount={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeByte(this.id);
byteArray.writeUnsignedByte(this.type);
byteArray.writeInt(this.amount);
return byteArray
};
this.load=function(byteArray){this.id=byteArray.readByte();
this.type=POKER_PROTOCOL.PotTypeEnum.makePotTypeEnum(byteArray.readUnsignedByte());
this.amount=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.Pot :";
result+=" id["+this.id.toString()+"]";
result+=" type["+this.type.toString()+"]";
result+=" amount["+this.amount.toString()+"]";
return result
}
};
POKER_PROTOCOL.Pot.CLASSID=26;
POKER_PROTOCOL.PotTransfer=function(){this.classId=function(){return POKER_PROTOCOL.PotTransfer.CLASSID
};
this.potId={};
this.playerId={};
this.amount={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeByte(this.potId);
byteArray.writeInt(this.playerId);
byteArray.writeInt(this.amount);
return byteArray
};
this.load=function(byteArray){this.potId=byteArray.readByte();
this.playerId=byteArray.readInt();
this.amount=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.PotTransfer :";
result+=" pot_id["+this.potId.toString()+"]";
result+=" player_id["+this.playerId.toString()+"]";
result+=" amount["+this.amount.toString()+"]";
return result
}
};
POKER_PROTOCOL.PotTransfer.CLASSID=27;
POKER_PROTOCOL.PotTransfers=function(){this.classId=function(){return POKER_PROTOCOL.PotTransfers.CLASSID
};
this.fromPlayerToPot={};
this.transfers=[];
this.pots=[];
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeBoolean(this.fromPlayerToPot);
byteArray.writeInt(this.transfers.length);
var i;
for(i=0;
i<this.transfers.length;
i++){byteArray.writeArray(this.transfers[i].save())
}byteArray.writeInt(this.pots.length);
for(i=0;
i<this.pots.length;
i++){byteArray.writeArray(this.pots[i].save())
}return byteArray
};
this.load=function(byteArray){this.fromPlayerToPot=byteArray.readBoolean();
var i;
var transfersCount=byteArray.readInt();
var oPotTransfer;
this.transfers=[];
for(i=0;
i<transfersCount;
i++){oPotTransfer=new POKER_PROTOCOL.PotTransfer();
oPotTransfer.load(byteArray);
this.transfers.push(oPotTransfer)
}var potsCount=byteArray.readInt();
var oPot;
this.pots=[];
for(i=0;
i<potsCount;
i++){oPot=new POKER_PROTOCOL.Pot();
oPot.load(byteArray);
this.pots.push(oPot)
}};
this.toString=function(){var result="POKER_PROTOCOL.PotTransfers :";
result+=" fromPlayerToPot["+this.fromPlayerToPot.toString()+"]";
result+=" transfers["+this.transfers.toString()+"]";
result+=" pots["+this.pots.toString()+"]";
return result
}
};
POKER_PROTOCOL.PotTransfers.CLASSID=28;
POKER_PROTOCOL.PotTypeEnum=function(){};
POKER_PROTOCOL.PotTypeEnum.MAIN=0;
POKER_PROTOCOL.PotTypeEnum.SIDE=1;
POKER_PROTOCOL.PotTypeEnum.makePotTypeEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.PotTypeEnum.MAIN;
case 1:return POKER_PROTOCOL.PotTypeEnum.SIDE
}return -1
};
POKER_PROTOCOL.ProtocolObjectFactory={};
POKER_PROTOCOL.ProtocolObjectFactory.create=function(classId,gameData){var protocolObject;
switch(classId){case POKER_PROTOCOL.PlayerAction.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerAction();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.ErrorPacket.CLASSID:protocolObject=new POKER_PROTOCOL.ErrorPacket();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.FuturePlayerAction.CLASSID:protocolObject=new POKER_PROTOCOL.FuturePlayerAction();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.GameCard.CLASSID:protocolObject=new POKER_PROTOCOL.GameCard();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.BestHand.CLASSID:protocolObject=new POKER_PROTOCOL.BestHand();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PlayerState.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerState();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.CardToDeal.CLASSID:protocolObject=new POKER_PROTOCOL.CardToDeal();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.RequestAction.CLASSID:protocolObject=new POKER_PROTOCOL.RequestAction();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.InformFutureAllowedActions.CLASSID:protocolObject=new POKER_PROTOCOL.InformFutureAllowedActions();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.StartNewHand.CLASSID:protocolObject=new POKER_PROTOCOL.StartNewHand();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.DealerButton.CLASSID:protocolObject=new POKER_PROTOCOL.DealerButton();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.DealPublicCards.CLASSID:protocolObject=new POKER_PROTOCOL.DealPublicCards();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.DealPrivateCards.CLASSID:protocolObject=new POKER_PROTOCOL.DealPrivateCards();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.ExposePrivateCards.CLASSID:protocolObject=new POKER_PROTOCOL.ExposePrivateCards();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.HandEnd.CLASSID:protocolObject=new POKER_PROTOCOL.HandEnd();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.HandCanceled.CLASSID:protocolObject=new POKER_PROTOCOL.HandCanceled();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.StartHandHistory.CLASSID:protocolObject=new POKER_PROTOCOL.StartHandHistory();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.StopHandHistory.CLASSID:protocolObject=new POKER_PROTOCOL.StopHandHistory();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PerformAction.CLASSID:protocolObject=new POKER_PROTOCOL.PerformAction();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.TournamentOut.CLASSID:protocolObject=new POKER_PROTOCOL.TournamentOut();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PlayerBalance.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerBalance();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.BuyInInfoRequest.CLASSID:protocolObject=new POKER_PROTOCOL.BuyInInfoRequest();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.BuyInInfoResponse.CLASSID:protocolObject=new POKER_PROTOCOL.BuyInInfoResponse();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.BuyInRequest.CLASSID:protocolObject=new POKER_PROTOCOL.BuyInRequest();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.BuyInResponse.CLASSID:protocolObject=new POKER_PROTOCOL.BuyInResponse();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.Pot.CLASSID:protocolObject=new POKER_PROTOCOL.Pot();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PotTransfer.CLASSID:protocolObject=new POKER_PROTOCOL.PotTransfer();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PotTransfers.CLASSID:protocolObject=new POKER_PROTOCOL.PotTransfers();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.TakeBackUncalledBet.CLASSID:protocolObject=new POKER_PROTOCOL.TakeBackUncalledBet();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.RakeInfo.CLASSID:protocolObject=new POKER_PROTOCOL.RakeInfo();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PlayerPokerStatus.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerPokerStatus();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PlayerHandStartStatus.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerHandStartStatus();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PlayerSitinRequest.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerSitinRequest();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PlayerSitoutRequest.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerSitoutRequest();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.DeckInfo.CLASSID:protocolObject=new POKER_PROTOCOL.DeckInfo();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.ExternalSessionInfoPacket.CLASSID:protocolObject=new POKER_PROTOCOL.ExternalSessionInfoPacket();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PlayerDisconnectedPacket.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerDisconnectedPacket();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PlayerReconnectedPacket.CLASSID:protocolObject=new POKER_PROTOCOL.PlayerReconnectedPacket();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PingPacket.CLASSID:protocolObject=new POKER_PROTOCOL.PingPacket();
protocolObject.load(gameData);
return protocolObject;
case POKER_PROTOCOL.PongPacket.CLASSID:protocolObject=new POKER_PROTOCOL.PongPacket();
protocolObject.load(gameData);
return protocolObject
}return null
};
POKER_PROTOCOL.RakeInfo=function(){this.classId=function(){return POKER_PROTOCOL.RakeInfo.CLASSID
};
this.totalPot={};
this.totalRake={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.totalPot);
byteArray.writeInt(this.totalRake);
return byteArray
};
this.load=function(byteArray){this.totalPot=byteArray.readInt();
this.totalRake=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.RakeInfo :";
result+=" total_pot["+this.totalPot.toString()+"]";
result+=" total_rake["+this.totalRake.toString()+"]";
return result
}
};
POKER_PROTOCOL.RakeInfo.CLASSID=30;
POKER_PROTOCOL.RankEnum=function(){};
POKER_PROTOCOL.RankEnum.TWO=0;
POKER_PROTOCOL.RankEnum.THREE=1;
POKER_PROTOCOL.RankEnum.FOUR=2;
POKER_PROTOCOL.RankEnum.FIVE=3;
POKER_PROTOCOL.RankEnum.SIX=4;
POKER_PROTOCOL.RankEnum.SEVEN=5;
POKER_PROTOCOL.RankEnum.EIGHT=6;
POKER_PROTOCOL.RankEnum.NINE=7;
POKER_PROTOCOL.RankEnum.TEN=8;
POKER_PROTOCOL.RankEnum.JACK=9;
POKER_PROTOCOL.RankEnum.QUEEN=10;
POKER_PROTOCOL.RankEnum.KING=11;
POKER_PROTOCOL.RankEnum.ACE=12;
POKER_PROTOCOL.RankEnum.HIDDEN=13;
POKER_PROTOCOL.RankEnum.makeRankEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.RankEnum.TWO;
case 1:return POKER_PROTOCOL.RankEnum.THREE;
case 2:return POKER_PROTOCOL.RankEnum.FOUR;
case 3:return POKER_PROTOCOL.RankEnum.FIVE;
case 4:return POKER_PROTOCOL.RankEnum.SIX;
case 5:return POKER_PROTOCOL.RankEnum.SEVEN;
case 6:return POKER_PROTOCOL.RankEnum.EIGHT;
case 7:return POKER_PROTOCOL.RankEnum.NINE;
case 8:return POKER_PROTOCOL.RankEnum.TEN;
case 9:return POKER_PROTOCOL.RankEnum.JACK;
case 10:return POKER_PROTOCOL.RankEnum.QUEEN;
case 11:return POKER_PROTOCOL.RankEnum.KING;
case 12:return POKER_PROTOCOL.RankEnum.ACE;
case 13:return POKER_PROTOCOL.RankEnum.HIDDEN
}return -1
};
POKER_PROTOCOL.RequestAction=function(){this.classId=function(){return POKER_PROTOCOL.RequestAction.CLASSID
};
this.currentPotSize={};
this.seq={};
this.player={};
this.allowedActions=[];
this.timeToAct={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.currentPotSize);
byteArray.writeInt(this.seq);
byteArray.writeInt(this.player);
byteArray.writeInt(this.allowedActions.length);
var i;
for(i=0;
i<this.allowedActions.length;
i++){byteArray.writeArray(this.allowedActions[i].save())
}byteArray.writeInt(this.timeToAct);
return byteArray
};
this.load=function(byteArray){this.currentPotSize=byteArray.readInt();
this.seq=byteArray.readInt();
this.player=byteArray.readInt();
var i;
var allowedActionsCount=byteArray.readInt();
var oPlayerAction;
this.allowedActions=[];
for(i=0;
i<allowedActionsCount;
i++){oPlayerAction=new POKER_PROTOCOL.PlayerAction();
oPlayerAction.load(byteArray);
this.allowedActions.push(oPlayerAction)
}this.timeToAct=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.RequestAction :";
result+=" current_pot_size["+this.currentPotSize.toString()+"]";
result+=" seq["+this.seq.toString()+"]";
result+=" player["+this.player.toString()+"]";
result+=" allowed_actions["+this.allowedActions.toString()+"]";
result+=" time_to_act["+this.timeToAct.toString()+"]";
return result
}
};
POKER_PROTOCOL.RequestAction.CLASSID=8;
POKER_PROTOCOL.StartHandHistory=function(){this.classId=function(){return POKER_PROTOCOL.StartHandHistory.CLASSID
};
this.save=function(){return[]
};
this.load=function(byteArray){};
this.toString=function(){var result="POKER_PROTOCOL.StartHandHistory :";
return result
}
};
POKER_PROTOCOL.StartHandHistory.CLASSID=17;
POKER_PROTOCOL.StartNewHand=function(){this.classId=function(){return POKER_PROTOCOL.StartNewHand.CLASSID
};
this.dealerSeatId={};
this.handId={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.dealerSeatId);
byteArray.writeString(this.handId);
return byteArray
};
this.load=function(byteArray){this.dealerSeatId=byteArray.readInt();
this.handId=byteArray.readString()
};
this.toString=function(){var result="POKER_PROTOCOL.StartNewHand :";
result+=" dealerSeatId["+this.dealerSeatId.toString()+"]";
result+=" handId["+this.handId.toString()+"]";
return result
}
};
POKER_PROTOCOL.StartNewHand.CLASSID=10;
POKER_PROTOCOL.StopHandHistory=function(){this.classId=function(){return POKER_PROTOCOL.StopHandHistory.CLASSID
};
this.save=function(){return[]
};
this.load=function(byteArray){};
this.toString=function(){var result="POKER_PROTOCOL.StopHandHistory :";
return result
}
};
POKER_PROTOCOL.StopHandHistory.CLASSID=18;
POKER_PROTOCOL.SuitEnum=function(){};
POKER_PROTOCOL.SuitEnum.CLUBS=0;
POKER_PROTOCOL.SuitEnum.DIAMONDS=1;
POKER_PROTOCOL.SuitEnum.HEARTS=2;
POKER_PROTOCOL.SuitEnum.SPADES=3;
POKER_PROTOCOL.SuitEnum.HIDDEN=4;
POKER_PROTOCOL.SuitEnum.makeSuitEnum=function(value){switch(value){case 0:return POKER_PROTOCOL.SuitEnum.CLUBS;
case 1:return POKER_PROTOCOL.SuitEnum.DIAMONDS;
case 2:return POKER_PROTOCOL.SuitEnum.HEARTS;
case 3:return POKER_PROTOCOL.SuitEnum.SPADES;
case 4:return POKER_PROTOCOL.SuitEnum.HIDDEN
}return -1
};
POKER_PROTOCOL.TakeBackUncalledBet=function(){this.classId=function(){return POKER_PROTOCOL.TakeBackUncalledBet.CLASSID
};
this.playerId={};
this.amount={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.playerId);
byteArray.writeInt(this.amount);
return byteArray
};
this.load=function(byteArray){this.playerId=byteArray.readInt();
this.amount=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.TakeBackUncalledBet :";
result+=" player_id["+this.playerId.toString()+"]";
result+=" amount["+this.amount.toString()+"]";
return result
}
};
POKER_PROTOCOL.TakeBackUncalledBet.CLASSID=29;
POKER_PROTOCOL.TournamentOut=function(){this.classId=function(){return POKER_PROTOCOL.TournamentOut.CLASSID
};
this.player={};
this.position={};
this.save=function(){var byteArray=new FIREBASE.ByteArray();
byteArray.writeInt(this.player);
byteArray.writeInt(this.position);
return byteArray
};
this.load=function(byteArray){this.player=byteArray.readInt();
this.position=byteArray.readInt()
};
this.toString=function(){var result="POKER_PROTOCOL.TournamentOut :";
result+=" player["+this.player.toString()+"]";
result+=" position["+this.position.toString()+"]";
return result
}
};
POKER_PROTOCOL.TournamentOut.CLASSID=20;

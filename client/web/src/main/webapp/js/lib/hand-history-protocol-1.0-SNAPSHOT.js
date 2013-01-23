// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

var com = com || {};
com.cubeia = com.cubeia || {};
com.cubeia.games = com.cubeia.games || {};
com.cubeia.games.poker = com.cubeia.games.poker || {};
com.cubeia.games.poker.handhistoryservice = com.cubeia.games.poker.handhistoryservice || {};
com.cubeia.games.poker.handhistoryservice.io = com.cubeia.games.poker.handhistoryservice.io || {};
com.cubeia.games.poker.handhistoryservice.io.protocol = com.cubeia.games.poker.handhistoryservice.io.protocol || {};


com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequest=function(){this.classId=function(){return com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequest.CLASSID};this.tableId={};this.time={};this.save=function(){var a=new FIREBASE.ByteArray();a.writeInt(this.tableId);a.writeString(this.time);return a};this.load=function(a){this.tableId=a.readInt();this.time=a.readString()};this.getNormalizedObject=function(){var a={};var b;a.summary="com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequest";a.details={};a.details.tableId=this.tableId;a.details.time=this.time;return a}};com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequest.CLASSID=1;com.cubeia.games.poker.handhistoryservice.io.protocol.ProtocolObjectFactory={};com.cubeia.games.poker.handhistoryservice.io.protocol.ProtocolObjectFactory.create=function(c,a){var b;switch(c){case com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequest.CLASSID:b=new com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequest();b.load(a);return b}return null};

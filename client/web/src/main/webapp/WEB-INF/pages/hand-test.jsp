<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <title></title>

    <meta name="viewport" content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="default">

    <link rel="apple-touch-icon" href="${cp}/skins/${skin}/images/lobby/icon.png" />
    <link rel="stylesheet" type="text/css" href="${cp}/skins/default/css/gritter/css/jquery.gritter.css"/>
    <link rel="stylesheet" type="text/css" href="${cp}/skins/default/css/browser-support.css"/>
    <link rel="stylesheet/less" type="text/css" href="${cp}/js/lib/bootstrap/less/bootstrap.less"/>

    <link id="defaultSkinCss" rel="stylesheet/less" type="text/css" href="${cp}/skins/default/less/base.less" />

    <!-- All less files are imported in this base.less-->
    <link id="skinCss" rel="stylesheet/less" type="text/css" href="${cp}/skins/${skin}/less/base.less" />

    <c:if test="${not empty cssOverride}">
        <link id="overrideSkinCss" rel="stylesheet/less" type="text/css" href="${cssOverride}" />
    </c:if>

    <script type="text/javascript" src="${cp}/skins/${skin}/skin-config.js"></script>
    <script type="text/javascript" src="${cp}/skins/${skin}/preload-images.js"></script>

    <script type="text/javascript"  src="${cp}/js/lib/less-1.4.1.min.js"></script>

    <script type="text/javascript" src="${cp}/js/lib/classjs.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/modernizr-2.6.2-custom.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery.ui.touch-punch.js"></script>
    <script type="text/javascript" src="${cp}/js/base/jquery-plugins/touch-click.js"></script>
    <script type="text/javascript" src="${cp}/js/base/jquery-plugins/relative-offset.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery.nicescroll.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/moment.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/purl.js"></script>


    <script type="text/javascript" src="${cp}/js/lib/handlebars.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/json2.js"></script>

    <script type="text/javascript" src="${cp}/js/lib/org/cometd.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/CanvasProgressbar.js"></script>

    <script type="text/javascript" src="${cp}/js/lib/cubeia/firebase-js-api-1.10.0-javascript.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/cubeia/firebase-protocol-1.10.0-javascript.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/poker-protocol-1.0-SNAPSHOT.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/routing-service-protocol-1.0-SNAPSHOT.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/quo.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/i18next-1.6.0.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery.gritter.js"></script>


    <script type="text/javascript" src="${cp}/js/lib/PxLoader-0.1.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/PxLoaderImage-0.1.js"></script>



    <script src="${cp}/js/base/Utils.js" type="text/javascript"></script>
    <script src="${cp}/js/base/ProtocolUtils.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/base/data/Map.js"></script>
    <script type="text/javascript" src="${cp}/js/base/PeriodicalUpdater.js"></script>
    <script type="text/javascript" src="${cp}/js/base/OperatorConfig.js"></script>
    <script type="text/javascript" src="${cp}/js/base/MyPlayer.js"></script>
    <script type="text/javascript" src="${cp}/js/base/PlayerTableStatus.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/NotificationsManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/AchievementManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/achievement/AchievementPacketHandler.js"></script>
    <script type="text/javascript" src="${cp}/js/base/TimeStatistics.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/PingManager.js"></script>


    <script src="${cp}/js/base/communication/poker-game/ActionUtils.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerPacketHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerRequestHandler.js"  type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerSequence.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/lobby/LobbyPacketHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/lobby/LobbyRequestHandler.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/handhistory/HandHistoryRequestHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/handhistory/HandHistoryPacketHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/ui/HandHistoryLayout.js" type="text/javascript"></script>
    <script src="${cp}/js/base/HandHistoryManager.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/connection/ConnectionManager.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/connection/ConnectionPacketHandler.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/tournament/TournamentPacketHandler.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/tournament/TournamentRequestHandler.js"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/table/TableRequestHandler.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/table/TablePacketHandler.js"></script>

    <script src="${cp}/js/base/communication/CommunicationManager.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/player-api/PlayerApi.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/profile/Profile.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/profile/MyProfile.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/profile/ProfileManager.js"></script>

    <script type="text/javascript" src="${cp}/js/base/Settings.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/BetSlider.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Action.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/CheckBoxAction.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/BlindsActions.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/ActionButton.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/AbstractTableButtons.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/ActionButtons.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/TableButtons.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/MyActionsManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/data/LobbyData.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/BasicMenu.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/LobbyLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/LobbyManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Player.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Table.js"></script>
    <script type="text/javascript" src="${cp}/js/base/TableManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Clock.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/PotTransferAnimator.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Log.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TableEventLog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/ChatInput.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TableLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TemplateManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/cards/PlayerHand.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Seat.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/MyPlayerSeat.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/cards/Card.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/cards/CommunityCard.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Pot.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Hand.js"></script>
    <script type="text/javascript" src="${cp}/js/base/jquery-plugins/describe.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/CSSUtils.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/Transform.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/Animation.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/CSSClassAnimation.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/TransformAnimation.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/AnimationManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/DealerButton.js"></script>

    <script type="text/javascript" src="${cp}/js/base/Navigation.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundSource.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundPlayer.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundRepository.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/Sounds.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/FutureActionType.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/FutureActions.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Dialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/DialogManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/DisconnectDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/BuyInDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TournamentBuyInDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/View.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TabView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ResponsiveTabView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/LoginView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TableView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/MultiTableView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TournamentView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/SoundSettingsView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/DevSettingsView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ViewManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ExternalPageView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/MainMenuManager.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/views/AccountPageManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ApplicationContext.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ViewSwiper.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/ContextMenu.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Sharing.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Pager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/tournaments/TournamentList.js"></script>
    <script type="text/javascript" src="${cp}/js/base/tournaments/Tournament.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/tournaments/TournamentLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/tournaments/TournamentManager.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ResourcePreLoader.js"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/lobby/Unsubscribe.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/cards/DynamicHand.js"></script>
    <script type="text/javascript" src="${cp}/js/base/dev/MockEventManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/dev/PositionEditor.js"></script>
    <script type="text/javascript" src="${cp}/js/base/dev/DevTools.js"></script>

    <script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=${addThisPubId}"></script>
    <script type="text/javascript" src="${cp}/js/base/cs-leaderboard.js"></script>


    <c:if test="${not empty operatorId}">
        <script type="text/javascript">
            Poker.OperatorConfig.operatorId = ${operatorId};
            Poker.SkinConfiguration.operatorId = ${operatorId};
        </script>
    </c:if>
    <c:if test="${not empty token}">
        <script type="text/javascript">
            Poker.MyPlayer.loginToken = "${token}";
            Poker.MyPlayer.pureToken = ${pureToken};
        </script>
    </c:if>

    <script type="text/javascript">

        var contextPath = "${cp}";

        $(document).ready(function(){

             var dynamicHand = new Poker.DynamicHand($(".hand-container"));
             var cards = [];
             cards.push(new Poker.Card(1,1,"as",new Poker.TemplateManager()));
             cards.push(new Poker.Card(2,1,"as",new Poker.TemplateManager()));
             cards.push(new Poker.Card(3,1,"as",new Poker.TemplateManager()));

            $.each(cards,function(i,card){
                setTimeout(function(){
                    $(".hand-container").append(card.render(i));
                    dynamicHand.addCard(card);
                },50);
            });
        });



    </script>
<style>

    .card-image {
        position: absolute;
        top:0;
        left:0;
        width:38%;
        height:100%;
        -webkit-transition: -webkit-transform 0.5s;

    }
</style>

</head>
<body style="background-color: #333;">
    <div class="hand-container" style=" position:absolute; top: 10%; left: 10%; border: 1px solid #ff0000; width:20%; height: 20%;">
    </div>

    <script type="text/mustache" id="playerCardTemplate" style="display: none;">
        <img class="card-image" src="{{backgroundImage}}" id="{{domId}}" style=""/>
    </script>

</body>
</html>

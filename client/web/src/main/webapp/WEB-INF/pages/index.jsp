<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Cubeia Poker</title>

    <meta name="viewport" content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <link rel="apple-touch-icon" href="${cp}/skins/${skin}/images/lobby/icon.png" />

    <!-- All less files are imported in this base.less-->
    <link id="skinCss" rel="stylesheet/less" type="text/css" href="${cp}/skins/${skin}/less/base.less" />

    <c:if test="${not empty cssOverride}">
        <link id="overrideSkinCss" rel="stylesheet/less" type="text/css" href="${cssOverride}" />
    </c:if>

    <script type="text/javascript" src="${cp}/skins/${skin}/skin-config.js"></script>
    <script type="text/javascript" src="${cp}/skins/${skin}/preload-images.js"></script>

    <script type="text/javascript"  src="${cp}/js/lib/less-1.3.0.min.js"></script>

    <script type="text/javascript" src="${cp}/js/lib/classjs.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery.ui.touch-punch.js"></script>
    <script type="text/javascript" src="${cp}/js/base/jquery-plugins/touch-click.js"></script>
    <script type="text/javascript" src="${cp}/js/base/jquery-plugins/relative-offset.js"></script>

    <script type="text/javascript" src="${cp}/js/lib/mustache.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery.jqGrid.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/json2.js"></script>


    <script type="text/javascript" src="${cp}/js/lib/facebox/facebox.js"></script>
    <script type="text/javascript">
        $.facebox.settings.closeImage = '${cp}/skins/${skin}/images/global/close.png';
        $.facebox.settings.loadingImage = '${cp}/skins/${skin}/images/global/close.png';
    </script>

    <script type="text/javascript" src="${cp}/js/base/ui/CircularProgressBar.js"></script>

    <script src="${cp}/js/lib/cubeia/firebase-js-api-1.9.2-CE-javascript.js" type="text/javascript"></script>
    <script src="${cp}/js/lib/cubeia/firebase-protocol-1.9.2-CE-javascript.js" type="text/javascript"></script>
    <script src="${cp}/js/lib/poker-protocol-1.0-SNAPSHOT.js" type="text/javascript"></script>
    <script src="${cp}/js/lib/hand-history-protocol-1.0-SNAPSHOT.js" type="text/javascript"></script>
    <script src="${cp}/js/lib/quo.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/lib/PxLoader-0.1.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/PxLoaderImage-0.1.js"></script>


    <script src="${cp}/js/base/Utils.js" type="text/javascript"></script>
    <script src="${cp}/js/base/ProtocolUtils.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/base/data/Map.js"></script>
    <script type="text/javascript" src="${cp}/js/base/PeriodicalUpdater.js"></script>
    <script type="text/javascript" src="${cp}/js/base/OperatorConfig.js"></script>
    <script type="text/javascript" src="${cp}/js/base/MyPlayer.js"></script>
    <script type="text/javascript" src="${cp}/js/base/PlayerTableStatus.js"></script>

    <script src="${cp}/js/base/communication/poker-game/ActionUtils.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerPacketHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerRequestHandler.js"  type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerSequence.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/lobby/LobbyPacketHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/lobby/LobbyRequestHandler.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/handhistory/HandHistoryPacketHandler.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/connection/ConnectionManager.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/connection/ConnectionPacketHandler.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/tournament/TournamentPacketHandler.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/tournament/TournamentRequestHandler.js"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/table/TableRequestHandler.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/table/TablePacketHandler.js"></script>

    <script src="${cp}/js/base/communication/CommunicationManager.js" type="text/javascript"></script>
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
    <script type="text/javascript" src="${cp}/js/base/ui/LobbyLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/LobbyManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Player.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Table.js"></script>
    <script type="text/javascript" src="${cp}/js/base/TableManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Clock.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/PotTransferAnimator.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TableLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TemplateManager.js"></script>
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
    <script type="text/javascript" src="${cp}/js/base/sound/SoundManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundRepository.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/Sounds.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/FutureActionType.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/FutureActions.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/DialogManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/DisconnectDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/BuyInDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TournamentBuyInDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/View.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TabView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/LoginView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TableView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/MultiTableView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TournamentView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/DevSettingsView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ViewManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/MainMenuManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ApplicationContext.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ViewSwiper.js"></script>
    <script type="text/javascript" src="${cp}/js/base/tournaments/Tournament.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/tournaments/TournamentLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/tournaments/TournamentManager.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ResourcePreLoader.js"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/lobby/Unsubscribe.js"></script>


    <script type="text/javascript" src="${cp}/js/base/dev/MockEventManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/dev/PositionEditor.js"></script>
    <script type="text/javascript" src="${cp}/js/base/dev/DevTools.js"></script>



    <c:if test="${not empty operatorId}">
        <script type="text/javascript">
            Poker.SkinConfiguration.operatorId = ${operatorId};
            Poker.MyPlayer.loginToken = "${token}";
            $(document).ready(function(){
                $(".login-container").hide();
            });
        </script>
    </c:if>
    <script type="text/javascript">

        <c:set var="CUBEIA_CLASSIC" value="cubeiaclassic"/>
        <c:choose>
            <c:when test="${skin eq CUBEIA_CLASSIC}">
                var currentSkin = "${CUBEIA_CLASSIC}";
                function changeSkin() {
                    if(currentSkin == "${CUBEIA_CLASSIC}") {
                        currentSkin = "cubeia";
                    } else {
                        currentSkin = "${CUBEIA_CLASSIC}";
                    }

                   $("#skinCss").attr("href","${cp}/skins/" + currentSkin + "/lcss/base.css");
                }
            </c:when>
            <c:otherwise>
                function changeSkin() {
                    $("body").toggleClass("skin-classic");
                }
            </c:otherwise>
        </c:choose>
    </script>
    <script type="text/javascript">

        var contextPath = "${cp}";

        $(document).ready(function(){
            //to clear the stored user add #clear to the url
            if(document.location.hash.indexOf("clear")!=-1){
                Poker.Utils.removeStoredUser();
            }

            less.watch(); //development only
            $(".describe").describe();


            //TODO: remove later, probably wont be a button in the toolbar
            $("#skinButton").click(function(e){
                changeSkin();
                $("#classic").toggle();
                $("#modern").toggle();
            });

            var onPreLoadComplete = function() {
                var requestHost = window.location.hostname;
                var webSocketUrl = requestHost ? requestHost : "localhost";
                var webSocketPort = 9191;

                console.log("connecting to WS: " + webSocketUrl + ":" + webSocketPort);

                //handles the lobby UI
                Poker.AppCtx.wire({
                    webSocketUrl : webSocketUrl,
                    webSocketPort : webSocketPort,
                    tournamentLobbyUpdateInterval : 10000
                });

                $(".logout-link").click(function(){
                    Poker.AppCtx.getCommunicationManager().getConnector().logout();
                    document.location = document.location.hash = "clear";
                    document.location.reload();
                });
            };

            new Poker.ResourcePreloader('${cp}',onPreLoadComplete,  Poker.SkinConfiguration.preLoadImages);



        });

    </script>

</head>
<body>
<div class="view-port">
    <div id="toolbar" style="display:none;">
        <div class="main-menu-button">
        </div>
        <div class="tabs-container">
            <ul id="tabItems" class="tabs">
            </ul>
        </div>
        <div class="skin-button" id="skinButton">
            <div id="classic" style="display:none;">Classic</div>
            <div id="modern">Modern</div>
        </div>
    </div>
    <div class="toolbar-background"></div>
    <div class="main-menu-container" style="">
        <ul id="mainMenuList">

        </ul>
    </div>
    <div class="menu-overlay slidable" style="display: none;">

    </div>
    <div id="devSettingsView" class="config-view" style="display: none;">
        <h1>Development config</h1>
        <h2>Communication</h2>
        <div class="group">
            <div class="item">
                <fieldset class="toggle">
                    <input  id="freezeComEnabled" type="checkbox">
                    <label onclick="" for="freezeComEnabled">Freeze communication</label>
                    <span class="toggle-button"></span>
                </fieldset>
            </div>
        </div>
        <h2>Experimental features</h2>
        <div class="group">
            <div class="item">
                <fieldset class="toggle">
                    <input id="swipeEnabled" type="checkbox">
                    <label onclick="" for="swipeEnabled">Swipe to change tabs</label>
                    <span class="toggle-button"></span>
                </fieldset>
            </div>
            <div class="item">
                <span>Something else goes here</span>
            </div>
        </div>
    </div>
    <div class="view-container slidable">
        <div class="table-view-container" style="display:none;">
            <div class="multi-view-switch multi">
            </div>
        </div>
        <div id="loadingView" class="loading-view">
            <div class="login-dialog">
                <div class="logo-container"><img src="${cp}/skins/${skin}/images/lobby/poker-logo.png"/></div>
                <div class="loading-progressbar">
                    <div class="progress"></div>
                </div>
            </div>
        </div>
        <div id="loginView" class="login-view" style="display:none;">
            <div id="dialog1" class="login-dialog">
                <div class="logo-container"><img src="${cp}/skins/${skin}/images/lobby/poker-logo.png"/></div>
                <div class="login-container">
                    <div class="login-input-container">
                        <input name="user" class="describe" id="user" type="text" title="Username" value="" />
                        <input name="pwd" class="describe" id="pwd" type="password" title="Password" value=""/>
                    </div>
                    <div id="loginButton" class="login-button">
                        <span>login</span>
                    </div>
                </div>
                <div class="status-label">Status: <span class="connect-status"></span></div>
            </div>
        </div>

        <div id="lobbyView" class="lobby-container"  style="display:none;">
            <div id="lobby" class="lobby-list">

                <div class="left-column">
                    <div class="logo-container">
                        <img src="${cp}/skins/${skin}/images/lobby/poker-logo.png"/>
                    </div>
                    <ul class="main-menu">
                            <li><a class="selected lobby-link" id="cashGameMenu" data->Cash Games</a></li>
                            <li><a id="sitAndGoMenu" class="lobby-link">Sit &amp; Go's</a></li>
                            <li><a  id="tournamentMenu" class="lobby-link">Tournaments</a></li>
                    </ul>
                </div>
                <div class="right-column">
                    <div class="top-panel" id="table-list">
                        <div class="user-panel">
                            <span class="status">Logged in: </span>
                            <span id="username"></span> (<span id="userId"></span>)
                            <a class="logout-link">Log out</a>
                        </div>
                        <div class="show-filters">
                            <a>Show filters</a>
                        </div>
                        <div class="table-filter">
                            <div class="filter-group tables">
                                <div class="filter-label">Show tables:</div>
                                <div class="filter-button" id="fullTables">Full</div>
                                <div class="filter-button" id="emptyTables">Empty</div>
                            </div>
                            <div class="filter-group limits">
                                <div class="filter-label">Show Limits:</div>
                                <div class="filter-button" id="noLimit">NL</div>
                                <div class="filter-button" id="potLimit">PL</div>
                                <div class="filter-button" id="fixedLimit">FL</div>
                            </div>
                            <div class="filter-group stakes">
                                <div class="filter-label">Stakes:</div>
                                <div class="filter-button" id="lowStakes">Low</div>
                                <div class="filter-button" id="mediumStakes">Mid</div>
                                <div class="filter-button" id="highStakes">High</div>
                            </div>
                        </div>
                    </div>
                        <div class="lobby-tab"  id="tableListAnchor">
                        <div id="tableListContainer">

                        </div>
                    </div>
                </div>

            </div>

        </div>
    </div>
</div>
<div id="emptySeatTemplate" style="display: none;">
    <div class="avatar-base">
        <div class="open-seat">Open Seat</div>
    </div>
</div>
<div id="seatTemplate" style="display: none;">

    <div class="avatar-base">
        <div class="progress-bar">

        </div>
    </div>
    <div class="player-name">
        {{name}}
    </div>
    <div class="avatar">

    </div>
    <div class="cards-container cards-container-player">

    </div>

    <div class="player-status">
        Sitting out
    </div>
    <div class="seat-balance balance">

    </div>
    <div class="action-text">
        Small Blind
    </div>
    <div class="action-amount balance">
        <span></span>
    </div>
    <div class="hand-strength">

    </div>
</div>

<script type="text/mustache" id="playerCardTemplate" style="display: none;">
    <div id="playerCard-{{domId}}" class="player-card-container">
        <img src="${cp}/skins/${skin}/images/cards/{{cardString}}.svg" class="player-card"/>
    </div>
</script>
<script type="text/mustache" id="communityCardTemplate" style="display: none;">
    <div id="communityCard-{{domId}}" class="community-card-container">
        <img src="${cp}/skins/${skin}/images/cards/{{cardString}}.svg" class="player-card"/>
    </div>
</script>
<div id="mainPotTemplate" style="display: none;">
        <div class="balance pot-container-{{potId}}">&euro;<span class="pot-value pot-{{potId}}">{{amount}}</span></div>
</div>
<div id="myPlayerSeatTemplate" style="display:none;">
        <div class="player-name">
            {{name}}
        </div>
        <div class="seat-balance balance">

        </div>
    <div class="avatar-base">

    </div>
    <div class="avatar">

    </div>
    <div class="action-amount balance">

    </div>
    <div class="cards-container">

    </div>
    <div class="action-text">

    </div>
    <div class="hand-strength">

    </div>
</div>


<script type="text/mustache" id="sitAndGoLobbyListTemplate">
    <div class="table-item-header sit-and-go">
        <div class="table-name">Name</div>
        <div class="buy-in">Buy-in</div>
        <div class="seated">Players</div>
        <div class="status">Status</div>
    </div>

    <div class="table-list-item-container">

    </div>
</script>

<script type="text/mustache" id="tournamentLobbyListTemplate">
    <div class="table-item-header tournament">
        <div class="table-name">Name</div>
        <div class="buy-in">Buy-in</div>
        <div class="registered">Players</div>
        <div class="group">Starting</div>
    </div>

    <div class="table-list-item-container">

    </div>
</script>

<script type="text/mustache" id="tableLobbyListTemplate">
    <div class="table-item-header">
        <div class="table-name">name</div>
        <div class="seated">seated</div>
        <div class="blinds">blinds</div>
        <div class="type">type</div>
        <div class="play"></div>
    </div>

    <div class="table-list-item-container">

    </div>
</script>

<div id="tableListItemTemplate" style="display: none;">
    <div class="table-item  {{tableStatus}}" id="tableItem{{id}}">
        <div class="table-name">{{name}}</div>
        <div class="seated">{{seated}}/{{capacity}}</div>
        <div class="blinds">{{blinds}}</div>
        <div class="type">{{type}}</div>
        <div class="play-text">&raquo;</div>
        <div class="full-text">Full</div>
    </div>
</div>
<div id="sitAndGoListItemTemplate" style="display: none;">
    <div class="table-item sit-and-go  {{tableStatus}}" id="sitAndGoItem{{id}}">
        <div class="table-name">{{name}}</div>
        <div class="buy-in">{{buyIn}}+{{fee}}</div>
        <div class="seated">{{registered}}/{{capacity}}</div>
        <div class="type">{{type}}</div>
        <div class="status {{status}}">{{status}}</div>
        <div class="play-text">&raquo;</div>
    </div>
</div>
<div id="tournamentListItemTemplate" style="display: none;">
    <div class="table-item tournament {{tableStatus}}" id="tournamentItem{{id}}">
        <div class="table-name">{{name}}</div>
        <div class="buy-in">{{buyIn}}+{{fee}}</div>
        <div class="registered">{{registered}}</div>
        <div class="group">
            <div class="start-time">{{startTime}}</div>
            <div class="status {{status}}">{{status}}</div>
        </div>
        <div class="play-text">&raquo;</div>
    </div>
</div>
<div id="potTransferTemplate" style="display: none;">
        <div id="{{ptId}}" class="pot-transfer" style="visibility: hidden;">
        <div class="balance">&euro;{{amount}}</div>
    </div>
</div>

<script type="text/mustache" id="tableViewTemplate" style="display:none;">
    <div id="tableView-{{tableId}}" class="table-container">

        <div class="table-logo"></div>
        <div id="seatContainer-{{tableId}}" class="default-table table-10">
            <div class="seat" id="seat0-{{tableId}}">

            </div>
            <div class="seat" id="seat1-{{tableId}}">

            </div>
            <div class="seat" id="seat2-{{tableId}}">

            </div>
            <div class="seat" id="seat3-{{tableId}}">

            </div>
            <div class="seat" id="seat4-{{tableId}}">

            </div>
            <div class="seat" id="seat5-{{tableId}}">

            </div>
            <div class="seat" id="seat6-{{tableId}}">

            </div>
            <div class="seat" id="seat7-{{tableId}}">

            </div>
            <div class="seat" id="seat8-{{tableId}}">

            </div>
            <div class="seat" id="seat9-{{tableId}}">

            </div>
            <div class="my-player-seat" id="myPlayerSeat-{{tableId}}">

            </div>
                <div class="click-area-0">

                </div>
            <div class="table-info" style="display:none;">
                <div class="blinds">
                    Blinds: <span class="table-blinds-value value">10/20</span>
                </div>
                <div class="tournament-info">
                    <div class="time-to-next-level">
                        Time to next level: <span class="time-to-next-level-value time">10:00</span>
                    </div>
                </div>
            </div>
            <div class="community-cards">

            </div>
                <div class="total-pot">
                    Pot: <span>&euro;<span class="amount"></span></span>
                </div>
            <div class="main-pot">

            </div>
            <div class="dealer-button" style="display:none;">
                <img src="${cp}/skins/${skin}/images/table/dealer-button.svg"/>
            </div>
        </div>
        <div class="bottom-bar">
                <div class="action-button action-leave" style="display: none;">
                    <span>Leave</span>
            </div>
            <div class="own-player" id="myPlayerSeat-{{tableId}}Info" style="display:none;">
                <div class="name" id="myPlayerName-{{tableId}}"></div>
                <div class="balance" id="myPlayerBalance-{{tableId}}"></div>
                <div class="no-more-blinds">
                    <input class="checkbox" type="checkbox" id="noMoreBlinds-{{tableId}}"/>
                    <label class="checkbox-icon-label" for="noMoreBlinds-{{tableId}}">No more blinds</label>
                </div>
                    <div class="sit-out-next-hand">
                        <input class="checkbox" type="checkbox" id="sitOutNextHand-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="sitOutNextHand-{{tableId}}">Sit out next hand</label>
                    </div>
            </div>


            <div id="userActActions-{{tableId}}" class="user-actions">
                <div class="action-button action-fold"  style="display: none;">
                    <span>Fold</span>
                </div>
                <div class="action-button action-call" style="display: none;">
                    <span class="amount"></span>
                    <span>Call</span>
                </div>
                <div class="action-button action-check"  style="display: none;">
                    <span>Check</span>
                </div>
                <div class="action-button action-raise" style="display: none;">
                    <span class="amount"></span>
                    <span>Raise</span>
                </div>
                <div class="action-button action-bet"  style="display: none;">
                    <span class="amount"></span>
                    <span>Bet</span>
                </div>
                <div class="action-button action-big-blind"  style="display: none;">
                    <span class="amount"></span>
                    <span>Big Blind</span>
                </div>
                <div class="action-button action-small-blind"  style="display: none;">
                    <span class="amount"></span>
                    <span>Small Blind</span>
                </div>
                <div class="action-button action-cancel-bet" style="display:none;">
                    <span>Cancel</span>
                </div>
                <div class="action-button do-action-bet" style="display:none;">
                    <span class="slider-value amount"></span>
                    <span>Bet</span>
                </div>
                <div class="action-button do-action-raise" style="display:none;">
                    <span class="slider-value amount"></span>
                    <span>Raise to</span>
                </div>
                <div class="action-button fixed-action-bet" style="display:none;">
                    <span class="amount"></span>
                    <span>Bet</span>
                </div>
                <div class="action-button fixed-action-raise" style="display:none;">
                    <span class="amount"></span>
                    <span>Raise to</span>
                </div>
                <div class="action-button action-join"style="display: none;">
                    <span>Join</span>
                </div>

                <div class="action-button action-sit-in" style="display: none;">
                    <span>Sit-in</span>
                </div>
                <div class="action-button action-hhl" style="display: none;">
                    <span>HHL</span>
                </div>
            </div>
            <div id="futureActions-{{tableId}}" class="future-actions" style="display:none;">
                    <div class="future-action check" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-check-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-check-{{tableId}}">Fold</label>
                    </div>

                    <div class="future-action check-or-fold" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-check-or-fold-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-check-or-fold-{{tableId}}">Check/Fold</label>
                    </div>

                    <div class="future-action call-current-bet" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-call-current-bet-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-call-current-bet-{{tableId}}">Call <span class="amount"></span></label>
                    </div>

                    <div class="future-action check-or-call-any" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-check-or-call-any-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-check-or-call-any-{{tableId}}">Check/Call any</label>
                    </div>
                    <div class="future-action call-any" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-call-any-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-call-any-{{tableId}}">Call any</label>
                    </div>

                    <div class="future-action fold" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-fold-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-fold-{{tableId}}">Fold</label>
                    </div>

                    <div class="future-action raise" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-raise-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-raise-{{tableId}}">Raise to <span class="amount"></span></label>
                    </div>

                    <div class="future-action raise-any" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-raise-any-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-raise-any-{{tableId}}">Raise any</label>
                    </div>

            </div>
            <div id="waitForBigBlind-{{tableId}}" class="wait-for-big-blind" style="display:none;">
                <input class="checkbox" type="checkbox" id="wait-for-big-blind-cb-{{tableId}}" checked="checked"/>
                <label class="checkbox-icon-label" for="wait-for-big-blind-cb-{{tableId}}">Wait for Big Blind</label>
                <div>Uncheck to post the Big Blind and be dealt in next hand </div>
            </div>
        <div id="myPlayerSeat-{{tableId}}Progressbar" class="circular-progress-bar">

        </div>

    </div>
</div>
</script>
<div id="disconnectDialog" style="display: none;">
    <h1>You have been disconnected</h1>
    <p class="message disconnect-reconnecting">
        Trying to reconnect (attempt <span class="reconnectAttempt"></span>)
        <br/>
        <br/>
    </p>
    <p class="stopped-reconnecting" style="display: none;">
        Unable to reconnect
    </p>
    <p class="dialog-buttons stopped-reconnecting" style="display: none;">
            <a class="dialog-ok-button">
            Reload
        </a>
    </p>
</div>
<div id="buyInDialog" style="display: none;">
</div>
<div id="genericDialog" style="display: none;">
    <h1>Header</h1>
    <p class="message">Message</p>
    <p class="dialog-buttons">
            <a class="dialog-cancel-button" style="display:none;">
                Cancel
            </a>
            <a class="dialog-ok-button">
                Continue
            </a>
    </p>

    </div>
    <script type="text/mustache" id="tournamentBuyInContent">
        <h1>Buy-in at {{name}}</h1>
        <div class="buy-in-row">
            <span class="desc">Your balance:</span>  <span class="balance buyin-balance">{{balance}}</span>
        </div>
        <div class="buy-in-row">
            <span class="desc">Buy-in</span>  <span class="balance buyin-max-amount">{{buyIn}}+{{fee}}</span>
        </div>
        <div class="buy-in-row">
            <span class="buyin-error" style="display: none;"></span>
        </div>
        <p class="dialog-buttons">
            <a class="dialog-cancel-button">
            Cancel
        </a>
            <a class="dialog-ok-button">
            Buy in
        </a>
    </p>
</script>
<script type="text/mustache" id="cashGamesBuyInContent">
    <h1>Buy-in at table <span class="buyin-table-name">{{title}}</span></h1>
    <div class="buy-in-row">
        <span class="desc">Your balance:</span>  <span class="balance buyin-balance">{{balance}}</span>
    </div>
    <div class="buy-in-row">
        <span class="desc">Max amount:</span>  <span class="balance buyin-max-amount">{{maxAmount}}</span>
    </div>
    <div class="buy-in-row">
        <span class="desc">Min amount:</span>  <span class="balance buyin-min-amount">{{minAmount}}</span>
    </div>
    <div class="buy-in-row">
        <span class="desc">Buy-in amount:</span>
        <input type="text" class="buyin-amount dialog-input" value="" />
    </div>
    <div class="buy-in-row">
        <span class="buyin-error" style="display: none;"></span>
    </div>
    <p class="dialog-buttons">
            <a class="dialog-cancel-button">
            Cancel
        </a>
            <a  class="dialog-ok-button">
            Buy in
        </a>
    </p>
</script>
<script type="text/mustache" id="menuItemTemplate">
    <li class="{{cssClass}}">
        <div class="icon">
        </div>
        <div class="text">
            {{title}}
            <span class="description">{{description}}</span>
        </div>
    </li>

</script>
<script type="text/mustache" id="tabTemplate">
    <li>
        <div class="tab-content">
            <div class="mini-cards"></div>
            <span class="name">{{name}}</span>
        </div>
    </li>
</script>
<script type="text/mustache" id="miniCardTemplate" style="display: none;">
    <div id="miniCard-{{domId}}" class="mini-card-container">
        <img src="${cp}/skins/${skin}/images/cards/{{cardString}}.svg"/>
    </div>
</script>
<script type="text/mustache" id="tournamentTemplate" style="display:none;">
    <div id="tournamentView{{tournamentId}}" class="tournament-view">
        <div class="top-row">
            <h3>
                {{name}}
                <span class="tournament-start-date"></span>
            </h3>
            <a class="register-button leave-action">Close</a>
            <a class="register-button register-action">Register</a>
            <a class="register-button unregister-action">Unregister</a>
            <a class="register-button take-seat-action">Go to table</a>
            <a class="register-button loading-action">Please wait...</a>
        </div>
        <div class="lobby-data-container">
            <div class="column column-3">
                <div class="tournament-info-container">
                    <div class="info-section tournament-info"></div>
                    <div class="info-section tournament-stats"></div>
                    <div class="info-section payout-structure"></div>
                    <div class="info-section blinds-structure"></div>

                </div>
            </div>
            <div class="column column-3-2">
                <div class="tournament-info-container">
                    <div class="info-section registered-players">
                        <h4>Players</h4>
                        <table class="player-list">
                            <thead>
                            <tr>
                                <th colspan="2">Player</th>
                                <th>Stack</th>
                                <th>Winnings</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td colspan="4">Loading Players...</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</script>
<script type="text/mustache" id="tournamentInfoTemplate">
    <h4>Tournament Info</h4>
    <div class="stats-item"><span>{{gameType}}</span></div>
    <div class="stats-item">Buy-in: <span>{{buyIn}}+{{fee}}</span></div>
    <div class="stats-item">
        Status:
            <span class="status-container">
                <span class="status-0-{{tournamentStatus}}">ANNOUNCED</span>
                <span class="status-1-{{tournamentStatus}}">REGISTERING</span>
                <span class="status-2-{{tournamentStatus}}">RUNNING</span>
                <span class="status-3-{{tournamentStatus}}">ON BREAK</span>
                <span class="status-4-{{tournamentStatus}}">ON BREAK</span>
                <span class="status-5-{{tournamentStatus}}">FINISHED</span>
                <span class="status-6-{{tournamentStatus}}">CANCELLED</span>
                <span class="status-7-{{tournamentStatus}}">CLOSED</span>
            </span>
    </div>
    {{#sitAndGo}}
    <div class="stats-item">Players: <span>{{minPlayers}}</span></div>
    {{/sitAndGo}}
    {{^sitAndGo}}
    <div class="stats-item">Max Players: <span>{{maxPlayers}}</span></div>
    <div class="stats-item">Min Players: <span>{{minPlayers}}</span></div>
    <div class="stats-item">Registration Starts: <span><br/>{{registrationStartTime}}</span></div>
    {{/sitAndGo}}


</script>
<script type="text/mustache" id="tournamentStatsTemplate">
    <h4>Statistics</h4>
    <div class="stats-item">Max Stack: <span>{{chipStatistics.maxStack}}</span></div>
    <div class="stats-item">Min Stack: <span>{{chipStatistics.minStack}}</span></div>
    <div class="stats-item">Average Stack: <span>{{chipStatistics.averageStack}}</span></div>
    <div class="stats-item">Current Level: <span>{{levelInfo.currentLevel}}</span></div>
    <div class="stats-item">Players Left: <span>{{playersLeft.remainingPlayers}}/{{playersLeft.registeredPlayers}}</span></div>
</script>
<script type="text/mustache" id="tournamentPayoutStructureTemplate" style="display:none;">
    <h4>Payouts</h4>
    <div class="prize-pool">Prize pool: <span>{{prizePool}}</span></div>
    <div class="payouts">
        <div class="payout info-list-item header">
            Position <span>Amount</span>
        </div>
        <div class="info-list">
            {{#payouts}}
            <div class="payout info-list-item">
                {{position}} <span>{{payoutAmount}}</span>
            </div>
            {{/payouts}}
        </div>
    </div>
</script>
<script type="text/mustache" id="tournamentBlindsStructureTemplate" style="display:none;">
    <h4>Blinds Structure</h4>
    <div class="blinds-level info-list-item header">
        Blinds
        <span>Duration</span>
    </div>
    <div class="info-list">
        {{#blindsLevels}}
        <div class="blinds-level info-list-item">
            {{#isBreak}}
            Break
            {{/isBreak}}
            {{^isBreak}}
            {{smallBlind}}/{{bigBlind}}
            {{/ isBreak}}
            <span>{{durationInMinutes}}</span>
        </div>
        {{/blindsLevels}}
    </div>
</script>

<script type="text/mustache" id="tournamentPlayerListItem" style="display:none;">
    <tr>
        <td>{{position}}</td>
        <td>{{name}}</td>
        <td>{{stackSize}}</td>
        <td>{{winnings}}</td>
    </tr>
</script>
</body>
</html>

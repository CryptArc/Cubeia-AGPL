"use strict";

var Poker = Poker || {};

Poker.DevTools = Class.extend({
    tableId : 999999,
    tableManager : null,
    cards : null,
    cardIdSeq : 0,
    mockEventHandler : null,
    init : function() {
        var self = this;

        $(document).ready(function(){

            if(document.location.hash.indexOf("dev")!=-1){
                console.log("dev mode enabled");
            }
        });


            setTimeout(function(){self.launch();},1000);
        this.initCards();
    },
    initCards : function() {
       var suits = "hsdc ";
       var rank = "0"
    },
    launch : function() {
        var self = this;
        Poker.AppCtx.getViewManager().onLogin();
        setTimeout(function(){
            self.createTable();
        },1000);
        new Poker.PositionEditor("#tableView-"+this.tableId+" *");

    },

    createTable : function() {
        var self = this;

        var tableName = "Dev Table";
        this.tableManager = Poker.AppCtx.getTableManager();
        var tableViewContainer = $(".view-container");
        var templateManager = new Poker.TemplateManager();


        var beforeFunction = function() {
            var tableLayoutManager = new Poker.TableLayoutManager(self.tableId, tableViewContainer,
                templateManager, null, 10);
            self.tableManager.createTable(self.tableId, 10, tableName , [tableLayoutManager]);
            Poker.AppCtx.getViewManager().addTableView(tableLayoutManager,tableName);
        };

        var cleanUpFunction = function() {
            self.tableManager.leaveTable(self.tableId);
            Poker.AppCtx.getViewManager().removeTableView(self.tableId);
        };

        this.mockEventHandler = new Poker.MockEventHandler(beforeFunction,cleanUpFunction);

        var mockEvent = function(name,func,delay) {
            return new Poker.MockEvent(name,func,delay);
        };

        this.mockEventHandler.addEvent(
            mockEvent("Add players",function(){
                for(var i = 0; i<10; i++) {
                    self.addPlayer(i,i,"CoolPlayer"+i);
                }
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Deal cards",function(){
                for(var i = 0; i<10; i++) {
                    self.dealCards(i,i);
                }
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Player 0 small blind",function(){
                self.playerAction(0,Poker.ActionType.SMALL_BLIND);
            })
        );

        this.mockEventHandler.addEvent(
            mockEvent("Update main pots", function(){
                self.tableManager.updatePots(self.tableId,[{type:Poker.PotType.MAIN, amount:10000}]);
            })
        );


        this.mockEventHandler.addEvent(
            mockEvent("Player 1 small blind",function(){
                self.playerAction(1,Poker.ActionType.BIG_BLIND);
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Player 2 call",function(){
                self.playerAction(2,Poker.ActionType.CALL);
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Player 3 fold",function(){
                self.playerAction(3,Poker.ActionType.FOLD,0);
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Player 4 raise",function(){
                self.playerAction(4,Poker.ActionType.RAISE);
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Player 5 raise",function(){
                self.playerAction(5,Poker.ActionType.RAISE);
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("All players call",function(){
                self.playerAction(6,Poker.ActionType.CALL);
                self.playerAction(7,Poker.ActionType.CALL);
                self.playerAction(8,Poker.ActionType.CALL);
                self.playerAction(9,Poker.ActionType.CALL);
                self.playerAction(0,Poker.ActionType.CALL);
                self.playerAction(1,Poker.ActionType.CALL);
                self.playerAction(2,Poker.ActionType.CALL);
                self.playerAction(4,Poker.ActionType.CALL);
            })
        );

        this.mockEventHandler.addEvent(
            mockEvent("Deal community cards", function(){
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"as");
            })
        );

        this.mockEventHandler.addEvent(
            mockEvent("Deal community cards", function(){
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"ad");
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Deal community cards", function(){
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"ks");
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Deal community cards", function(){
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"qs");
            })
        );
        this.mockEventHandler.addEvent(
            mockEvent("Deal community cards", function(){
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"ac");
            })
        );


    },

    addPlayer : function(seat,playerId,name) {
        this.tableManager.addPlayer(this.tableId,seat,playerId, name);
        this.tableManager.updatePlayerStatus(this.tableId, playerId, Poker.PlayerTableStatus.SITTING_IN);
        this.tableManager.updatePlayerBalance(this.tableId,playerId, 100000);
    },
    dealCards : function(seat,playerId) {
        this.tableManager.dealPlayerCard(this.tableId,playerId,this.cardIdSeq++,"  ");
        this.tableManager.dealPlayerCard(this.tableId,playerId,this.cardIdSeq++,"  ");
    },
    playerAction : function(playerId,action,amount) {
        if(!amount) {
            amount = 10000
        }
        this.tableManager.handlePlayerAction(this.tableId,playerId,action,amount);
    },

    getRandomCard : function() {

    }
});
new Poker.DevTools();

Poker.PositionEditor = Class.extend({
    elements : null,
    elementIdSeq : 0,
    selectedElement : null,
    elementsSelector : null,
    init : function(elementsSelector) {
        var self = this;
        this.elementsSelector = elementsSelector;
        this.elements = new Poker.Map();
        setTimeout(function(){
            self.initHighlight();
        },2000);

    },
    clearStyle : function(){
        if(this.selectedElement!=null) {
            this.selectedElement.get(0).style.cssText="";
        }
    },
    initHighlight : function() {

        var self = this;
        $("body").append('<div class="dev-div-selector" id="divSelector"><ul></ul></div>');
        $("body").append('<div class="dev-div-selector" id="devElementStyles" style="display:none;"><a id="devClearDiv">clear style</a><div class="style-container"></div></div>');
        $("#devClearDiv").click(function(e){
            self.clearStyle();
        });

        $(document).bind('keydown',function(e){
            if(self.selectedElement==null) {
                return null;
            }
            $("#devElementStyles").show();
            var moveX = 0;
            var moveY = 0;
            if(e.keyCode == 38) {
                //upp
                moveY= -0.5;

            } else if(e.keyCode == 39) {
                //right
                moveX = 0.5;
            }
            else if(e.keyCode == 40) {
                //down
                moveY = 0.5;
            }
            else if(e.keyCode == 37) {
                //left
                moveX = -0.5;
            } else if(e.keyCode == 27) {    //esc
                if(self.selectedElement!=null) {
                    self.selectedElement.removeClass("dev-style-selected");
                    self.selectedElement = null;
                }
                return;
            }



            var left = self.selectedElement.css("left").replace("%","");
            var top = self.selectedElement.css("top").replace("%","");
            var right = self.selectedElement.css("right").replace("%","");
            var bottom = self.selectedElement.css("bottom").replace("%","");
            $("#devElementStyles .style-container").empty();
            if(moveX!=0) {
                if(typeof(left)!="undefined" && left!="auto") {
                    left=parseFloat(left);
                    self.selectedElement.css("left",(left+moveX) + "%");

                } else {
                    right=parseFloat(right);
                    self.selectedElement.css("right",(right-moveX) + "%");

                }
            }
            if(moveY!=0) {
                if(typeof(top)!="undefined" && top!="auto") {
                    var newTop = top=parseFloat(top) + moveY;
                    self.selectedElement.css("top",newTop + "%");

                } else {
                    right=parseFloat(bottom);
                    self.selectedElement.css("bottom",(bottom-moveY) + "%");

                }
            }

            self.addAttr("left");
            self.addAttr("right");
            self.addAttr("top");
            self.addAttr("bottom");


        });
        $(this.elementsSelector).bind("mouseenter",function(e){

            var el = $(e.target);
            if(el.attr("id")==null) {
                el.attr("id","develementid"+(self.elementIdSeq++));
            }
            self.elements.put(el.attr("id"),el);
            var cursor = el.css("cursor");

            el.css("cursor","pointer").click(function(ce){

                $("#divSelector").css({top:ce.pageY + "px",left:ce.pageX + "px"});
                var selector =  $("#divSelector ul");
                selector.empty();
                $.each(self.elements.values(),function(i,e){
                    var element = e;
                    var elId = element.attr("id") || "";
                    var li = $("<li>").append(elId+ ".["+element.attr("class")+"]");
                    selector.append(li);
                    $("#divSelector").show();
                    li.click(function(){
                        if(self.selectedElement!=null) {
                            self.selectedElement.removeClass("dev-style-selected");
                        }
                        self.selectedElement = element;
                        element.addClass("dev-style-selected");
                        self.elements.put(el.attr("id"),el);
                        $("#divSelector").hide();
                    });
                });
            });
        }).bind("mouseleave",function(e){
                var id = $(this).attr("id");
                if(id!=null) {
                    self.elements.remove(id);
                }
            });

    },
    addAttr : function(attr){
        if(this.selectedElement.css(attr)!="auto") {
            $("#devElementStyles .style-container").append($("<span/>").append(attr+":"+this.selectedElement.css(attr)));
        }

    }
});

Poker.MockEventHandler = Class.extend({
    events : null,
    currentPosition : 0,
    playing : false,
    defaultDelay : 500,
    beforeFunc : null,
    cleanUpFunc : null,
    stopAt : -1,
    init : function(beforeFunc, cleanUpFunc) {
        this.beforeFunc = beforeFunc;
        this.cleanUpFunc = cleanUpFunc;
        this.events = [];
        var div = $("<div/>").hide().attr("id","mockEventPlayer");
        div.draggable({height:200});
        div.append(this.createToolsPanel());

        div.append($("<ul/>").attr("id","mockEvents"));
        $("body").append(div);
        $("#mockEventPlayer").show();
        this.beforeFunc();

    },
    setBeforeFunc : function(beforeFunc) {
        this.beforeFunc = beforeFunc;
    },
    setCleanUpFunc : function(cleanUpFunc) {
        this.cleanUpFunc = cleanUpFunc;
    },
    createToolsPanel : function() {
        var self = this;
        var toolbar = $("<div/>").addClass("player-panel");
        this.createToolBarButton(toolbar,"play-previous",function(){self.playPrevious();});
        this.createToolBarButton(toolbar,"play",function(){self.play();});
        this.createToolBarButton(toolbar,"pause",function(){self.pause();});
        this.createToolBarButton(toolbar,"play-next",function(){self.playNext();});
        this.createToolBarButton(toolbar,"maximize",function(){self.maximize();},true);
        this.createToolBarButton(toolbar,"minimize",function(){self.minimize();});

        return toolbar;
    },
    maximize : function() {
        $("#mockEventPlayer .minimize").show();
        $("#mockEventPlayer .maximize").hide();
        $("#mockEventPlayer").height(250+"px").find("ul").show();
    },
    minimize : function() {
        $("#mockEventPlayer .minimize").hide();
        $("#mockEventPlayer .maximize").show();
        $("#mockEventPlayer").height(20+"px").find("ul").hide();
    },
    createToolBarButton : function(container,clazz,func,hidden) {
        var b = $("<div/>").addClass(clazz).click(function(){
            container.find(".active").removeClass("active");
            $(this).addClass("active");
            func();
        });
        if(hidden) {
            b.hide();
        }
        container.append(b);
    },
    addEvent : function(event) {
        var self = this;
        var id = this.events.length;
        this.events.push(event);
        var ev = $("<li/>").attr("id","mockEvent-"+id)
        var cb = $("<input/>").attr("type","checkbox").attr("checked","checked");
        var play = $("<div/>").addClass("play").click(function(e){
            self.playTo(id+1);
        });
        cb.change(function(e){
            if($(this).is(":checked")) {
                event.enabled = true;
            } else {
                event.enabled = false;
            }
            ev.toggleClass("disabled");
        });
        ev.append(play).append(cb).append(event.name);

        $("#mockEvents").append(ev);

    },
    playTo : function(pos) {
        var self = this;
        var restart = false;
        if(pos<this.currentPosition) {
            restart = true;
        }
        this.stopAt = pos;

        if(restart==true) {
            this.currentPosition=0;
            this.cleanUpFunc();
            var self = this;

            setTimeout(function(){
                self.beforeFunc();
                self.play();

            },500)

        } else {
            self.play();
        }
        $(".player-panel").find(".active").removeClass("active");
        $(".player-panel").find(".play").addClass("active");

    },
    pause : function() {
        this.playing = false;
    },
    playPrevious : function() {
        if(this.currentPosition==0) {
            return;
        }
        this.playTo(this.currentPosition-1);


    },
    play : function() {
        console.log("Playing mock events");
        this.playing = true;
        this.nextEvent();
    },
    playNext : function() {
        if(this.events.length<this.currentPosition) {
            return;
        }
        this.events[this.currentPosition].func();
        $("#mockEvents .current").removeClass("current");

        $("#mockEvent-"+this.currentPosition).addClass("current");
        this.currentPosition++;
    },
    nextEvent : function() {
        if(this.playing == false) {
            return;
        }
        if(this.events.length<=this.currentPosition) {
            this.playing = false;
            return;
        }
        if(this.stopAt == this.currentPosition) {
            $(".player-panel").find(".active").removeClass("active");
            $(".player-panel").find(".stop").addClass("active");
            this.stopAt = -1;
            this.playing = false;
            return;
        }

        var event = this.events[this.currentPosition];
        if(event.enabled==false) {
            this.currentPosition++;
            this.nextEvent();
            return;
        }
        console.log("next event");
        var delay = this.defaultDelay;
        if(typeof(event.delay)!="undefined") {
            delay = event.delay;
        }
        this.playNext();
        var self = this;
        setTimeout(function(){
            self.nextEvent();
        },delay);
    }
});

Poker.MockEvent = Class.extend({
    name : null,
    func : null,
    delay : 0,
    enabled : true,
    init : function(name,func,delay) {
        this.name = name;
        this.func = func;
        this.delay = delay;
    }
});
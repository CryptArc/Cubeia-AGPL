"use strict";

var Poker = Poker || {};

Poker.DevTools = Class.extend({
    tableId : 999999,
    tableManager : null,
    cards : null,
    cardIdSeq : 0,
    elements : null,
    elementIdSeq : 0,
    init : function() {
        var self = this;
        this.elements = new Poker.Map();
        $(document).ready(function(){
            if(document.location.hash.indexOf("dev")!=-1){
                console.log("dev mode enabled");
                setTimeout(function(){self.launch();},1000);

            }
        });
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
    },

    createTable : function() {
        var tableName = "Dev Table";
        this.tableManager = Poker.AppCtx.getTableManager();
        var tableViewContainer = $(".view-container");
        var templateManager = new Poker.TemplateManager();
        var tableLayoutManager = new Poker.TableLayoutManager(this.tableId, tableViewContainer, templateManager, this, 10);

        this.tableManager.createTable(this.tableId, 10, tableName , [tableLayoutManager]);
        Poker.AppCtx.getViewManager().addTableView(tableLayoutManager,tableName);

        for(var i = 0; i<10; i++) {
            this.addPlayer(i,i,"CoolPlayer"+i);
        }

        this.tableManager.dealCommunityCard(this.tableId,this.cardIdSeq++,"as");
        this.tableManager.dealCommunityCard(this.tableId,this.cardIdSeq++,"ac");
        this.tableManager.dealCommunityCard(this.tableId,this.cardIdSeq++,"ad");
        this.tableManager.dealCommunityCard(this.tableId,this.cardIdSeq++,"ks");
        this.tableManager.dealCommunityCard(this.tableId,this.cardIdSeq++,"qs");
        this.tableManager.updatePots(this.tableId,[{type:Poker.PotType.MAIN, amount:10000}]);
        var self = this;
        setTimeout(function(){
            self.initHighlight();
        },1000);
    },
    clearStyle : function(){
        if(this.selectedElement!=null) {
            this.selectedElement.get(0).style.cssText="";
        }
    },
    selectedElement : null,
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
;           $("#devElementStyles").show();
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
            var addAttr = function(attr){
                if(self.selectedElement.css(attr)!="auto") {
                    $("#devElementStyles .style-container").append($("<span/>").append(attr+":"+self.selectedElement.css(attr)));
                }

            }
            addAttr("left");
            addAttr("right");
            addAttr("top");
            addAttr("bottom");


        });
        $("#tableView-"+this.tableId+" *").bind("mouseenter",function(e){
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

    addPlayer : function(seat,playerId,name) {
        this.tableManager.addPlayer(this.tableId,seat,playerId, name);
        this.tableManager.updatePlayerStatus(this.tableId, playerId, Poker.PlayerTableStatus.SITTING_IN);
        this.tableManager.updatePlayerBalance(this.tableId,playerId, 100000);
        var self = this;
        setTimeout(function(){
            self.tableManager.dealPlayerCard(self.tableId,playerId,self.cardIdSeq++,"  ");
            self.tableManager.dealPlayerCard(self.tableId,playerId,self.cardIdSeq++,"  ");
            self.tableManager.handlePlayerAction(self.tableId,playerId,Poker.ActionType.CALL,10000);
            //self.tableManager.showHandStrength(self.tableId,playerId,Poker.Hand.FOUR_OF_A_KIND);
        },50);




    },

    getRandomCard : function() {

    }
});
new Poker.DevTools();
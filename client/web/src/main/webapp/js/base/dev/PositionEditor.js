"use strict";

var Poker = Poker || {};



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
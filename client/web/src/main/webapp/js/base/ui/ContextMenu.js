var Poker = Poker || {};
Poker.ContextMenu = Class.extend({
    init : function(event,items) {
        var container = $("#contextMenuContainer");
        if(container.length==0){
            container = $("<div/>").attr("id","contextMenuContainer").addClass("context-menu");
            $("body").append(container);
            container = $("#contextMenuContainer");
        }
        container.empty();
        var left = Math.max(0,event.pageX - container.outerWidth());
        container.css("top",event.pageY).css("left",left);
        var menuList = $("<ul/>")
        $.each(items,function(i,e){
            var li = $("<li/>").append(e.title);
            li.click(function(evt){
                e.callback();
            });
            menuList.append(li);
        });
        container.append(menuList);
        event.stopPropagation();
        $("body").click(function(e){
           container.remove();
        });

    }
});
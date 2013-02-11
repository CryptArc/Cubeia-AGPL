var InstantClickListener = function(el,callFunc) {
    this.callFunc = callFunc;
    this.moved = false;
    this.element = el;
    this.element.addEventListener('touchstart', this, false);
};

InstantClickListener.prototype = {
    handleEvent: function(e) {
        switch(e.type) {
            case 'touchstart': this.onTouchStart(e); break;
            case 'touchmove': this.onTouchMove(e); break;
            case 'touchend': this.onTouchEnd(e); break;
        }
    },

    onTouchStart: function(e) {
        this.moved = false;
        this.element.addEventListener('touchmove', this, false);
        this.element.addEventListener('touchend', this, false);
    },

    onTouchMove: function(e) {
        this.moved = true;
    },

    onTouchEnd: function(e) {
        this.element.removeEventListener('touchmove', this, false);
        this.element.removeEventListener('touchend', this, false);

        if(this.moved == false) {
            var theTarget = document.elementFromPoint(e.changedTouches[0].clientX, e.changedTouches[0].clientY);
            if(theTarget.nodeType == 3) theTarget = theTarget.parentNode;
            var theEvent = document.createEvent('MouseEvents');
            this.callFunc(theEvent);
            e.stopPropagation();
            e.preventDefault();
        }
    }
};

(function( $ ) {
    $.fn.touchSafeClick = function(func) {
        return this.each(function(){
            if("ontouchstart" in window) {
                new InstantClickListener($(this)[0],func);
            } else {
                $(this).click(func);
            }
        });
    };
})( jQuery );
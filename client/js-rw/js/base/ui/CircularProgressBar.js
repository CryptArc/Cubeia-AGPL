var CircularProgressBar = function(containerId) {
	this._initialize(containerId);
};
CircularProgressBar.prototype = {
	containerId : null,
	secondPartCreated : false,
	nextToggle : 20,
	_initialize : function(containerId) {
		if (containerId == null) {
			throw "CircularProgressBar: containerId must be set";
		}
		if (containerId.indexOf("#") != 0) {
			containerId = "#" + containerId;
		}
		var c = $(containerId);
		c.css({fontSize:c.width()+"px"});
		this.containerId = containerId;
		this._addContent();
	},
	show : function() {
		$(this.containerId).show();
	},
	hide : function() {
		$(this.containerId).hide();
	},
	_addContent : function() {
		var progressBarHTML = ''
            + '<div class="cpb-timer cpb-animated">'
                + '<div class="cpb-slice">'
                    + '<div class="cpb-pie"></div>'
				    + '<div class="cpb-pie cpb-fill" style="display:none;"></div>'
				+ '</div>'
            + '</div>';

		var backgroundHTML = '<div class="cpb-timer cpb-background">'
				+ '<div class="cpb-slice cpb-gt50">'
				+ '<div class="cpb-pie"></div>'
				+ '<div class="cpb-pie cpb-fill"></div>' + '</div>' + '</div>';

		$(this.containerId).append(backgroundHTML).append(progressBarHTML);

	},
	reset : function() {

		$(".cpb-animated .cpb-slice", this.containerId).removeClass("cpb-gt50");
		$(".cpb-animated .cpb-fill", this.containerId).hide();
        $('.cpb-animated .cpb-pie', this.containerId).removeAttr("style");
	},
    animation : null,
	render : function(time) {

        if(this.animation!=null){
            this.animation.rotate("0deg");
        }
        $(".cpb-animated .cpb-slice", this.containerId).removeClass("cpb-gt50");
        $(".cpb-animated .cpb-fill", this.containerId).hide();


        var self = this;
        var el = $('.cpb-animated .cpb-pie', this.containerId);

        this.animation = Firmin.animateR(el.get(0),
            { rotate : "179.9deg", timingFunction : 'linear'},
            time/2000,
            function(){
                self.animation.rotate("180.1deg");
                $(".cpb-animated .cpb-slice", self.containerId).addClass("cpb-gt50");
                $(".cpb-animated .cpb-fill", self.containerId).show();

                self.animation.animateR(
                    { rotate : "179.8deg", timingFunction:"linear"},
                    time/2000

                );
            }
        );
    },
    detach : function() {
        $(this.containerId).remove();
    }
};
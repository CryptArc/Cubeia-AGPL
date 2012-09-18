(function( $ ) {
	  $.fn.describe = function() {
		 
		  return this.each(function(){

               var originalInput = null;
			   var defaultMsg = $(this).attr("title");

              if($(this).attr("type")=="password") {
                   originalInput = $(this);
                   var p = $("<input/>").attr(
                       { name: $(this).attr("name"),
                           type : "text"});
                       p.attr("class",originalInput.attr("class"));
                       p.val(defaultMsg);
                       p.focus(function(){
                          $(this).hide();
                          originalInput.show();
                          originalInput.removeClass("describe");
                          originalInput.focus();
                       });
                  originalInput.after(p);
                  originalInput.hide();
               } else {
                  $(this).val(defaultMsg);
              }

			   $(this).bind("focus",function(e){

                   if($(this).val()==defaultMsg) {
		      		  $(this).val("");
		      		  $(this).removeClass("describe");
		      	  }
		        });
		        $(this).bind("blur",function(e){
                    if(originalInput!=null) {
                     //todo?
                    } else if($(this).val()=="") {
		        		$(this).val(defaultMsg);
		        		$(this).addClass("describe");
		        	}
		        });
		  });
	  };
	})( jQuery );


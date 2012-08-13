UiElementHandler = function() {

};

UiElementHandler.prototype.removeDivElement = function(id) {
	var divId = document.getElementById(id);	
	divId.parentNode.removeChild(divId);
};

UiElementHandler.prototype.setDivElementParent = function(id, parentId) {
	var divId = document.getElementById(id);
    var parentDiv = document.getElementById(parentId);
    parentDiv.appendChild(divId);
};

UiElementHandler.prototype.createDivElement = function(parentId, id, html, styleClass, clickAttribute) {
	var parent = document.getElementById(parentId);
	var index = parent.getElementsByTagName("*");
	var newdiv = document.createElement('div', [index]); 
	newdiv.setAttribute('id', id); 
	newdiv.setAttribute('onclick', clickAttribute); 
	newdiv.className = styleClass; 
	
	if (html) { 
			newdiv.innerHTML = html; 
		} 
		else { 
		newdiv.innerHTML = ""; 
		} 
	parent.appendChild(newdiv);
//	console.log(newdiv.id)
	return newdiv.id;
};


UiElementHandler.prototype.createTextInputElement = function(parentId, id, varName, styleClass) {
	var parent = document.getElementById(parentId);
	var index = parent.getElementsByTagName("*");
	var newdiv = document.createElement('input', [index]);

	newdiv.setAttribute('id', id); 	
	newdiv.setAttribute('type', "text"); 
	newdiv.setAttribute('name', varName); 
	
	newdiv.className = styleClass; 
//	console.log(varName);
		
	parent.appendChild(newdiv);
};


UiElementHandler.prototype.removeElements = function(release, elementId) {
	var opacity = release;
    var elementId = elementId;
	if (opacity > 0.999) {
		opacity = 0.95;
	}

	var removeInterval = setInterval(function() {
		if (document.getElementById(elementId) == null) {
			clearTimeout(removeInterval);
			return;
		}

		opacity = opacity * opacity;
		document.getElementById(elementId).style.opacity = opacity;
        document.getElementById(elementId).style.webkitTransform = "scale("+opacity+")";

		if (opacity < 0.05) {
			if ( document.getElementById(elementId).childNodes ) {
				    while ( document.getElementById(elementId).childNodes.length >= 1 )
				{
				    document.getElementById(elementId).removeChild( document.getElementById(elementId).firstChild );
				}
			}

			uiElementHandler.removeDivElement(elementId);
			clearInterval(removeInterval);
		}
			
	}, 50, this);
	
};

UiElementHandler.prototype.removeElementChildren = function(release, elementId) {
    var opacity = release;
    if (opacity > 0.99) {
        opacity = 0.95;
    }

    var removeInterval = setInterval(function() {
        if (document.getElementById(elementId) == null) {
            clearTimeout(removeInterval);
            return;
        }

        opacity = opacity * opacity;
    //    document.getElementById(elementId).style.opacity = opacity;

        if (opacity < 0.15) {
            if ( document.getElementById(elementId).childNodes ) {
                while ( document.getElementById(elementId).childNodes.length >= 1 )
                {
                    document.getElementById(elementId).removeChild( document.getElementById(elementId).firstChild );
                }
            }

            clearInterval(removeInterval);
        }

    }, 50, this);

};

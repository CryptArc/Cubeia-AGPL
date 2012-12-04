DomHandler = function() {

};

DomHandler.prototype.removeDivElement = function(div) {
    div.parentNode.removeChild(div);
};

DomHandler.prototype.addElementClass = function(element, classId) {
    element.className += " "+classId;
};

DomHandler.prototype.setDivElementParent = function(id, parentId) {
    var divId = document.getElementById(id);
    var parentDiv = document.getElementById(parentId);
    parentDiv.appendChild(divId)

};

DomHandler.prototype.createDivElement = function(parentElement, id, html, styleClass) {
    var parent = parentElement;
    var index = parent.getElementsByTagName("*");
    var newdiv = document.createElement('div', [index]);
    newdiv.setAttribute('id', id);
    newdiv.className = styleClass;

    if (html) {
        newdiv.innerHTML = html;
    }
    else {
        newdiv.innerHTML = "";
    }
    parent.appendChild(newdiv);
    return newdiv;
};

DomHandler.prototype.removeElementChildren = function(element) {
    if (element.childNodes )
    {
        while ( element.childNodes.length >= 1 )
        {
            element.removeChild(element.firstChild);
        }
    }
}

DomHandler.prototype.removeElement = function(element) {
    this.removeElementChildren(element);
    this.removeDivElement(element);
};

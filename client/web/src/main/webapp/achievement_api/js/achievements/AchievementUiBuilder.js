AchievementUiBuilder = function(domHandler) {
    this.domHandler = domHandler;
};

AchievementUiBuilder.prototype.builUiComponent = function(parent, id, styleClass) {
    var element = this.domHandler.createDivElement(parent, id, "", styleClass)
    return element;
};

AchievementUiBuilder.prototype.clearList = function(parentElement) {
    var element = this.domHandler.removeElementChildren(parentElement)
    return element;
};

AchievementUiBuilder.prototype.addClass = function(element, classId) {
    var element = this.domHandler.addElementClass(element, classId)
    return element;
};
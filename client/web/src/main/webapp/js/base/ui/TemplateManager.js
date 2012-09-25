"use strict";
var Poker = Poker || {};

Poker.TemplateManager = Class.extend({
    templates : [],
    init : function(preCacheTemplates) {
        if(preCacheTemplates && preCacheTemplates.length>0) {
            for(var i in preCacheTemplates) {
                this.getTemplate(preCacheTemplates[i]);
            }
        }
    },
    getTemplate : function(id) {
        if(typeof  this.templates[id]!="undefined") {
            return this.templates[id];
        } else {
            var el = $("#"+id);
            if(el.length==0) {
               throw "Template " + id + " not found";
            }
            var template = el.html();
            this.templates[id] = template;
            return template;

        }
    }
});
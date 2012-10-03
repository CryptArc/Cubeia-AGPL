"use strict";
var Poker = Poker || {};

Poker.TemplateManager = Class.extend({
    templates : null,
    init : function(preCacheTemplates) {
        this.templates = new Poker.Map();
        if(preCacheTemplates && preCacheTemplates.length>0) {
            for(var i in preCacheTemplates) {
                this.getTemplate(preCacheTemplates[i]);
            }
        }

    },
    getTemplate : function(id) {
        if(this.templates.get(id)!=null) {
            return this.templates.get(id);
        } else {
            var el = $("#"+id);
            if(el.length==0) {
               throw "Template " + id + " not found";
            }
            var template = el.html();
            this.templates.put(id,template);
            return template;

        }
    }
});
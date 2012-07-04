/**
 * A mixin for Comments controller to help with authentication.
 */
Ext.define('Docs.controller.AuthHelpers', {

    /**
     * Performs request to the comments server.
     *
     * Works as if calling Ext.Ajax.request or Ext.data.JsonP.request
     * directly, but prefixes the URL with docs base URL and database
     * name and adds Session ID.
     *
     * @param {String} proxy Should we perform "ajax" or "jsonp" request.
     * @param {Object} config
     * @protected
     */
    request: function(type, config) {
        config.url = this.addSid(Docs.baseUrl + '/' + Docs.commentsDb + '/' + Docs.commentsVersion + config.url);
        if (type === "jsonp") {
            Ext.data.JsonP.request(config);
        }
        else {
            // Allow doing Cross Origin request.
            config.cors = true;
            Ext.Ajax.request(config);
        }
    },

    addSid: function(url) {
        var sid = this.getController('Auth').sid;
        return url + (url.match(/\?/) ? '&' : '?') + 'sid=' + sid;
    },

    /**
     * Returns true if user is logged in.
     */
    isLoggedIn: function() {
        return this.getController('Auth').isLoggedIn();
    }

});

var exec = require('cordova/exec');

var nostr = {
    serviceName: "nostr",

    signEvent: function(success, error, key, value) {
        exec(success, error, this.serviceName, "signEvent", [key, value]);
    },

    getPublicKey: function(success, error, key) {
        exec(success, error, this.serviceName, "getPublicKey", [key]);
    },
};

module.exports = nostr;
var exec = require('cordova/exec');

var nostr = {
    serviceName: "nostr",

    signEvent: function(success, error, key, value) {
        exec(success, error, this.serviceName, "signEvent", [key, value]);
    },

    getPublicKey: function(success, error, key) {
        exec(success, error, this.serviceName, "getPublicKey", [key]);
    },

    getPublicKey1: function(success, error, key) {
        exec(success, error, this.serviceName, "getPublicKey1", [key]);
    },
};

window.nostr = nostr;

window.nostr.getPublicKey1 = function(success, error, key) {
     nostr.getPublicKey1(success, error, key);
};

module.exports = nostr;
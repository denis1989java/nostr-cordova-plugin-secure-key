var exec = require('cordova/exec');

var nostr = {
    serviceName: "nostr",

    signEvent: function(success, error, key, value) {
        exec(success, error, this.serviceName, "signEvent", [key, value]);
    },

    getPublicKey: function(success, error, key) {
        exec(success, error, this.serviceName, "getPublicKey", [key]);
    },
    close: function(data) {
        exec(null, null, 'Modal', 'close', [ data ]);
    }

};

module.exports = nostr;
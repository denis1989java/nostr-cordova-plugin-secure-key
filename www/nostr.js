var exec = require('cordova/exec');

var nostr = {
    serviceName: "nostr",

    signEvent: function(success, error, key, value) {
        exec(success, error, this.serviceName, "signEvent", [key, value]);
    },

    getPublicKey: function(success, error, key) {
        exec(success, error, this.serviceName, "getPublicKey", [key]);
    },
    getPublicKey1: function(key) {
        exec(null, null, this.serviceName, "getPublicKey1", [key]);
    },
    close: function(data) {
        exec(null, null, this.serviceName, 'close', [ data ]);
    },
    successCallback: function(event) {
        exec(null, null, this.serviceName, 'successCallback', [ event ]);
    }

};

module.exports = nostr;
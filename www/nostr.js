var exec = require('cordova/exec');

const SERVICE_NAME = "nostr";
const SIGN_EVENT = "signEvent";
const GET_PUBLIC_KEY = "getPublicKey";

var nostr = {

    signEvent: function (success, error, key, value) {
        exec(success, error, SERVICE_NAME, SIGN_EVENT, [key, value]);
    },

    getPublicKey: function (success, error) {
        exec(success, error, SERVICE_NAME, GET_PUBLIC_KEY, []);
    }

};

module.exports = nostr;
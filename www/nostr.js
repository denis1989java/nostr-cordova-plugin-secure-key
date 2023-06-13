var exec = require('cordova/exec');

const SERVICE_NAME = "Nostr";
const SIGN_EVENT = "signEvent";
const GET_PUBLIC_KEY = "getPublicKey";

var Nostr = {

    signEvent: function (success, error, msg) {
        exec(success, error, SERVICE_NAME, SIGN_EVENT, [msg]);
    },

    getPublicKey: function (success, error) {
        exec(success, error, SERVICE_NAME, GET_PUBLIC_KEY, []);
    }

};

module.exports = Nostr;
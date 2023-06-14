var exec = require('cordova/exec');

const SERVICE_NAME = "nostr";
const SIGN_EVENT = "signEvent";
const GET_PUBLIC_KEY = "getPublicKey";

var nostr = {

    signEvent: function (success, error, msg) {
        exec(success, error, SERVICE_NAME, SIGN_EVENT, [msg]);
    },

    getPublicKey: function (success, error) {
        exec(success, error, SERVICE_NAME, GET_PUBLIC_KEY, []);
    }

};

document.addEventListener("deviceready", onDeviceReady, false)

function onDeviceReady() {
    let nostr = {
        getPublicKey: function () {
            return new Promise((resolve, reject) => {
                cordova.plugins.nostr.getPublicKey(
                    function (res) {
                        resolve(res.privKey.replaceAll("\"", ""))
                    },
                    function (error) {
                        reject(error)
                    }
                )
            })
        },
        signEvent: function (msg) {
            return new Promise((resolve, reject) => {
                cordova.plugins.nostr.signEvent(
                    function (res) {
                        resolve(res)
                    },
                    function (error) {
                        reject(error)
                    },
                    msg
                )
            })
        }
    }
    window.nostr = nostr
}

module.exports = nostr;
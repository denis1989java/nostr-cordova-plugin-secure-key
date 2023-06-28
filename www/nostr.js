var exec = require('cordova/exec');

const SERVICE_NAME = "NostrKeyStore";
const SIGN_EVENT = "signEvent";
const GET_PUBLIC_KEY = "getPublicKey";

var NostrKeyStore = {

    signEvent: function (success, error, msg) {
        exec(success, error, SERVICE_NAME, SIGN_EVENT, [msg]);
    },

    getPublicKey: function (success, error) {
        exec(success, error, SERVICE_NAME, GET_PUBLIC_KEY, []);
    }

};

document.addEventListener("deviceready", onDeviceReady, false)

function onDeviceReady() {
    let NostrKeyStore = {
        getPublicKey: function () {
            return new Promise((resolve, reject) => {
                cordova.plugins.NostrKeyStore.getPublicKey(
                    function (res) {
                        resolve(res.pubKey.replaceAll("\"", ""))
                    },
                    function (error) {
                        reject(error)
                    }
                )
            })
        },
        signEvent: function (msg) {
            return new Promise((resolve, reject) => {
                cordova.plugins.NostrKeyStore.signEvent(
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

    window.nostr = NostrKeyStore

    document.addEventListener("backbutton", function (e) {
        console.log(window.history)
        console.log(JSON.stringify(window.history.state))
        console.log(document.referrer)
        if (document.referrer === "") { //you check that there is nothing left in the history.
            e.preventDefault();
            console.log("DFDFDFDGFDGFDGFDGFDGFDGFDGFDGFGDFGDFGDFGDFGDFGDFG")
            navigator.app.exitApp();
        } else {
            console.log("DFDFDFDGFDGFDGFDGFDGFDGFDGFDGFGDFGDFGDFGDFGDFGDFG111111")
            window.history.back();
        }
    });
}

module.exports = NostrKeyStore;

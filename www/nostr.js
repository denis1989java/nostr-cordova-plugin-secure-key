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
        console.log("EEEEEEEEEEEEEEEEEEEE")
        console.log("SSSS" + document.referrer + "LLLL")
        if(window.history){
            console.log("window.history" + window.history.length + "OOOOOO")
        }
        console.log("RRRRRRRRRRRRRRR")
        if (document.referrer === "") { //you check that there is nothing left in the history.
            console.log("KKKKKKKKKKKKKKK")
            e.preventDefault();
            navigator.app.exitApp();
        } else {
            console.log("LLLLLLLLLLLLLLLLLL")
            e.preventDefault();
            window.history.back();
        }
    });
}

module.exports = NostrKeyStore;

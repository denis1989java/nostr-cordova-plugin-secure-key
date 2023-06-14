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

document.addEventListener("deviceready", testFromNostrJs, false)

function testFromNostrJs() {
    console.log("DGHDGHDGHDGHDGHDGHDGHDGHGDHGDHDGHDGHDGH")
    console.log("DGHDGHDGHDGHDGHDGHDGHDGHGDHGDHDGHDGHDGH")
    console.log("DGHDGHDGHDGHDGHDGHDGHDGHGDHGDHDGHDGHDGH")
    console.log("DGHDGHDGHDGHDGHDGHDGHDGHGDHGDHDGHDGHDGH")
    console.log("DGHDGHDGHDGHDGHDGHDGHDGHGDHGDHDGHDGHDGH")
    console.log("1111111111111111111111111111111")
}

module.exports = nostr;
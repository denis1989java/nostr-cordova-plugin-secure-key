package com.nostr.plugin;

// Secure key store main class

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class nostr extends CordovaPlugin {

  Map<String, String> keys = new HashMap<String, String>() {{
    put("a", "GFDGFDHFDHGFDHG");
    put("c", "HDSKLJLSKLDKJKL");
  }};

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    if (action.equals("signEvent")) {
      String alias = args.getString(0);
      String input = args.getString(1);
      this.encrypt(alias, input, callbackContext);
      return true;
    }

    if (action.equals("getPublicKey")) {
      String alias = args.getString(0);
      this.decrypt(alias, callbackContext);
      return true;
    }

    return false;
  }

  private void encrypt(String alias, String input, CallbackContext callbackContext) {
    callbackContext.success("key created and stored successfully");
  }

  private void decrypt(String alias, CallbackContext callbackContext) {
      callbackContext.success(keys.get(alias));
  }

}

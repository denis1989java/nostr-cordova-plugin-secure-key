package com.nostr.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Base64;
import android.security.KeyPairGeneratorSpec;
import android.os.Build;
com.nostr.plugin.ModalActivity;


import java.security.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.StringBuffer;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class nostr extends CordovaPlugin {

  private static final int ACTIVITY_MODAL = 1001;
  String DEFAULT_VAL = "DEFAULT_PK";
  public static final String PARAM_LOAD_URL = "loadUrl";

  private CallbackContext callbackContext;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {


    this.callbackContext = callbackContext;

    if (action.equals("getPublicKey")) {

      String pubKey = decrypt(DEFAULT_VAL);

      Log.i(Constants.TAG, "pubKey " + pubKey);

      if ("".equals(pubKey)) {

        Intent intent = new Intent(this.cordova.getActivity(), ModalActivity.class);
        intent.putExtra(PARAM_LOAD_URL, "test");
        this.cordova.setActivityResultCallback(this);
        this.cordova.getActivity().startActivityForResult(intent, ACTIVITY_MODAL);

        return true;

      }

      callbackContext.success("3356de61b39647931ce8b2140b2bab837e0810c0ef515bbe92de0248040b8bdd");
      return true;
    }

    if (action.equals("close")) {
      if (cordova.getActivity() instanceof ModalActivity) {

        Log.i(Constants.TAG, "modal res " + args.getString(0));

        Intent intent = new Intent();
        intent.putExtra("param", "3356de61b39647931ce8b2140b2bab837e0810c0ef515bbe92de0248040b8bdd");

        encrypt(DEFAULT_VAL, "3356de61b39647931ce8b2140b2bab837e0810c0ef515bbe92de0248040b8bdd");

        this.cordova.getActivity().setResult(Activity.RESULT_OK, intent);
        this.cordova.getActivity().finish();

        callbackContext.success("3356de61b39647931ce8b2140b2bab837e0810c0ef515bbe92de0248040b8bdd");
      } else {
        callbackContext.error("Not ModalActivity");
      }
      return true;
    }

/*
    if (action.equals("signEvent")) {
      String alias = args.getString(0);
      String input = args.getString(1);
      this.encrypt(alias, input, callbackContext);
      return true;
    }*/

    return false;
  }

  private void encrypt(String alias, String input) {

    try {

      KeyStore keyStore = KeyStore.getInstance(getKeyStore());
      keyStore.load(null);

      if (!keyStore.containsAlias(alias)) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(getContext()).setAlias(alias)
                .setSubject(new X500Principal("CN=" + alias)).setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime()).setEndDate(end.getTime()).build();

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", getKeyStore());
        generator.initialize(spec);

        KeyPair keyPair = generator.generateKeyPair();

        Log.i(Constants.TAG, "created new key pairs");
      }

      PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();

      if (input.isEmpty()) {
        Log.d(Constants.TAG, "Exception: input text is empty");
        return;
      }

      Cipher cipher = Cipher.getInstance(Constants.RSA_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
      cipherOutputStream.write(input.getBytes("UTF-8"));
      cipherOutputStream.close();
      byte[] vals = outputStream.toByteArray();

      // writing key to storage
      KeyStorage.writeValues(getContext(), alias, vals);
      Log.i(Constants.TAG, "key created and stored successfully");

    } catch (Exception e) {
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
    }

  }

  private String decrypt(String alias) {
    try {
      KeyStore keyStore = KeyStore.getInstance(getKeyStore());
      keyStore.load(null);
      PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);

      Cipher output = Cipher.getInstance(Constants.RSA_ALGORITHM);
      output.init(Cipher.DECRYPT_MODE, privateKey);
      CipherInputStream cipherInputStream = new CipherInputStream(
              new ByteArrayInputStream(KeyStorage.readValues(getContext(), alias)), output);

      ArrayList<Byte> values = new ArrayList<Byte>();
      int nextByte;
      while ((nextByte = cipherInputStream.read()) != -1) {
        values.add((byte) nextByte);
      }
      byte[] bytes = new byte[values.size()];
      for (int i = 0; i < bytes.length; i++) {
        bytes[i] = values.get(i).byteValue();
      }

      return new String(bytes, 0, bytes.length, "UTF-8");

    } catch (Exception e) {
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      return "";
    }
  }

  private void removeKeyFile(String alias, CallbackContext callbackContext) {
    try {
      KeyStorage.resetValues(getContext(), alias);
      Log.i(Constants.TAG, "keys removed successfully");
      callbackContext.success("keys removed successfully");

    } catch (Exception e) {
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      callbackContext.error(
              "{\"code\": 6, \"api-level\": " + Build.VERSION.SDK_INT + ", \"message\": \"" + e.getMessage() + "\"}");
    }
  }

  private Context getContext() {
    return cordova.getActivity().getApplicationContext();
  }

  private String getKeyStore() {
    try {
      KeyStore.getInstance(Constants.KEYSTORE_PROVIDER_1);
      return Constants.KEYSTORE_PROVIDER_1;
    } catch (Exception err) {
      try {
        KeyStore.getInstance(Constants.KEYSTORE_PROVIDER_2);
        return Constants.KEYSTORE_PROVIDER_2;
      } catch (Exception e) {
        return Constants.KEYSTORE_PROVIDER_3;
      }
    }
  }

}

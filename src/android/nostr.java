package com.nostr.band;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.apache.cordova.dialogs.Notification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostr.band.Constants;
import com.nostr.band.KeyStorage;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;
import java.util.Calendar;

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

        final CordovaInterface cordova = this.cordova;

        //Toast.makeText(webView.getContext(), "msg", Toast.LENGTH_LONG).show();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(0, "accept");
        jsonArray.put(1, "cancel");
        prompt("MessageTest", "TitleTest", jsonArray, "DefaultTextTest", this.callbackContext);

        /*Intent intent = new Intent(this.cordova.getActivity(), ModalActivity.class);
        intent.putExtra(PARAM_LOAD_URL, "test");
        this.cordova.setActivityResultCallback(this);
        this.cordova.getActivity().startActivityForResult(intent, ACTIVITY_MODAL);*/
        this.callbackContext.isFinished();
        return true;

      }


      //callbackContext.success("3356de61b39647931ce8b2140b2bab837e0810c0ef515bbe92de0248040b8bdd");
      return true;
    }

   /* if (action.equals("close")) {
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
      }*/
    return true;
  }

/*
    if (action.equals("signEvent")) {
      String alias = args.getString(0);
      String input = args.getString(1);
      this.encrypt(alias, input, callbackContext);
      return true;
    }*/


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
      com.nostr.band.KeyStorage.writeValues(getContext(), alias, vals);
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

  private synchronized void prompt(final String message, final String title, final JSONArray buttonLabels, final String defaultText, final CallbackContext callbackContext) {

    final CordovaInterface cordova = this.cordova;

    Runnable runnable = new Runnable() {
      public void run() {
        final EditText promptInput = new EditText(cordova.getActivity());

                /* CB-11677 - By default, prompt input text color is set according current theme.
                But for some android versions is not visible (for example 5.1.1).
                android.R.color.primary_text_light will make text visible on all versions. */
        Resources resources = cordova.getActivity().getResources();
        int promptInputTextColor = resources.getColor(android.R.color.primary_text_light);
        promptInput.setTextColor(promptInputTextColor);
        promptInput.setText(defaultText);
        AlertDialog.Builder dlg = createDialog(cordova); // new AlertDialog.Builder(cordova.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dlg.setMessage(message);
        dlg.setTitle(title);
        dlg.setCancelable(true);

        dlg.setView(promptInput);

        final JSONObject result = new JSONObject();

        // First button
        if (buttonLabels.length() > 0) {
          try {
            dlg.setNegativeButton(buttonLabels.getString(0),
                    new AlertDialog.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                          result.put("buttonIndex", 1);
                          result.put("input1", promptInput.getText().toString().trim().length() == 0 ? defaultText : promptInput.getText());
                        } catch (JSONException e) {
                          //LOG.d(LOG_TAG,"JSONException on first button.", e);
                        }
                        Log.i(Constants.TAG, "result0 " + result );
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
                      }
                    });
          } catch (JSONException e) {
            //LOG.d(LOG_TAG,"JSONException on first button.");
          }
        }

        // Second button
        if (buttonLabels.length() > 1) {
          try {
            dlg.setNeutralButton(buttonLabels.getString(1),
                    new AlertDialog.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                          result.put("buttonIndex", 2);
                          result.put("input1", promptInput.getText().toString().trim().length() == 0 ? defaultText : promptInput.getText());
                        } catch (JSONException e) {
                          //LOG.d(LOG_TAG,"JSONException on second button.", e);
                        }
                        Log.i(Constants.TAG, "result1 " + result );
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
                      }
                    });
          } catch (JSONException e) {
            //LOG.d(LOG_TAG,"JSONException on second button.");
          }
        }

        // Third button
        if (buttonLabels.length() > 2) {
          try {
            dlg.setPositiveButton(buttonLabels.getString(2),
                    new AlertDialog.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                          result.put("buttonIndex", 3);
                          result.put("input1", promptInput.getText().toString().trim().length() == 0 ? defaultText : promptInput.getText());
                        } catch (JSONException e) {
                          //LOG.d(LOG_TAG,"JSONException on third button.", e);
                        }
                        Log.i(Constants.TAG, "result2 " + result );
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
                      }
                    });
          } catch (JSONException e) {
            // LOG.d(LOG_TAG,"JSONException on third button.");
          }
        }
        dlg.setOnCancelListener(new AlertDialog.OnCancelListener() {
          public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
            try {
              result.put("buttonIndex", 0);
              result.put("input1", promptInput.getText().toString().trim().length() == 0 ? defaultText : promptInput.getText());
            } catch (JSONException e) {
              e.printStackTrace();
            }
            Log.i(Constants.TAG, "resultCancel " + result );
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
          }
        });

        changeTextDirection(dlg);
      }

      ;
    };
    this.cordova.getActivity().runOnUiThread(runnable);
  }

  @SuppressLint("NewApi")
  private AlertDialog.Builder createDialog(CordovaInterface cordova) {
    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
      return new AlertDialog.Builder(cordova.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
    } else {
      return new AlertDialog.Builder(cordova.getActivity());
    }
  }

  @SuppressLint("NewApi")
  private void changeTextDirection(AlertDialog.Builder dlg) {
    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    dlg.create();
    AlertDialog dialog = dlg.show();
    if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
      TextView messageview = (TextView) dialog.findViewById(android.R.id.message);
      messageview.setTextDirection(android.view.View.TEXT_DIRECTION_LOCALE);
    }
  }
}

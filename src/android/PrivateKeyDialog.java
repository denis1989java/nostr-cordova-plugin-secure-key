package com.nostr.plugin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.nostr.plugin.R;

public class PrivateKeyDialog extends AppCompatDialogFragment {
  private EditText privateKeyEditText;
  private PrivateKeyListener listener;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    try {
      listener = (PrivateKeyListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement PrivateKeyListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.dialog_private_key, null);

    privateKeyEditText = view.findViewById(R.id.edit_private_key);

    builder.setView(view)
            .setTitle("Warning")
            .setMessage("You don't have a private key, please type it:")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                String privateKey = privateKeyEditText.getText().toString().trim();
                listener.onPrivateKeyEntered(privateKey);
              }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Cancel button clicked, do nothing
              }
            });

    return builder.create();
  }

  public interface PrivateKeyListener {
    void onPrivateKeyEntered(String privateKey);
  }
}

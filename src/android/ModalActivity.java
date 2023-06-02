package com.nostr.plugin;

import android.os.Bundle;
import android.widget.Toast;

import com.nostr.plugin.PrivateKeyDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.nostr.plugin.R;

public class ModalActivity extends AppCompatActivity implements PrivateKeyDialog.PrivateKeyListener  {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Show the private key dialog
    showPrivateKeyDialog();
  }

  private void showPrivateKeyDialog() {
    DialogFragment dialog = new PrivateKeyDialog();
    dialog.show(getSupportFragmentManager(), "private_key_dialog");
  }

  @Override
  public void onPrivateKeyEntered(String privateKey) {
    // Handle the private key entered by the user
    Toast.makeText(this, "Private Key: " + privateKey, Toast.LENGTH_SHORT).show();
    // Perform any additional logic here with the private key
  }
}

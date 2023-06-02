package com.nostr.plugin;

import org.apache.cordova.CordovaActivity;
import android.os.Bundle;
import com.nostr.plugin.R;

public class ModalActivity extends CordovaActivity {

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    this.overridePendingTransition(R.anim.bottom_in, R.anim.hold);
    super.init();

    String url = getIntent().getStringExtra(Modal.PARAM_LOAD_URL);
    super.loadUrl(url);
  }

  @Override
  public void finish() {
    super.finish();
    this.overridePendingTransition(R.anim.diagonaltranslate,R.alpha);
  }
}

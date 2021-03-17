package com.shaayaari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.shaayaari.utils.AppConstant;

import java.util.Arrays;
import java.util.List;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    String msgId, type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {

        super.onStart();
        getData();
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                checkLoginStatus();
            }
        }.start();

    }

    private void checkLoginStatus() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            initLogin();
        } else {
            startActivity(new Intent(SplashScreen.this, HomeScreen.class)
                    .putExtra(msgId, AppConstant.MSG_ID));
            finish();
        }
    }


    private void getData() {
        if (null != getIntent().getExtras()) {
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("msgId")) {
                    Log.d(TAG, "getData: BidId " + getIntent().getExtras().getString(key));
                    msgId = getIntent().getExtras().getString(key);
                }
               /* if (key.equals("type")) {
                    Log.d(TAG, "getData: type " + getIntent().getExtras().getString(key));
                    type = getIntent().getExtras().getString(key);
                }*/
            }

        } else Log.d(TAG, "getData: null");
    }

    private void initLogin() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_launcher_foreground)
                        .setTheme(R.style.ThemePlayLudoNoActionBar)
                        .build(),
                10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(SplashScreen.this, "sign in successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashScreen.this, HomeScreen.class));
                finish();
            } else {
                Toast.makeText(SplashScreen.this, "sign in failed", Toast.LENGTH_SHORT).show();

            }
        }
    }
}

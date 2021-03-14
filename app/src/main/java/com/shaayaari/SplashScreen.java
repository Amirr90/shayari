package com.shaayaari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            startActivity(new Intent(SplashScreen.this, HomeScreen.class));
            finish();
        }
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

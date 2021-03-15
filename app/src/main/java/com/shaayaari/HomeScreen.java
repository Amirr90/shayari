package com.shaayaari;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.shaayaari.databinding.ActivityHomeScreenBinding;


public class HomeScreen extends AppCompatActivity {

    ActivityHomeScreenBinding binding;
    NavController navController;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }


    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }
}
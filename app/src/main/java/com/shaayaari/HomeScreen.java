package com.shaayaari;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.messaging.FirebaseMessaging;
import com.shaayaari.databinding.ActivityHomeScreenBinding;
import com.shaayaari.utils.AppConstant;
import com.shaayaari.utils.SharedPrefs;
import com.shaayaari.views.DashboardFragmentDirections;

import static com.shaayaari.utils.SharedPrefs.subscribeTopic;


public class HomeScreen extends AppCompatActivity {
    private static final String TAG = "HomeScreen";

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
        getData();


        //checking for subscription !!
        if (!SharedPrefs.isSubscribeTopic(AppConstant.SUBSCRIBE_FOR_NEW_ADD_MSG))
            subscribeToTopics();
        else Log.d(TAG, "onStart: already subscribed !!");
    }

    private void subscribeToTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConstant.SUBSCRIBE_FOR_NEW_ADD_MSG)
                .addOnCompleteListener(task -> {
                    String msg = getString(R.string.msg_subscribed);
                    if (!task.isSuccessful()) {
                        msg = getString(R.string.msg_subscribe_failed);

                    } else {
                        subscribeTopic(AppConstant.SUBSCRIBE_FOR_NEW_ADD_MSG);
                    }
                    Log.d(TAG, msg);
                });
    }


    public void getData() {
        String bidId = getIntent().getStringExtra(AppConstant.MSG_ID);
        Log.d(TAG, "msgId: " + bidId);
        if (null != bidId && !bidId.isEmpty()) {
            DashboardFragmentDirections.ActionDashboardFragmentToDataFragment action = DashboardFragmentDirections.actionDashboardFragmentToDataFragment();
            action.setId(bidId);
            navController.navigate(action);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }
}
package com.shaayaari.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.shaayaari.R;
import com.shaayaari.adapter.HomeAdapter;
import com.shaayaari.databinding.FragmentDashboardBinding;
import com.shaayaari.interfaces.AdapterInterface;
import com.shaayaari.utils.AppConstant;
import com.shaayaari.utils.AppUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment implements AdapterInterface {
    private static final String TAG = "DashboardFragment";

    HomeAdapter adapter;
    FragmentDashboardBinding dashboardBinding;
    List<DocumentSnapshot> snapshots;
    NavController navController;


    //Ad
    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    @Override

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dashboardBinding = FragmentDashboardBinding.inflate(getLayoutInflater());
        initAdd();
        return dashboardBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        showInterstitialAd();

        snapshots = new ArrayList<>();
        adapter = new HomeAdapter(snapshots, this);
        dashboardBinding.recHome.setAdapter(adapter);
        loadCategoryData();
    }

    private void initAdd() {
        MobileAds.initialize(requireActivity(), initializationStatus -> {
        });
        adRequest = new AdRequest.Builder().build();
    }


    private void showInterstitialAd() {
        InterstitialAd.load(requireActivity(), "ca-app-pub-5778282166425967/1523915565", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                AppUtils.hideDialog();
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d(TAG, "The ad was dismissed.");

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d(TAG, "The ad failed to show. " + adError.getMessage());

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd = null;
                        Log.d(TAG, "The ad was shown.");
                    }
                });
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(requireActivity());
                } else {
                    Log.d(TAG, "The interstitial ad wasn't ready yet.");
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
                AppUtils.hideDialog();

                Log.d(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
            }

        });

    }

    private void loadCategoryData() {
        AppUtils.showRequestDialog(requireActivity());
        AppUtils.getFireStoreReference().collection(AppConstant.CATEGORY).get().addOnSuccessListener(queryDocumentSnapshots -> {
            AppUtils.hideDialog();
            snapshots.clear();
            if (null == queryDocumentSnapshots || queryDocumentSnapshots.isEmpty())
                return;
            snapshots.addAll(queryDocumentSnapshots.getDocuments());
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireActivity(), "try again !!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
            AppUtils.hideDialog();
        });
    }

    @Override
    public void onItemClicked(Object o) {
        String id = (String) o;
        switch (id) {
            case AppConstant.FAVOURITE_ID:
                navController.navigate(R.id.action_dashboardFragment_to_favouriteMsgFragment);
                break;
            default: {
                DashboardFragmentDirections.ActionDashboardFragmentToDataFragment action = DashboardFragmentDirections.actionDashboardFragmentToDataFragment();
                action.setId(id);
                navController.navigate(action);
            }
        }


    }
}
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

import com.google.firebase.firestore.DocumentSnapshot;
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

    @Override

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dashboardBinding = FragmentDashboardBinding.inflate(getLayoutInflater());
        return dashboardBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        snapshots = new ArrayList<>();
        adapter = new HomeAdapter(snapshots,this);
        dashboardBinding.recHome.setAdapter(adapter);
        loadCategoryData();
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
        DashboardFragmentDirections.ActionDashboardFragmentToDataFragment action = DashboardFragmentDirections.actionDashboardFragmentToDataFragment();
        action.setId((String) o);
        navController.navigate(action);
    }
}
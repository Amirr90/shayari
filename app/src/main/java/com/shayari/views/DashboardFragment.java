package com.shayari.views;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;
import com.shayari.R;
import com.shayari.adapter.HomeAdapter;
import com.shayari.databinding.FragmentDashboardBinding;
import com.shayari.interfaces.AdapterInterface;
import com.shayari.utils.AppConstant;
import com.shayari.utils.AppUtils;

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
        AppUtils.getFireStoreReference().collection(AppConstant.CATEGORY).get().addOnSuccessListener(queryDocumentSnapshots -> {
            snapshots.clear();
            if (null == queryDocumentSnapshots || queryDocumentSnapshots.isEmpty())
                return;
            snapshots.addAll(queryDocumentSnapshots.getDocuments());
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireActivity(), "try again !!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
        });
    }

    @Override
    public void onItemClicked(Object o) {
        DashboardFragmentDirections.ActionDashboardFragmentToDataFragment action = DashboardFragmentDirections.actionDashboardFragmentToDataFragment();
        action.setId((String) o);
        navController.navigate(action);
    }
}
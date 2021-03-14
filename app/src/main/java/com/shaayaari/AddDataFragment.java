package com.shaayaari;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.shaayaari.databinding.FragmentAddDataBinding;
import com.shaayaari.databinding.FragmentDataBinding;
import com.shaayaari.models.CategoryModel;
import com.shaayaari.utils.App;
import com.shaayaari.utils.AppConstant;
import com.shaayaari.utils.AppUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AddDataFragment extends Fragment {

    FragmentAddDataBinding binding;
    String catId;
    NavController navController;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddDataBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) {
            return;
        }

        navController = Navigation.findNavController(view);
        catId = AddDataFragmentArgs.fromBundle(getArguments()).getCatId();

        binding.tvCatId.setText("Category " + catId);


        binding.btnAddMsg.setOnClickListener(v -> {
            String msg = binding.editTextTextPersonName.getText().toString();
            if (!TextUtils.isEmpty(msg)) {
                addMsg(msg);
            } else
                Toast.makeText(requireActivity(), "Add some msg before press add Button !!", Toast.LENGTH_SHORT).show();
        });

        binding.btnAddMsg.setVisibility(AppConstant.ADMIN_ID.equals(AppUtils.getUid())?View.VISIBLE:View.GONE);
    }

    private void addMsg(String msg) {
        AppUtils.hideSoftKeyboard(requireActivity());
        AppUtils.showRequestDialog(requireActivity());
        AppUtils.getFireStoreReference().collection(AppConstant.DATA).add(getMsgMap(msg))
                .addOnSuccessListener(documentReference -> {
                    AppUtils.hideDialog();
                    Toast.makeText(requireActivity(), "Added successfully !!", Toast.LENGTH_SHORT).show();
                    navController.navigateUp();
                })
                .addOnFailureListener(e -> {
                    AppUtils.hideDialog();
                    Toast.makeText(requireActivity(), "try again !!", Toast.LENGTH_SHORT).show();

                });
    }

    private Map<String, Object> getMsgMap(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstant.CATEGORY, catId);
        map.put(AppConstant.FAVOURITE_IDS, FieldValue.arrayUnion(""));
        map.put(AppConstant.LIKE_IDS, FieldValue.arrayUnion(""));
        map.put(AppConstant.LIKES, 0);
        map.put(AppConstant.MSG, msg);
        map.put(AppConstant.TIMESTAMP, System.currentTimeMillis());
        map.put(AppConstant.ADDED_BY, AppUtils.getUid());
        map.put(AppConstant.STATUS, AppConstant.PENDING);
        return map;
    }
}
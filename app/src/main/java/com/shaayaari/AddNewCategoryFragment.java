package com.shaayaari;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shaayaari.databinding.FragmentAddNewCategoryBinding;
import com.shaayaari.utils.AppConstant;
import com.shaayaari.utils.AppUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class AddNewCategoryFragment extends Fragment {
    private static final String TAG = "AddNewCategoryFragment";


    FragmentAddNewCategoryBinding binding;
    NavController navController;
    String catId, catTitle;
    Uri imageUri;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNewCategoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        navController = Navigation.findNavController(view);
        progressDialog = new ProgressDialog(requireActivity());


        binding.btnAddNewCategory.setOnClickListener(v -> {
            catId = binding.editTextTextPersonName2.getText().toString().trim();
            catTitle = binding.editTextTextPersonName3.getText().toString().trim();

            if (TextUtils.isEmpty(catId))
                Toast.makeText(requireActivity(), "Add Category Id !!", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(catTitle))
                Toast.makeText(requireActivity(), "Add Category Title !!", Toast.LENGTH_SHORT).show();
            else {
                if (null == imageUri)
                    addNewCategory("");
                else uploadImageToFirebase();
            }

        });
        binding.ivCatIcon.setOnClickListener(v -> selectImage());
    }

    private void uploadImageToFirebase() {


        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();


        progressDialog.setMessage("Uploading image, please wait...");
        progressDialog.show();
        final String STORAGE_PATH = "shaayaariImage/" + catId + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference spaceRef = storageRef.child(STORAGE_PATH);

        UploadTask uploadTask = spaceRef.putFile(imageUri);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            progressDialog.setProgress((int) progress);
        }).addOnSuccessListener(taskSnapshot -> storageRef.child(STORAGE_PATH).getDownloadUrl().addOnSuccessListener(uri1 -> {
            progressDialog.dismiss();
            addNewCategory(uri1.toString());
        })).addOnCanceledListener(() -> {
            progressDialog.dismiss();
            Toast.makeText(requireActivity(), "Upload cancelled, try again", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(requireActivity(), "failed to upload Image " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });


    }


    private void selectImage() {
        String[] mimeType = {"image/png", "image/jpg", "image/jpeg"};
        ImagePicker.Companion.with(this)
                .compress(512)//Final image size will be less than 1 MB(Optional)
                .crop(4f, 4f)
                .galleryOnly()
                .galleryMimeTypes(mimeType)
                .maxResultSize(800, 800)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (null != data) {
                Uri uri = data.getData();
                binding.ivCatIcon.setImageURI(uri);
                Log.d(TAG, "onActivityResult: Uri" + data.getData());
                imageUri = uri;
            } else Log.d(TAG, "onActivityResult: No Data ");
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), "Result Error ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireActivity(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }


    }

    private void addNewCategory(String s) {
        AppUtils.showRequestDialog(requireActivity());
        AppUtils.getFireStoreReference().collection(AppConstant.CATEGORY).document(catId).set(getCategoryMap(s)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AppUtils.hideDialog();
                Toast.makeText(requireActivity(), "new Category Added successfully !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Object getCategoryMap(String s) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstant.TITLE, catTitle);
        map.put(AppConstant.IMAGE, s);
        return map;
    }
}
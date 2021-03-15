package com.shaayaari;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shaayaari.databinding.FragmentAddDataBinding;
import com.shaayaari.databinding.FragmentDataBinding;
import com.shaayaari.models.CategoryModel;
import com.shaayaari.utils.App;
import com.shaayaari.utils.AppConstant;
import com.shaayaari.utils.AppUtils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.shaayaari.utils.AppUtils.getFireStoreReference;
import static com.shaayaari.utils.AppUtils.getUid;

public class AddDataFragment extends Fragment {
    private static final String TAG = "AddDataFragment";
    FragmentAddDataBinding binding;
    String catId;
    NavController navController;
    Uri imageUri;
    ProgressDialog progressDialog;


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

        progressDialog = new ProgressDialog(requireActivity());

        navController = Navigation.findNavController(view);
        catId = AddDataFragmentArgs.fromBundle(getArguments()).getCatId();

        binding.tvCatId.setText("Category " + catId);


        binding.btnAddMsg.setOnClickListener(v -> {
            String msg = binding.editTextTextPersonName.getText().toString();
            if (!TextUtils.isEmpty(msg) && null == imageUri) {
                addMsg(msg, "");
            } else if (TextUtils.isEmpty(msg) && null != imageUri)
                uploadImageToFirebase();
            else
                Toast.makeText(requireActivity(), "Add some msg or image before press add Button !!", Toast.LENGTH_SHORT).show();
        });

        binding.btnAddMsg.setVisibility(AppConstant.ADMIN_ID.equals(getUid()) ? View.VISIBLE : View.GONE);

        binding.btnSelectImage.setOnClickListener(v -> selectImage());
    }

    private void selectImage() {

        ImagePicker.Companion.with(this)
                .compress(512)//Final image size will be less than 1 MB(Optional)
                .crop(5.5f, 5.5f)
                .maxResultSize(800, 800)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (null != data) {
                Uri uri = data.getData();
                binding.btnSelectImage.setImageURI(uri);
                Log.d(TAG, "onActivityResult: Uri" + data.getData());
                imageUri = uri;
            } else Log.d(TAG, "onActivityResult: No Data ");
        } else Log.d(TAG, "onActivityResult: resultCode not matched");

    }


    private void uploadImageToFirebase() {
        progressDialog.show();
        progressDialog.setMessage("Uploading image, please wait...");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();


        final String STORAGE_PATH = "shaayaariImage/" + catId + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference spaceRef = storageRef.child(STORAGE_PATH);

        Bitmap bitmap2 = ((BitmapDrawable) binding.btnSelectImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] compressData = baos.toByteArray();
        UploadTask uploadTask = spaceRef.putBytes(compressData);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            progressDialog.setProgress((int) progress);
        }).addOnSuccessListener(taskSnapshot -> storageRef.child(STORAGE_PATH).getDownloadUrl().addOnSuccessListener(uri1 -> {
            progressDialog.dismiss();
            addMsg("", uri1.toString());
        })).addOnCanceledListener(() -> {
            progressDialog.dismiss();
            Toast.makeText(requireActivity(), "Upload cancelled, try again", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(requireActivity(), "failed to upload Image " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    private void addMsg(String msg, String image) {
        AppUtils.hideSoftKeyboard(requireActivity());
        AppUtils.showRequestDialog(requireActivity());
        getFireStoreReference().collection(AppConstant.DATA).add(getMsgMap(msg, image))
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

    private Map<String, Object> getMsgMap(String msg, String image) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstant.CATEGORY, catId);
        map.put(AppConstant.FAVOURITE_IDS, FieldValue.arrayUnion(""));
        map.put(AppConstant.LIKE_IDS, FieldValue.arrayUnion(""));
        map.put(AppConstant.LIKES, 0);
        map.put(AppConstant.MSG, msg);
        map.put(AppConstant.IMAGE, image);
        map.put(AppConstant.TIMESTAMP, System.currentTimeMillis());
        map.put(AppConstant.ADDED_BY, getUid());
        map.put(AppConstant.STATUS, AppConstant.PENDING);
        return map;
    }
}
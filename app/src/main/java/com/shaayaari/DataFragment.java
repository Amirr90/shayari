package com.shaayaari;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.firestore.Query;
import com.shaayaari.databinding.FragmentDataBinding;
import com.shaayaari.databinding.ShayariView2Binding;
import com.shaayaari.interfaces.DataPageInterface;
import com.shaayaari.models.CategoryModel;
import com.shaayaari.utils.App;
import com.shaayaari.utils.AppConstant;
import com.shaayaari.utils.AppUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.shaayaari.utils.AppUtils.getFavouriteMap;
import static com.shaayaari.utils.AppUtils.getLikeUpdateMap;
import static com.shaayaari.utils.AppUtils.getUid;
import static com.shaayaari.utils.AppUtils.updateFavouriteIds;


public class DataFragment extends Fragment {
    private static final String TAG = "DataFragment";
    FragmentDataBinding binding;
    NavController navController;
    FirestorePagingAdapter adapter;
    String catId;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDataBinding.inflate(getLayoutInflater());
        initAds();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        if (null != getArguments()) {
            catId = DataFragmentArgs.fromBundle(getArguments()).getId();
        }

        Objects.requireNonNull(navController.getCurrentDestination()).setLabel(catId);
        setAdapter();

        binding.btnAddData.setOnClickListener(v -> {
            DataFragmentDirections.ActionDataFragmentToAddDataFragment action = DataFragmentDirections.actionDataFragmentToAddDataFragment();
            action.setCatId(catId);
            navController.navigate(action);
        });

        binding.btnAddData.setVisibility(AppConstant.ADMIN_ID.equals(AppUtils.getUid()) ? View.VISIBLE : View.GONE);

    }

    private void setAdapter() {

        AppUtils.showRequestDialog(requireActivity());
        Query query = AppUtils.getFireStoreReference().collection(AppConstant.DATA)
                .whereEqualTo(AppConstant.CATEGORY, catId)
                .orderBy(AppConstant.TIMESTAMP, Query.Direction.DESCENDING);


        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(5)
                .setPageSize(3)
                .build();
        FirestorePagingOptions<CategoryModel> options = new FirestorePagingOptions.Builder<CategoryModel>()
                .setQuery(query, config, snapshot -> {
                    CategoryModel categoryModel = snapshot.toObject(CategoryModel.class);
                    if (null != categoryModel) {
                        categoryModel.setId(snapshot.getId());

                        //setting likes
                        for (String likes : categoryModel.getLikeIds()) {
                            categoryModel.setLike(likes.equals(getUid()));
                        }
                        //setting favourite
                        for (String favourite : categoryModel.getFavouriteIds()) {
                            categoryModel.setFavourite(favourite.equals(getUid()));
                        }
                    }
                    return categoryModel;
                })
                .build();


        AppUtils.hideDialog();

        //creating Adapter
        adapter = new FirestorePagingAdapter<CategoryModel, CategoryViewHolder>(options) {
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                ShayariView2Binding binding = ShayariView2Binding.inflate(layoutInflater, parent, false);
                binding.setDataInterface(dataPageInterface);
                return new CategoryViewHolder(binding);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull CategoryModel model) {
                holder.binding.setData(model);
                holder.binding.btnFavourite.setChecked(model.getFavourite());
                holder.binding.btnLikes.setOnClickListener(v -> {
                    int likes = Integer.parseInt(holder.binding.textView6.getText().toString());
                    String id = model.getId();
                    if (holder.binding.btnLikes.isChecked()) {
                        likes = likes + 1;
                    } else {
                        likes = likes - 1;
                    }

                    dataPageInterface.onLikeClicked(holder.binding.btnLikes.isChecked() ? AppConstant.INCREMENT : AppConstant.DECREMENT, id);
                    holder.binding.textView6.setText(String.valueOf(likes));
                });
                holder.binding.btnFavourite.setOnClickListener(v -> {
                    dataPageInterface.onFavouriteBtnClicked(model, holder.binding.btnFavourite.isChecked());

                });
                setBannerAdd(holder.binding.adView);


                if (position % 2 == 0)
                    holder.binding.getRoot().setBackgroundColor(getResources().getColor(R.color.colorGray));
                else
                    holder.binding.getRoot().setBackgroundColor(getResources().getColor(R.color.white));

            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                switch (state) {
                    case ERROR: {
                        Log.d(TAG, "onLoadingStateChanged: error ");
                        Toast.makeText(requireActivity(), "failed to get Data !!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case FINISHED: {
                        Log.d(TAG, "onLoadingStateChanged: FINISHED");
                        Toast.makeText(requireActivity(), "No more data !!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case LOADED: {
                        Log.d(TAG, "onLoadingStateChanged: LOADED " + getItemCount());
                    }
                    case LOADING_MORE: {
                        Log.d(TAG, "onLoadingStateChanged: LOADING_MORE");
                    }
                    case LOADING_INITIAL: {
                        Log.d(TAG, "onLoadingStateChanged: LOADING_INITIAL");
                    }
                    break;
                }
            }
        };


        binding.recShayariList.setHasFixedSize(true);
        binding.recShayariList.setAdapter(adapter);

    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void setBannerAdd(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.d(TAG, "onAdFailedToLoad: ");
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened: ");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication: ");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed: ");
            }
        });
    }

    private void initAds() {
        MobileAds.initialize(requireActivity(), initializationStatus -> Log.d(TAG, "onInitializationComplete: " + initializationStatus));
    }

    DataPageInterface dataPageInterface = new DataPageInterface() {
        @Override
        public void onShareBtnClicked(Object obj) {
            CategoryModel categoryModel = (CategoryModel) obj;
            if (null != categoryModel.getImage() && !categoryModel.getImage().isEmpty()) {
                shareImage(categoryModel.getImage());
            } else {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                String shareBody = categoryModel.getMsg();
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
            }

        }

        @Override
        public void onFavouriteBtnClicked(Object model, Object var) {
            CategoryModel msgModel = (CategoryModel) model;
            String id = msgModel.getId();

            if ((boolean) var) {
                if (null != getUid())
                    AppUtils.getFireStoreReference().collection(AppConstant.FAVOURITE)
                            .document(getUid())
                            .collection(AppConstant.FAVOURITE)
                            .document(msgModel.getId()).set(getFavouriteMap(msgModel))
                            .addOnSuccessListener(aVoid -> {
                                updateFavouriteIds((boolean) var, id);
                            })
                            .addOnFailureListener(e -> Toast.makeText(requireActivity(), "try again !!", Toast.LENGTH_SHORT).show());
            } else {
                if (null != getUid())
                    AppUtils.getFireStoreReference().collection(AppConstant.FAVOURITE)
                            .document(getUid())
                            .collection(AppConstant.FAVOURITE)
                            .document(msgModel.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {

                                updateFavouriteIds((boolean) var, id);
                            })
                            .addOnFailureListener(e -> Toast.makeText(requireActivity(), "try again !!", Toast.LENGTH_SHORT).show());
            }

        }

        @Override
        public void onLikeClicked(Object obj, String id) {
            AppUtils.getFireStoreReference()
                    .collection(AppConstant.DATA)
                    .document(id)
                    .update(getLikeUpdateMap(AppConstant.INCREMENT.equals((String) obj)));

        }

        @Override
        public void onCopyBtnClicked(Object obj) {
            CategoryModel msgModel = (CategoryModel) obj;
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", msgModel.getMsg());
            if (clipboard == null || clip == null) return;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireActivity(), "copied !!", Toast.LENGTH_SHORT).show();
        }
    };

    public void shareImage(String url) {
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            //bmpUri = Uri.fromFile(file);

            bmpUri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.shaayaari.provider", //(use your app signature + ".provider" )
                    file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
package com.shayari;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.paging.PagedList;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.shayari.databinding.FragmentDataBinding;
import com.shayari.databinding.ShayariViewBinding;
import com.shayari.interfaces.DataPageInterface;
import com.shayari.models.CategoryModel;
import com.shayari.utils.AppConstant;
import com.shayari.utils.AppUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.shayari.utils.AppUtils.getUid;


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
    }

    private void setAdapter() {
        Query query = AppUtils.getFireStoreReference().collection(AppConstant.DATA)
                .whereEqualTo(AppConstant.CATEGORY, catId);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();
        FirestorePagingOptions<CategoryModel> options = new FirestorePagingOptions.Builder<CategoryModel>()
                .setLifecycleOwner(requireActivity())
                .setQuery(query, config, snapshot -> {
                    CategoryModel categoryModel = snapshot.toObject(CategoryModel.class);
                    if (null != categoryModel)
                        categoryModel.setId(snapshot.getId());
                    return categoryModel;
                })
                .build();


        //creating Adapter
        adapter = new FirestorePagingAdapter<CategoryModel, CategoryViewHolder>(options) {
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                ShayariViewBinding binding = ShayariViewBinding.inflate(layoutInflater, parent, false);
                binding.setDataInterface(dataPageInterface);
                return new CategoryViewHolder(binding);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull CategoryModel model) {
                holder.binding.setData(model);
                holder.binding.btnLikes.setOnClickListener(v -> {
                    int likes = Integer.parseInt(holder.binding.textView6.getText().toString());
                    String id = model.getId();
                    if (holder.binding.btnLikes.isChecked()) {
                        likes = likes + 1;
                        dataPageInterface.onLikeClicked(AppConstant.INCREMENT, id);

                    } else {
                        likes = likes - 1;
                        dataPageInterface.onLikeClicked(AppConstant.DECREMENT, id);

                    }
                    holder.binding.textView6.setText("" + likes);
                });
                holder.binding.btnFavourite.setOnClickListener(v -> {
                    if (holder.binding.btnFavourite.isChecked()) {
                        dataPageInterface.onFavouriteBtnClicked(model, true);
                    } else {
                        dataPageInterface.onFavouriteBtnClicked(model, false);
                    }

                });
                setBannerAdd(holder.binding.adView);


                //changing like btn view
                if (null != getUid() && null != model.getLikeIds())
                    for (int a = 0; a < model.getLikeIds().size(); a++) {
                        if (getUid().equals(model.getLikeIds().get(a))) {
                            holder.binding.btnLikes.setChecked(true);
                            break;
                        }
                    }

                //changing favourite btn view
                if (null != getUid() && null != model.getFavouriteIds())
                    for (int a = 0; a < model.getFavouriteIds().size(); a++) {
                        if (getUid().equals(model.getFavouriteIds().get(a))) {
                            holder.binding.btnFavourite.setChecked(true);
                            break;
                        }
                    }

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
            /*Create an ACTION_SEND Intent*/
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            /*This will be the actual content you wish you share.*/
            String shareBody = categoryModel.getMsg();
            /*The type of the content is text, obviously.*/
            intent.setType("text/plain");
            /*Applying information Subject and Body.*/
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            /*Fire!*/
            startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
        }

        @Override
        public void onFavouriteBtnClicked(Object model, Object var) {
            CategoryModel msgModel = (CategoryModel) model;
            String id = msgModel.getId();

            CategoryModel model1 = new CategoryModel();
            model1.setId(getUid());
            model1.setMsg(msgModel.getMsg());

            if ((boolean) var) {
                if (null != getUid())
                    AppUtils.getFireStoreReference().collection(AppConstant.FAVOURITE)
                            .document(msgModel.getId()).set(model1)
                            .addOnSuccessListener(aVoid -> {
                                updateFavouriteIds((boolean) var, id);
                            })
                            .addOnFailureListener(e -> Toast.makeText(requireActivity(), "try again !!", Toast.LENGTH_SHORT).show());
            } else {
                if (null != getUid())
                    AppUtils.getFireStoreReference().collection(AppConstant.FAVOURITE)
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
    };

    private Map<String, Object> getLikeUpdateMap(boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstant.LIKES, status ? FieldValue.increment(1) : FieldValue.increment(-1));
        map.put(AppConstant.LIKE_IDS, status ? FieldValue.arrayUnion(getUid()) : FieldValue.arrayRemove(getUid()));
        return map;

    }

    private void updateFavouriteIds(boolean var, Object model) {
        Log.d(TAG, "updateFavouriteIds: " + (String) model);
        if (var) {
            AppUtils.getFireStoreReference().collection(AppConstant.DATA)
                    .document((String) model)
                    .update(AppConstant.FAVOURITE_IDS, FieldValue.arrayUnion(getUid()));
            Toast.makeText(requireActivity(), "Added as favourite !!", Toast.LENGTH_SHORT).show();
        } else {
            AppUtils.getFireStoreReference().collection(AppConstant.DATA)
                    .document((String) model)
                    .update(AppConstant.FAVOURITE_IDS, FieldValue.arrayRemove(getUid()));
            Toast.makeText(requireActivity(), "Removed From Favourite !!", Toast.LENGTH_SHORT).show();

        }
    }

}
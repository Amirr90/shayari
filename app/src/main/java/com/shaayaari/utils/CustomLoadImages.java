package com.shaayaari.utils;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.shaayaari.R;

public class CustomLoadImages {
    private static final String TAG = "CustomLoadImages";

    @BindingAdapter("android:loadCustomImage")
    public static void loadCustomImage(ImageView imageView, String imagePath) {
        if (null != imagePath && !imagePath.isEmpty()) {
            try {
                Glide.with(App.context)
                        .load(imagePath)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(imageView);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "loadImage: " + e.getLocalizedMessage());
            }
        }
    }
}

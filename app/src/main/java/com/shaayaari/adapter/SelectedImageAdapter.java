package com.shaayaari.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shaayaari.databinding.ImageViewBinding;

import java.util.ArrayList;
import java.util.List;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ImageVH> {
    List<Uri> list;

    public SelectedImageAdapter(List<Uri> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SelectedImageAdapter.ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ImageViewBinding binding = ImageViewBinding.inflate(inflater, parent, false);
        return new ImageVH(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImageAdapter.ImageVH holder, int position) {
        holder.binding.imageView3.setImageURI(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list == null)
            list = new ArrayList<>();

        return list.size();
    }

    public static class ImageVH extends RecyclerView.ViewHolder {
        ImageViewBinding binding;

        public ImageVH(@NonNull ImageViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

package com.shayari;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    com.shayari.databinding.ShayariViewBinding binding;

    public CategoryViewHolder(@NonNull com.shayari.databinding.ShayariViewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

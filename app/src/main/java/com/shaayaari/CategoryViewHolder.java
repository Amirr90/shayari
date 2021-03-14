package com.shaayaari;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    com.shaayaari.databinding.ShayariView2Binding binding;

    public CategoryViewHolder(@NonNull com.shaayaari.databinding.ShayariView2Binding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

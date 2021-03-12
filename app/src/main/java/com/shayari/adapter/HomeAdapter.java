package com.shayari.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.shayari.databinding.HomeViewBinding;
import com.shayari.interfaces.AdapterInterface;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeVH> {
    List<DocumentSnapshot> homeModels;
    AdapterInterface adapterInterface;

    public HomeAdapter(List<DocumentSnapshot> homeModels, AdapterInterface adapterInterface) {
        this.homeModels = homeModels;
        this.adapterInterface = adapterInterface;
    }

    @NonNull
    @Override
    public HomeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        HomeViewBinding binding = HomeViewBinding.inflate(inflater, parent, false);
        return new HomeVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeVH holder, int position) {
        holder.binding.setHome(homeModels.get(position));

        holder.binding.getRoot().setOnClickListener(v -> adapterInterface.onItemClicked(homeModels.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return homeModels.size();
    }

    public static class HomeVH extends RecyclerView.ViewHolder {
        HomeViewBinding binding;

        public HomeVH(@NonNull HomeViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

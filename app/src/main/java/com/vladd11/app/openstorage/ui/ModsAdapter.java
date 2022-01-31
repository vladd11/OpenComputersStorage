package com.vladd11.app.openstorage.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vladd11.app.openstorage.databinding.ModHolderBinding;

public class ModsAdapter extends RecyclerView.Adapter<ModsAdapter.ModsViewHolder> {

    @NonNull
    @Override
    public ModsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final ModHolderBinding binding = ModHolderBinding.inflate(layoutInflater, parent, false);

        return new ModsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ModsViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class ModsViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ModHolderBinding binding;

        public ModsViewHolder(ModHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind() {
            binding.setName("Re");
        }
    }
}

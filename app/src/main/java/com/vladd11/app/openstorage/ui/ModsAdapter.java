package com.vladd11.app.openstorage.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.vladd11.app.openstorage.databinding.ModHolderBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModsAdapter extends RecyclerView.Adapter<ModsAdapter.ModsViewHolder> {
    private final SortedList<String> list = new SortedList<>(String.class, new SortedList.Callback<String>() {
        @Override
        public int compare(String a, String b) {
            return a.compareTo(b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(String oldString, String newString) {
            return oldString.equals(newString);
        }

        @Override
        public boolean areItemsTheSame(String string1, String string2) {
            return string1 == string2;
        }
    });
    private Map<String, String> mods;
    private final HashMap<String, String> checked = new HashMap<>();

    @NonNull
    @Override
    public ModsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final ModHolderBinding binding = ModHolderBinding.inflate(layoutInflater, parent, false);
        binding.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(propertyId == BR.checked) {
                    if(binding.getChecked()) {
                        checked.put(binding.getName(), binding.getGameName());
                    } else checked.remove(binding.getName());
                }
            }
        });

        return new ModsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ModsViewHolder holder, int position) {
        final String mod = list.get(position);
        holder.bind(mod, mods.get(mod));
    }

    public void replaceAll(Map<String, String> mods) {
        list.beginBatchedUpdates();
        this.mods = mods;
        for (int i = list.size() - 1; i >= 0; i--) {
            final String string = list.get(i);
            list.remove(string);
        }
        list.addAll(mods.keySet());
        list.endBatchedUpdates();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public HashMap<String, String> getSelectedMods() {
        return checked;
    }

    static class ModsViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ModHolderBinding binding;

        public ModsViewHolder(ModHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String name, String gameName) {
            binding.setName(name);
            binding.setGameName(gameName);
        }
    }
}

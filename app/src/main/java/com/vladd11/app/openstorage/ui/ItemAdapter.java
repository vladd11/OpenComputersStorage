package com.vladd11.app.openstorage.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.vladd11.app.openstorage.utils.Item;
import com.vladd11.app.openstorage.R;
import com.vladd11.app.openstorage.databinding.ImageItemHolderBinding;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private static final Comparator<Item> COMPARATOR = (a, b) -> b.chest.positionX - a.chest.positionX;
    public final SortedList<Item> list = new SortedList<>(Item.class, new SortedList.Callback<Item>() {
        @Override
        public int compare(Item a, Item b) {
            return b.chest.positionX - a.chest.positionX;
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
        public boolean areContentsTheSame(Item oldItem, Item newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Item item1, Item item2) {
            return item1 == item2;
        }
    });

    private ItemAdapterListener adapterListener;

    public interface ItemAdapterListener {

        void onRequest(Item item);

        void onLongRequest(Item item);
    }

    public void setListener(ItemAdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    public abstract static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Item item) {
        }
    }

    public static class ImageItemViewHolder extends ItemViewHolder {
        private final TextView countView;
        private final ImageView imageView;
        private final ImageItemHolderBinding binding;
        private final ItemAdapterListener listener;

        public ImageItemViewHolder(@NonNull View view, ImageItemHolderBinding binding, ItemAdapterListener listener) {
            super(view);

            countView = view.findViewById(R.id.itemCountView);
            imageView = view.findViewById(R.id.itemView);
            this.binding = binding;
            this.listener = listener;
        }

        public void bind(@NonNull Item item) {
            binding.setItem(item);

            imageView.setImageDrawable(item.image);
            imageView.setContentDescription(item.name);
            countView.setText(String.valueOf(item.count));
            if (listener != null) {
                countView.setOnClickListener(v -> listener.onRequest(item));
                countView.setOnLongClickListener(v -> {
                    listener.onLongRequest(item);
                    return false;
                });
            }
        }
    }

    public static class TextItemViewHolder extends ItemViewHolder {
        @NonNull
        private final TextView view;
        private final ItemAdapterListener listener;

        public TextItemViewHolder(@NonNull View view, ItemAdapterListener listener) {
            super(view);
            this.view = (TextView) view;
            this.listener = listener;
        }

        public void bind(@NonNull Item item) {
            view.setText(String.format(Locale.getDefault(), "%s (*%d)", item.title, item.count));

            if (listener != null) {
                view.setOnClickListener(v -> listener.onRequest(item));
                view.setOnLongClickListener(v -> {
                    listener.onLongRequest(item);
                    return false;
                });
            }
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        // Create a new view, which defines the UI of the list item
        if (viewType == 1) {
            final View view = layoutInflater.inflate(R.layout.text_item_holder, viewGroup, false);

            return new TextItemViewHolder(view, adapterListener);
        }
        final View view = layoutInflater.inflate(R.layout.image_item_holder, viewGroup, false);

        final ImageItemHolderBinding binding = ImageItemHolderBinding.inflate(layoutInflater, viewGroup, false);
        return new ImageItemViewHolder(view, binding, adapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).image == null) return 1;
        return 0;
    }

    public void add(Item item) {
        list.add(item);
    }

    public void remove(Item item) {
        list.remove(item);
    }

    public void add(List<Item> items) {
        list.addAll(items);
    }

    public void remove(List<Item> items) {
        list.beginBatchedUpdates();
        for (Item item : items) {
            list.remove(item);
        }
        list.endBatchedUpdates();
    }

    public void replaceAll(List<Item> items) {
        list.beginBatchedUpdates();
        for (int i = list.size() - 1; i >= 0; i--) {
            final Item item = list.get(i);
            list.remove(item);
        }
        list.addAll(items);
        list.endBatchedUpdates();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
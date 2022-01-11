package com.vladd11.app.openstorage.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vladd11.app.openstorage.Item;
import com.vladd11.app.openstorage.R;
import com.vladd11.app.openstorage.Server;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Server server;

    public abstract static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setItem(Item item) {}
    }

    public static class ImageItemViewHolder extends ItemViewHolder {
        private final TextView textView;
        private final Server server;
        private Item item;

        public ImageItemViewHolder(@NonNull View view, Server server) {
            super(view);

            textView = view.findViewById(R.id.itemView);
            this.server = server;
            textView.setOnClickListener(v -> server.addItemRequest(item));
        }

        public void setItem(@NonNull Item item) {
            this.item = item;
            textView.setBackground(item.image);
            textView.setText(String.valueOf(item.count));
            textView.setOnClickListener(v -> server.addItemRequest(item));
        }
    }

    public static class TextItemViewHolder extends ItemViewHolder {
        @NonNull
        private final TextView view;
        private final Server server;

        public TextItemViewHolder(@NonNull View view, Server server) {
            super(view);
            this.view = (TextView) view;
            this.server = server;
        }

        public void setItem(@NonNull Item item) {
            view.setText(String.format("%s (*%d)", item.label, item.count));
            view.setOnClickListener(v -> server.addItemRequest(item));
        }
    }

    public ItemAdapter(Server server) {
        this.server = server;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        if(viewType == 1) {
            final View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.text_item_holder, viewGroup, false);

            return new TextItemViewHolder(view, server);
        }
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_item_holder, viewGroup, false);

        return new ImageItemViewHolder(view, server);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setItem(server.getLastItemList().get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if(server.getLastItemList().get(position).image == null) return 1;
        return 0;
    }

    @Override
    public int getItemCount() {
        return server.getLastItemList().size();
    }
}
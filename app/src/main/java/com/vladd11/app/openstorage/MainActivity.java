package com.vladd11.app.openstorage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.vladd11.app.openstorage.databinding.ActivityMainBinding;
import com.vladd11.app.openstorage.ui.ChestUpdateDialog;
import com.vladd11.app.openstorage.ui.ItemAdapter;
import com.vladd11.app.openstorage.ui.ItemCountRequestDialog;
import com.vladd11.app.openstorage.utils.Item;
import com.vladd11.app.openstorage.utils.ItemRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Server server;
    private ActivityMainBinding binding;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        final TextureAtlas atlas = new TextureAtlas(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        server = new Server(atlas);
        binding.setServer(server);

        server.setListener(new Server.ServerListener() {
            @Override
            public void onItemsUpdated(List<Item> items) {
                handler.post(() -> adapter.replaceAll(items));
            }

            @Override
            public void onCommandsDelivered() {
                binding.notifyPropertyChanged(BR.server);
            }
        });
        try {
            server.start(50000);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 8);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position < server.getLastItemList().size() &&
                        server.getLastItemList().get(position).image == null) {
                    return 8;
                } else return 1;
            }
        });
        binding.recyclerView.setLayoutManager(layoutManager);

        adapter = new ItemAdapter();
        adapter.setListener(new ItemAdapter.ItemAdapterListener() {
            @Override
            public void onRequest(Item item) {
                server.addItemRequest(new ItemRequest(item, item.count));
            }

            @Override
            public void onLongRequest(Item item) {
                new ItemCountRequestDialog(item).setListener(new ItemCountRequestDialog.ItemCountRequestDialogListener() {
                    @Override
                    public void onAddToRequestClicked(ItemRequest request) {
                        server.addItemRequest(request);
                    }

                    @Override
                    public void onRequestNowClicked(ItemRequest request) {
                        server.addItemRequest(request);
                        server.setRequest(true);
                    }
                }).show(getSupportFragmentManager(), "request");
            }
        });
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0) {
            new ChestUpdateDialog(server).show(getSupportFragmentManager(), "update");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.stop();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Item> filteredModelList = filter(server.getLastItemList(), newText);
        adapter.replaceAll(filteredModelList);
        binding.recyclerView.scrollToPosition(0);
        return true;
    }

    private static List<Item> filter(List<Item> items, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Item> filteredModelList = new ArrayList<>();
        for (Item item : items) {
            final String text = item.title.toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(item);
            }
        }
        return filteredModelList;
    }
}
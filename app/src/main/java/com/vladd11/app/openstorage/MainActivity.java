package com.vladd11.app.openstorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import androidx.appcompat.widget.Toolbar;

import com.vladd11.app.openstorage.ui.ChestUpdateDialog;
import com.vladd11.app.openstorage.ui.ItemAdapter;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Server server;
    private final TextureAtlas atlas = new TextureAtlas(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final MutableLiveData<Boolean> deliverLiveData = new MutableLiveData<>();
        final ToggleButton sortButton = findViewById(R.id.sort);
        sortButton.setOnClickListener(v -> {
            server.setSort(true);
            v.setClickable(false);
        });
        deliverLiveData.observe(this, aBoolean -> {
            sortButton.setClickable(true);
            sortButton.setChecked(false);
        });
        server = new Server(atlas, deliverLiveData);
        try {
            server.start(50000);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(server.getLastItemList().get(position).image == null) {
                    return 4;
                } else return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        final ItemAdapter adapter = new ItemAdapter(server);
        server.setAdapter(adapter);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == 0) {
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
}
package com.vladd11.app.openstorage;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.vladd11.app.openstorage.databinding.ActivityInitialBinding;
import com.vladd11.app.openstorage.databinding.FragmentDownloadBinding;
import com.vladd11.app.openstorage.ui.DownloadFragment;

import java.io.File;

public class InitialActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityInitialBinding binding;
    private ButtonInterface buttonInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(new File(getExternalFilesDir("textures"), "exclusions.json").exists()) {
            final Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return;
        }

        binding = ActivityInitialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_initial);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(view -> {
            if (buttonInterface != null) buttonInterface.onDownloadButtonClicked();
            binding.fab.setEnabled(false);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_initial);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setButtonInterface(ButtonInterface buttonInterface) {
        this.buttonInterface = buttonInterface;
    }

    public interface ButtonInterface {
        void onDownloadButtonClicked();
    }
}
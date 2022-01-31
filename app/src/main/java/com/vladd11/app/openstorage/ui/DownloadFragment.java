package com.vladd11.app.openstorage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.vladd11.app.openstorage.InitialActivity;
import com.vladd11.app.openstorage.MainActivity;
import com.vladd11.app.openstorage.R;
import com.vladd11.app.openstorage.databinding.FragmentDownloadBinding;
import com.vladd11.app.openstorage.downloader.DownloadManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DownloadFragment extends Fragment {

    private FragmentDownloadBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        final InitialActivity activity = (InitialActivity) requireActivity();
        binding = FragmentDownloadBinding.inflate(inflater, container, false);

        final Handler handler = new Handler(Looper.getMainLooper());
        final DownloadManager downloadManager = new DownloadManager();

        assert getArguments() != null;
        final Map<String, String> mods = (Map<String, String>) getArguments().getSerializable("mods");
        final List<String> installedMods = new ArrayList<>();

        downloadManager.installMods(activity.getExternalFilesDir("textures"), mods);
        downloadManager.setListener(new DownloadManager.DownloadListener() {
            @Override
            public void onListReceived(Map<String, String> map) {
            }

            @Override
            public void onNewModInstalled(String mod) {
                installedMods.add(mod);
                handler.post(() -> binding.setProgress(mods.size() / installedMods.size() * 100));
                if(mods.size() == installedMods.size()) {
                    final Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                Snackbar.make(binding.getRoot(), R.string.no_internet, BaseTransientBottomBar.LENGTH_LONG).show();
            }

            @Override
            public void onServerError(int e) {
                Snackbar.make(binding.getRoot(), R.string.server_error, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
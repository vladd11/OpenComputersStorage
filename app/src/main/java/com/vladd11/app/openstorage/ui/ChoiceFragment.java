package com.vladd11.app.openstorage.ui;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.internal.LinkedTreeMap;
import com.vladd11.app.openstorage.InitialActivity;
import com.vladd11.app.openstorage.MainActivity;
import com.vladd11.app.openstorage.R;
import com.vladd11.app.openstorage.databinding.FragmentChoiceBinding;
import com.vladd11.app.openstorage.downloader.DownloadManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChoiceFragment extends Fragment implements InitialActivity.ButtonInterface {
    private ModsAdapter modsAdapter;
    private Map<String, String> mods;
    private FragmentChoiceBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentChoiceBinding.inflate(inflater, container, false);
        final InitialActivity activity = (InitialActivity) requireActivity();
        final DownloadManager downloadManager = new DownloadManager();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        modsAdapter = new ModsAdapter();
        binding.recyclerView.setAdapter(modsAdapter);

        final Handler handler = new Handler(Looper.getMainLooper());
        downloadManager.setListener(new DownloadManager.DownloadListener() {
            @Override
            public void onListReceived(Map<String, String> map) {
                mods = map;
                handler.post(() -> modsAdapter.replaceAll(mods));
            }

            @Override
            public void onNewModInstalled(String mod) {

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
        downloadManager.getModsList();

        activity.setButtonInterface(this);
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

    @Override
    public void onDownloadButtonClicked() {
        final Bundle bundle = new Bundle();
        bundle.putSerializable("mods", modsAdapter.getSelectedMods());
        NavHostFragment.findNavController(this).navigate(R.id.action_ChoiceFragment_to_DownloadFragment, bundle);
    }
}
package com.vladd11.app.openstorage.downloader;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class DownloadManager {
    private final OkHttpClient client = new OkHttpClient();
    private DownloadListener listener;

    public void getModsList() {
        client.newCall(
                new Request.Builder().url("https://storage.yandexcloud.net/textures/mods.json")
                        .addHeader("Host", "storage.yandexcloud.net").build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            listener.onServerError(response.code());
                            return;
                        }

                        listener.onListReceived(new Gson().fromJson(Objects.requireNonNull(response.body()).string(), Map.class));
                    }
                });
    }

    public void installMods(File installationDir, Map<String, String> mods) {
        client.newCall(new Request.Builder()
                .url("https://storage.yandexcloud.net/textures/exclusions.json")
                .addHeader("Host", "storage.yandexcloud.net").build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            listener.onServerError(response.code());
                            return;
                        }

                        final BufferedSink sink = Okio.buffer(Okio.sink(new File(installationDir, "exclusions.json")));
                        sink.writeAll(Objects.requireNonNull(response.body()).source());
                        sink.close();
                    }
                });

        for (Map.Entry<String, String> mod : mods.entrySet()) {
            client.newCall(new Request.Builder()
                    .url("https://storage.yandexcloud.net/textures/" + mod.getValue().toLowerCase().replace(' ', '_') + ".zip")
                    .addHeader("Host", "storage.yandexcloud.net").build())
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            listener.onFailure(e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            if (!response.isSuccessful()) {
                                listener.onServerError(response.code());
                                return;
                            }

                            final File file = new File(installationDir, mod.getValue());
                            file.mkdir();

                            unzip(Objects.requireNonNull(response.body()).byteStream(), file);
                            listener.onNewModInstalled(mod.getKey());
                        }
                    });
        }
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }


    public interface DownloadListener {
        void onListReceived(Map<String, String> list);

        void onNewModInstalled(String mod);

        void onFailure(IOException e);

        void onServerError(int e);
    }

    private static void unzip(InputStream is, File output) {
        ZipInputStream zis;
        try {
            String filename;
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(output, filename);
                    //noinspection ResultOfMethodCallIgnored
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(new File(output, filename));

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

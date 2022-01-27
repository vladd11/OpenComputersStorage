package com.vladd11.app.openstorage;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TextureAtlas {
    public final Context context;
    public final Map<String, Drawable> textures = new HashMap<>();
    public List<String> exclusions = new ArrayList<>();

    public TextureAtlas(Context context) {
        this.context = context;
        try {
            final InputStream inputStream = context.getAssets().open("exclusions.json");
            final int size = inputStream.available();
            final byte[] byteArray = new byte[size];
            // Already in byte array.
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(byteArray);
            inputStream.close();

            exclusions = new Gson().fromJson(new String(byteArray, StandardCharsets.UTF_8), TextureConfig.class).id;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class TextureConfig {
        public List<String> id;
    }

    public Drawable loadTexture(String itemId, String itemTitle) throws IOException {
        Drawable drawable = textures.get(itemTitle);
        if (drawable == null) {
            if (exclusions.contains(itemId)) {
                drawable = loadTextureFromAssets(itemId.replace(':', '/'));
            } else {
                drawable = loadTextureFromAssets(itemId.split(":")[0] +
                        '/' +
                        itemTitle.trim()
                                .replace(' ', '_')
                                .replace('/', '_')
                                .replace('(', '_')
                                .replace(')', '_').toLowerCase());
            }

            textures.put(itemTitle, drawable);
        }
        return drawable;
    }

    private Drawable loadTextureFromAssets(String textureName) {
        try {
            return Drawable.createFromStream(context.getAssets().open(textureName + ".png"), null);
        } catch (IOException ignored) { // Cause file not exist
            return null;
        }
    }
}

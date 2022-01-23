package com.vladd11.app.openstorage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TextureAtlas {
    public final Context context;
    public Map<String, Drawable> textures = new HashMap<>();

    public TextureAtlas(Context context) {
        this.context = context;
    }

    public Drawable loadTexture(String textureName) throws IOException {
        Drawable drawable = textures.get(textureName);
        if(drawable == null) {
            drawable = loadTextureFromAssets(textureName);
            textures.put(textureName, drawable);
        }
        return drawable;
    }

    private Drawable loadTextureFromAssets(String textureName) throws IOException {
        final String[] spl = textureName.split(Pattern.quote(":"));
        final String path = spl[0] + '/' + spl[1] + ".png";
        if(new File(path).exists()) {
            return Drawable.createFromStream(context.getAssets().open(path), null);
        }
        return null;
    }
}

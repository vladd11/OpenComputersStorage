package com.vladd11.app.openstorage;

import android.graphics.drawable.Drawable;

public class Item {
    public final String name;
    public final Drawable image;
    public final int count;
    public final Chest chest;
    public final String label;
    public final int position;

    public Item(String name, Drawable image, int count, Chest chest, String label, int position) {
        this.name = name;
        this.image = image;
        this.count = count;
        this.chest = chest;
        this.label = label;
        this.position = position;
    }
}

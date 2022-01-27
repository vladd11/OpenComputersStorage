package com.vladd11.app.openstorage.utils;

import android.graphics.drawable.Drawable;

import java.util.Objects;

public class Item {
    public final String name;
    public final Drawable image;
    public int count;
    public final Chest chest;
    public final String title;
    public final int position;

    public Item(String name, Drawable image, int count, Chest chest, String title, int position) {
        this.name = name;
        this.image = image;
        this.count = count;
        this.chest = chest;
        this.title = title;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return count == item.count && position == item.position && name.equals(item.name) && Objects.equals(chest, item.chest) && title.equals(item.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, count, chest, title, position);
    }
}
